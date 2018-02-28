package com.myththewolf.ServerButler.lib.Chat;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChatChannel implements SQLAble {
    private String name;
    private String permission;
    private String ID;
    private String shortcut;
    private String prefix;

    public ChatChannel(String ID) {
        this.ID = ID;
        try {
            ResultSet RS = prepareAndExecuteSelectThrow("SELECT * FROM `SB_Channels` WHERE ID = ?", 1, ID);
            while (RS.next()) {
                name = RS.getString("name");
                permission = RS.getString("permission");
                shortcut = RS.getString("shortcut");
                prefix = RS.getString("prefix");
            }
        } catch (SQLException e) {
            handleExceptionPST(e);
        }
    }

    public ChatChannel(String name, String perm, String shortcut, String prefix) {
        this.ID = null;
        this.name = name;
        this.permission = perm;
        this.shortcut = shortcut;
        this.prefix = prefix;
    }

    public List<MythPlayer> getAllCachedPlayers() {
        return DataCache.playerHashMap.entrySet().stream().map(Map.Entry::getValue)
                .filter(mp -> mp.getChannelList().contains(this)).collect(Collectors.toList());
    }

    public List<MythPlayer> getAuthors() {
        return getAllCachedPlayers().stream().filter(player -> player.getWritingChannel().equals(this))
                .collect(Collectors.toList());
    }

    public String getID() {
        return ID;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public Optional<String> getShortcut() {
        return Optional.ofNullable(shortcut);
    }

    public void push(String content, IMythPlayer player) {
        getAllCachedPlayers().forEach(p -> p.getBukkitPlayer()
                .ifPresent(p2 -> p2.sendMessage(getPrefix() + player.getName() + ": " + content)));
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ChatChannel) && (((ChatChannel) o).getID().equals(getID()));
    }

    public void update() {
        if (getID() == null) {
            String SQL = "INSERT INTO `SB_Channels` (`name`,`permission`,`shortcut`,`prefix`) VALUES (?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 4, getName(), getPermission(), getShortcut(), getPrefix());
        } else {
            String SQL = "UPDATE `SB_Channels` SET `name` = ?,`permission` = ?,`shortcut` = ?, `prefix` = ? WHERE `ID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 5, getName(), getPermission(), getShortcut(), getPrefix(), getID());
        }
        DataCache.rebuildChannelList();
    }

}
