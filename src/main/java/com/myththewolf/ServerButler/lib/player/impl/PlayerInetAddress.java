package com.myththewolf.ServerButler.lib.player.impl;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
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
                StringUtils.deserializeArray(rs.getString("playerUUIDs")).forEach(mappedPlayers::add);
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
            String SQL = "UPDATE `SB_IPAddresses` SET `address` = ? ,`playerUUIDs` = ?,`loginStatus` = ?,`dateJoined` =? WHERE `ID` = ? ";
            prepareAndExecuteUpdateExceptionally(SQL, 4, StringUtils
                    .serializeArray(mappedPlayers), getLoginStatus(), TimeUtils
                    .dateToString(getJoinDate()), getDatabaseId());
        }
    }

    public InetAddress getAddress() {
        return address;
    }

    public List<MythPlayer> getMappedPlayers() {
        return mappedPlayers.stream().map(DataCache::getOrMakePlayer).collect(Collectors.toList());
    }

    public DateTime getJoinDate() {
        return joinDate;
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
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
}
