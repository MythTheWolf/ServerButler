package com.myththewolf.ServerButler.lib.moderation.impl;

import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ActionUserBan implements ModerationAction, SQLAble {
    /**
     * The target player
     */
    private MythPlayer target;
    /**
     * The reason
     */
    private String reason;
    /**
     * The [Maybe] moderator
     */
    private MythPlayer moderator;

    private String DB_ID = null;

    private DateTime dateApplied;

    public ActionUserBan(String id) {
        DB_ID = id;
        String SQL = "SELECT * FROM `SB_Actions` WHERE `ID` = ?";
        ResultSet resultSet = prepareAndExecuteSelectExceptionally(SQL, 1, DB_ID);
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

    public ActionUserBan(String reason, MythPlayer target, MythPlayer moderator) {
        this.target = target;
        this.moderator = moderator;
        this.reason = reason;
        this.dateApplied = new DateTime();
    }

    @Override
    public String getReason() {
        return (reason != null ? reason : ConfigProperties.DEFAULT_BAN_REASON);
    }

    @Override
    public Optional<MythPlayer> getTargetUser() {
        return Optional.ofNullable(target);
    }

    @Override
    public Optional<MythPlayer> getModeratorUser() {
        return Optional.ofNullable(moderator);
    }

    @Override
    public Optional<String> getTargetIP() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getExpireDateString() {
        return Optional.empty();
    }

    @Override
    public ActionType getActionType() {
        return ActionType.BAN;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.BUKKIT_PLAYER;
    }

    public void update() {
        if (DB_ID == null) {
            String SQL = "INSERT INTO `SB_Actions` (`type`, `reason`, `target`,`moderator`,`targetType`,`dateApplied`) VALUES (?,?,?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 6, ActionType.BAN, reason, getTargetUser().get()
                    .getUUID(), getModeratorUser().map(MythPlayer::getUUID).orElse(null), TargetType.BUKKIT_PLAYER
                    .toString(), TimeUtils.dateToString(dateApplied));
        } else {
            String SQL = "UPDATE `SB_Actions` SET `type` = ?, `reason` = ?, `target` = ?, `moderator` = ?, `targetType` = ?, `dateApplied` = ? WHERE `ID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 7, ActionType.BAN, reason, getTargetUser().get()
                    .getUUID(), getModeratorUser().map(MythPlayer::getUUID).orElse(null), TargetType.BUKKIT_PLAYER
                    .toString(), Integer.parseInt(this.DB_ID), TimeUtils
                    .dateToString(dateApplied));
        }
    }

    @Override
    public String getDatabaseID() {
        return DB_ID;
    }

    @Override
    public DateTime getDateApplied() {
        return dateApplied;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ModerationAction) && ((ModerationAction) o).getDatabaseID().equals(getDatabaseID());
    }
}
