package com.myththewolf.ServerButler.lib.player.interfaces;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import org.joda.time.DateTime;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInetAddress implements SQLAble {
    private InetAddress address;
    private LoginStatus loginStatus;
    private List<MythPlayer> mappedPlayers;
    private String databaseId;
    private DateTime joinDate;

    public PlayerInetAddress(String databaseId) {
        this.databaseId = databaseId;

    }

    public PlayerInetAddress(InetAddress addr, MythPlayer start) {
        address = addr;
        mappedPlayers.add(start);
        joinDate = new DateTime();
        loginStatus = LoginStatus.PERMITTED;
    }

    public void update() {
        if (databaseId == null) {
            String SQL = "INSERT INTO `SB_IPAddresses` (`address`,`playerUUIDs`,`loginStatus`,`dateJoined`) VALUES (?,?,?,?)";
            try {
                PreparedStatement statement = getSQLConnection().prepareStatement(SQL,
                        Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, address.getHostName());
                statement.setString(2, StringUtils
                        .serializeArray(mappedPlayers.stream().map(MythPlayer::getUUID).collect(Collectors.toList())));
                statement.executeUpdate();
                ResultSet genKeys = statement.getGeneratedKeys();
                if (genKeys.next()) {
                    this.databaseId = genKeys.getString("ID");
                    return;
                }
            } catch (SQLException e) {
                handleException(e);
                return;
            }
        } else {
            String SQL = "UPDATE `SB_IPAddresses` SET `address` = ? ,`playerUUIDs` = ?,`loginStatus` = ?,`dateJoined` =? ";
        }
    }

    private boolean exists() {
        return databaseId != null;
    }
}
