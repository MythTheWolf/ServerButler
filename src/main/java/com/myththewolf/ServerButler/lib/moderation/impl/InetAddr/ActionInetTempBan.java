package com.myththewolf.ServerButler.lib.moderation.impl.InetAddr;

import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * This class represents a ban history entry
 */
public class ActionInetTempBan implements ModerationAction, SQLAble {
    /**
     * The target {@link PlayerInetAddress}
     */
    private PlayerInetAddress target;
    /**
     * The reason
     */
    private String reason;
    /**
     * The [Maybe] moderator
     */
    private MythPlayer moderator;
    /**
     * The ID of this entry from the database, null if not currently in the database
     */
    private String DB_ID = null;
    /**
     * The Date and Time the ban was applied
     */
    private DateTime dateApplied;
    /**
     * The Date and Time the ban expires
     */
    private DateTime expireDate;

    /**
     * Constructs a new ActionUserTempBan, pulling data from the Database
     *
     * @param id The ID of the entry of the database to pull data from
     */
    public ActionInetTempBan(String id) {
        DB_ID = id;
        String SQL = "SELECT * FROM `SB_Actions` WHERE `ID` = ?";
        ResultSet resultSet = prepareAndExecuteSelectExceptionally(SQL, 1, DB_ID);
        try {
            while (resultSet.next()) {
                this.dateApplied = TimeUtils.timeFromString(resultSet.getString("dateApplied"));
                this.reason = resultSet.getString("reason");
                this.moderator = resultSet.getString("moderator") == null ? null : DataCache
                        .getOrMakePlayer(resultSet.getString("moderator"));
                this.target = DataCache.getPlayerInetAddressByIp(resultSet.getString("target"))
                        .orElseThrow(() -> new IllegalStateException("Could not find target IP address"));
                this.expireDate = TimeUtils.timeFromString(resultSet.getString("expireDate"));
            }
        } catch (SQLException ex) {
            handleException(ex);
            return;
        }
    }

    /**
     * Constructs a new ActionInetTempBan such that no entry in the database exists with these parameters
     *
     * @param reason    The reason of the ban
     * @param target    The target {@link PlayerInetAddress}
     * @param moderator The moderator, or null if from the console
     */
    public ActionInetTempBan(String reason, DateTime expireDate, PlayerInetAddress target, MythPlayer moderator) {
        this.target = target;
        this.moderator = moderator;
        this.reason = reason;
        this.dateApplied = new DateTime();
        this.expireDate = expireDate;
    }

    @Override
    public String getReason() {
        return (reason != null ? reason : ConfigProperties.DEFAULT_BAN_REASON);
    }

    @Override
    public Optional<MythPlayer> getTargetUser() {
        return Optional.empty();
    }

    @Override
    public Optional<MythPlayer> getModeratorUser() {
        return Optional.ofNullable(moderator);
    }

    @Override
    public Optional<PlayerInetAddress> getTargetIP() {
        return Optional.ofNullable(target);
    }

    @Override
    public Optional<DateTime> getExpireDate() {
        return Optional.ofNullable(expireDate);
    }

    @Override
    public ActionType getActionType() {
        return ActionType.TEMP_BAN;
    }

    @Override
    public TargetType getTargetType() {
        return TargetType.IP_ADDRESS;
    }

    /**
     * Updates (Or inserts into) the database with the information in this instance
     */
    public void update() {
        if (DB_ID == null) {
            String SQL = "INSERT INTO `SB_Actions` (`type`, `reason`, `target`,`moderator`,`targetType`,`dateApplied`,`expireDate`) VALUES (?,?,?,?,?,?,?)";
            DB_ID = Integer
                    .toString(prepareAndExecuteUpdateExceptionally(SQL, 7, getActionType(), reason, getTargetIP()
                            .get()
                            .getAddress().toString(), getModeratorUser().map(MythPlayer::getUUID)
                            .orElse(null), TargetType.IP_ADDRESS
                            .toString(), TimeUtils.dateToString(dateApplied), TimeUtils
                            .dateToString(getExpireDate().get())));
        } else {
            String SQL = "UPDATE `SB_Actions` SET `type` = ?, `reason` = ?, `target` = ?, `moderator` = ?, `targetType` = ?, `dateApplied` = ?, `expireDate` = ? WHERE `ID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 8, getActionType(), reason, getTargetIP().get().getAddress()
                    .toString(), getModeratorUser().map(MythPlayer::getUUID).orElse(null), TargetType.IP_ADDRESS
                    .toString(), getExpireDate().map(TimeUtils::dateToString).get(), TimeUtils
                    .dateToString(dateApplied), Integer.parseInt(this.DB_ID));
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
