package com.myththewolf.ServerButler.lib.player.impl;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetBan;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetPardon;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetTempBan;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerInetAddress implements SQLAble {
    private InetAddress address;
    private LoginStatus loginStatus;
    private List<String> mappedPlayers = new ArrayList<>();
    private String databaseId;
    private DateTime joinDate;

    public PlayerInetAddress(String databaseId) {
        String SQL = "SELECT * FROM `SB_IPAddresses` WHERE `ID` = ?";
        try {
            ResultSet rs = prepareAndExecuteSelectThrow(SQL, 1, databaseId);
            while (rs.next()) {
                mappedPlayers.addAll(StringUtils.deserializeArray(rs.getString("playerUUIDs")));
                loginStatus = LoginStatus.valueOf(rs.getString("loginStatus"));
                joinDate = TimeUtils.timeFromString(rs.getString("dateJoined"));
                this.databaseId = databaseId;
                try {
                    address = InetAddress.getByName(rs.getString("address").substring(1));
                } catch (Exception e) {
                    handleException(e);
                }
            }
        } catch (SQLException e) {
            handleExceptionPST(e);
            return;
        }

    }

    public PlayerInetAddress(InetAddress addr, MythPlayer start) {
        address = addr;
        mappedPlayers.add(start.getUUID());
        joinDate = new DateTime();
        loginStatus = LoginStatus.PERMITTED;
    }

    public void update() {
        if (databaseId == null) {
            String SQL = "INSERT INTO `SB_IPAddresses` (`address`,`playerUUIDs`,`loginStatus`,`dateJoined`) VALUES (?,?,?,?)";
            try {
                PreparedStatement statement = getSQLConnection().prepareStatement(SQL,
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, address.toString());
                statement.setString(2, StringUtils
                        .serializeArray(mappedPlayers));
                statement.setString(3, LoginStatus.PERMITTED.toString());
                statement.setString(4, TimeUtils.dateToString(new DateTime()));
                statement.executeUpdate();
                ResultSet genKeys = statement.getGeneratedKeys();
                if (genKeys.next()) {
                    this.databaseId = genKeys.getString(1);
                    return;
                }
            } catch (SQLException e) {
                handleExceptionPST(e);
                return;
            }
        } else {
            String SQL = "UPDATE `SB_IPAddresses` SET `playerUUIDs` = ?,`loginStatus` = ?,`dateJoined` =? WHERE `ID` = ? ";
            prepareAndExecuteUpdateExceptionally(SQL, 4, StringUtils
                    .serializeArray(mappedPlayers), getLoginStatus().toString(), TimeUtils
                    .dateToString(getJoinDate()), getDatabaseId());
        }
    }

    public InetAddress getAddress() {
        return address;
    }

    public List<MythPlayer> getMappedPlayers() {
        return mappedPlayers.stream().map(s -> {
            getLogger().info("Mapping player: "+s);
            return DataCache.getPlayer(s);
        }).map(mythPlayer -> mythPlayer.orElseThrow(IllegalStateException::new)).collect(Collectors.toList());
    }

    public void addPlayer(MythPlayer mythPlayer) {
        mappedPlayers.add(mythPlayer.getUUID());
    }
    public List<ModerationAction> getInetAddressHistory() {
        List<ModerationAction> theList = new ArrayList<>();
        ResultSet history = prepareAndExecuteSelectExceptionally("SELECT * FROM `SB_Actions` WHERE `targetType` = ? AND `target` = ? ORDER BY `ID` DESC", 2, TargetType.IP_ADDRESS, getAddress()
                .toString());
        try {
            while (history.next()) {
                switch (ActionType.valueOf(history.getString("type"))) {
                    case BAN:
                        theList.add(new ActionInetBan(history.getString("ID")));
                        break;
                    case TEMP_BAN:
                        theList.add(new ActionInetTempBan(history.getString("ID")));
                        break;
                    case PARDON:
                        theList.add(new ActionInetPardon(history.getString("ID")));
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException excInetAddressHistory) {
            handleExceptionPST(excInetAddressHistory);
        }
        return theList;
    }

    public Optional<ModerationAction> getLatestActionOfType(ActionType type) {
        return getInetAddressHistory().stream()
                .filter(moderationAction -> moderationAction.getActionType().equals(type)).findFirst();
    }

    public DateTime getJoinDate() {
        return joinDate;
    }

    public LoginStatus getLoginStatus() {
        return this.loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public boolean exists() {
        return databaseId != null;
    }

    @Override
    public String toString() {
        return getAddress().toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof PlayerInetAddress && ((PlayerInetAddress) o).getDatabaseId().equals(getDatabaseId()));
    }
}
