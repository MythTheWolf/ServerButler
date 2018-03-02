package com.myththewolf.ServerButler.lib.moderation.impl;

import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * This class represents a kick history entry
 */
public class ActionUserKick implements ModerationAction, SQLAble {
    /**
     * The target user
     */
    private MythPlayer target;
    /**
     * The reason for the kick
     */
    private String reason;
    /**
     * The moderator who issued this action (or NULL if from the console)
     */
    private MythPlayer moderator;
    /**
     * The time and date og when this action was applied
     */
    private DateTime dateApplied;
    /**
     * The ID of the database
     */
    private String ID;

    /**
     * Constructs a new ActionUserKick, pulling data from the Database
     *
     * @param ID The ID of the entry of the database to pull data from
     */
    public ActionUserKick(String ID) {
        this.ID = ID;
        String SQL = "SELECT * FROM `SB_Actions` WHERE `ID` = ?";
        ResultSet resultSet = prepareAndExecuteSelectExceptionally(SQL, 1, ID);
        try {
            while (resultSet.next()) {
                this.dateApplied = TimeUtils.timeFromString(resultSet.getString("dateApplied"));
                this.reason = resultSet.getString("reason");
                this.moderator = resultSet.getString("moderator") == null ? null : DataCache
                        .getOrMakePlayer(resultSet.getString("moderator"));
                this.target = DataCache.getOrMakePlayer(resultSet.getString("target"));
            }
        } catch (SQLException ex) {
            handleException(ex);
            return;
        }
    }

    /**
     * Constructs a new ActionUserKick such that no entry in the database exists with these parameters
     *
     * @param reason    The reason of the ban
     * @param target    The target player
     * @param moderator The moderator, or null if from the console
     */
    public ActionUserKick(String reason, MythPlayer target, MythPlayer moderator) {
        this.target = target;
        this.moderator = moderator;
        this.reason = reason;
        this.dateApplied = new DateTime();
    }

    @Override
    public String getReason() {
        return reason == null ? ConfigProperties.DEFAULT_KICK_REASON : reason;
    }

    @Override
    public Optional<MythPlayer> getModeratorUser() {
        return Optional.ofNullable(moderator);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.KICK;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BUKKIT_PLAYER;
    }

    @Override
    public String getDatabaseID() {
        return ID;
    }

    @Override
    public DateTime getDateApplied() {
        return dateApplied;
    }

    @Override
    public Optional<MythPlayer> getTargetUser() {
        return Optional.ofNullable(target);
    }

    public void update() {
        if (getDatabaseID() == null) {
            String SQL = "INSERT INTO `SB_Actions` (`type`,`reason`,`target`,`targetType`,`moderator`,`dateApplied`) VALUES (?,?,?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 6, getActionType(), getReason(), getTargetUser().get()
                    .getUUID(), getTargetType(), getModeratorUser()
                    .map(MythPlayer::getUUID).orElse(null), TimeUtils.dateToString(getDateApplied()));
        } else {
            String SQL = "UPDATE `SB_Actions` SET `type` = ?, `reason` = ?, `target` = ?, `targetType` = ?,`moderator` = ? WHERE `ID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 7, getActionType(), getReason(), getTargetUser().get()
                    .getUUID(), getTargetType(), getModeratorUser().map(MythPlayer::getUUID).orElse(null), TimeUtils
                    .dateToString(getDateApplied()), getDatabaseID());
        }
    }
}
