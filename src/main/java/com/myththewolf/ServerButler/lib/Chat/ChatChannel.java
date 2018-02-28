package com.myththewolf.ServerButler.lib.Chat;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents a Chat channnel
 */
public class ChatChannel implements SQLAble {
    /**
     * The name of this channel
     */
    private String name;
    /**
     * The permission node to read/write to this channel
     */
    private String permission;
    /**
     * The ID in the database of this channel
     */
    private String ID;
    /**
     * The shortcut that can be used for this channel
     */
    private String shortcut;
    /**
     * The chat prefix of this channel
     */
    private String prefix;

    /**
     * Constructs a new Chat Channel, pulling data from the database
     *
     * @param ID The ID of the channel to pull data from
     */
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

    /**
     * Constructs a new Chat Channel, assuming there is no channel with these paramaters in the database
     *
     * @param name     The channel name
     * @param perm     The channel permission node, null if none
     * @param shortcut The shortcut for this channel, null if none
     * @param prefix   The chat prefix for this channel
     */
    public ChatChannel(String name, String perm, String shortcut, String prefix) {
        this.ID = null;
        this.name = name;
        this.permission = perm;
        this.shortcut = shortcut;
        this.prefix = prefix;
    }

    /**
     * Gets a list of all cached players who are viewing this channel
     *
     * @return The list of players
     */
    public List<MythPlayer> getAllCachedPlayers() {
        return DataCache.playerHashMap.entrySet().stream().map(Map.Entry::getValue)
                .filter(mp -> mp.getChannelList().contains(this)).collect(Collectors.toList());
    }

    /**
     * Gets a list of all players who are writing to this channel
     *
     * @return The list of authors
     */
    public List<MythPlayer> getAuthors() {
        return getAllCachedPlayers().stream().filter(player -> player.getWritingChannel().equals(this))
                .collect(Collectors.toList());
    }

    /**
     * Gets the database ID of this channel
     *
     * @return The ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Gets the chat prefix of this channel
     *
     * @return The chat prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the name of this channel
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the permission node of this channel
     *
     * @return A optional, empty if no permission node is specified
     */
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    /**
     * Gets the shortcut for this channel
     *
     * @return A optional, empty if no shortcut is specified
     */
    public Optional<String> getShortcut() {
        return Optional.ofNullable(shortcut);
    }

    /**
     * Sends a message to all players viewing this channel
     *
     * @param content The message to send
     * @param player  The player who is sending the message
     * @apiNote If player is null, the message being sent will be treated as a raw message, where the channel prefix and player name will not be included.
     */
    public void push(String content, MythPlayer player) {
        getAllCachedPlayers().forEach(p -> p.getBukkitPlayer()
                .ifPresent(p2 -> p2.sendMessage(getPrefix() + player.getName() + ": " + content)));
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ChatChannel) && (((ChatChannel) o).getID().equals(getID()));
    }

    /**
     * Updates channel entry with the data from this class <br />
     *
     * @apiNote This method will INSERT into the database if {@link ChatChannel#getID()} is null <br /> Additionally, if INSERTing, this method will auto re-build the channel cache
     */
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
