package com.myththewolf.ServerButler.lib.cache;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.joda.time.DateTime;

import java.net.InetAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class represents all caching
 */
public class DataCache {
    /**
     * This a mapping of a player's UUID and their cached MythPlayer object
     *
     * @Note This HashMap is not populated upon creation, and contains cached items only from players whom have joined the server since uptime.
     */
    public static HashMap<String, MythPlayer> playerHashMap;
    /**
     * This is a mapping of all ChatChannels to their IDs
     *
     * @Note This list is populated by a selection of * in the SB_Channels database,so all chat channels exist in this list.
     */
    public static HashMap<String, ChatChannel> channelHashMap = new HashMap<>();

    private static HashMap<String, PlayerInetAddress> ipHashMap = new HashMap<>();

    /**
     * Gets a player from cache if presents, but makes a new player object, or inserts a player into the database if they don't
     *
     * @param UUID The UUID of which player to grab
     * @return A new MythPlayer object
     * @note This does not check for valid UUIDs, so do not pass possibly invalid UUIDs.
     */
    public static MythPlayer getOrMakePlayer(String UUID) {
        if (playerHashMap.containsKey(UUID)) {
            return playerHashMap.get(UUID);
        }
        MythPlayer player = makeNewPlayerObj(UUID);
        if (player.playerExists()) {
            playerHashMap.put(UUID, player);
        } else {
            player = createPlayer(UUID);
            playerHashMap.put(UUID, player);
        }
        return playerHashMap.get(UUID);
    }

    /**
     * This inserts a player into the database
     *
     * @param UUID The UUID of the player to create
     * @return The created player
     * @note This does not check for valid UUIDs, so do not pass possibly invalid UUIDs.
     */
    private static MythPlayer createPlayer(String UUID) {
        if (ConfigProperties.DEBUG) {
            getLogger().info("Player doesn't exist in database. Inserting.");
        }
        MythPlayer MP = new IMythPlayer(new DateTime(), UUID);
        MP.updatePlayer();
        return makeNewPlayerObj(UUID);
    }

    /**
     * Creates a new player object and inserts into cache
     *
     * @param UUID The UUID of the player
     * @return The inserted player object
     * @note This does not check for valid UUIDs, so do not pass possibly invalid UUIDs.
     */
    private static MythPlayer makeNewPlayerObj(String UUID) {
        if (ConfigProperties.DEBUG) {
            getLogger().info("Player doesn't exist in cache. Creating.");
        }
        MythPlayer MP = new IMythPlayer(UUID);
        return MP;
    }

    /**
     * Instantiates the cache maps
     */
    public static void makeMaps() {
        playerHashMap = new HashMap<>();
        channelHashMap = new HashMap<>();
    }

    /**
     * Gets a channel by its' ID
     *
     * @param ID The ID of the channel to grab
     * @return A channel Optional, empty if the channel doesn't exist in the current cache
     * @apiNote This does not perform a selection from the database. It only searches cache
     * @see @link{DataCache#rebuildChannelList()} for rebuilding the channel list
     */
    public static Optional<ChatChannel> getOrMakeChannel(int ID) {
        return channelHashMap.containsKey(Integer.toString(ID)) ? Optional
                .ofNullable(channelHashMap.get(Integer.toString(ID))) : Optional
                .empty();
    }

    /**
     * Gets a channel by it's name
     *
     * @param name The name of the channel to get
     * @return A channel Optional, empty if the channel doesn't exist in the current cache
     * @apiNote This does not perform a selection from the database. It only searches cache
     * @see @link{DataCache#rebuildChannelList()} for rebuilding the channel list
     */
    public static Optional<ChatChannel> getOrMakeChannel(String name) {
        return channelHashMap.entrySet().stream().map(Map.Entry::getValue)
                .filter(chatChannel -> chatChannel.getName().equals(name)).findFirst();
    }

    /**
     * Empties the current cached channel list and re-populates it by a database selection
     */
    public static void rebuildChannelList() {
        channelHashMap = new HashMap<>();
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Channels`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                channelHashMap.put(rs.getString("ID"), new ChatChannel(rs.getString("ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getAdminChannel();
    }

    /**
     * Converts the channelHashMap to a list
     *
     * @return The list of channels
     */
    public static List<ChatChannel> getAllChannels() {
        return channelHashMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    /**
     * Gets the plugin logger. We can't use {@link Loggable#getLogger()} because all methods here are static
     *
     * @return The logger
     */
    private static Logger getLogger() {
        return Bukkit.getPluginManager().getPlugin("ServerButler").getLogger();
    }

    /**
     * Creates a new admin ChatChannel object
     *
     * @return The Admin chat channel object
     * @apiNote The admin chat is hard-coded and will never be in the database, so the update method should never be run.
     */
    private static ChatChannel makeAdminChatChannel() {
        String pre = ChatColor.GRAY + "[" + ChatColor.RED + "#STAFF" + ChatColor.GRAY + "]";
        ChatChannel admin = new ChatChannel("ADMIN", ConfigProperties.ADMIN_CHAT_PERMISSION, "#", pre);
        return admin;
    }

    /**
     * Pulls the hard-coded admin chat channel from cache
     *
     * @return The cached Admin chat channel
     */
    public static ChatChannel getAdminChannel() {
        Optional<ChatChannel> admin = getOrMakeChannel("ADMIN");
        if (!admin.isPresent()) {
            ChatChannel c = makeAdminChatChannel();
            c.update();
            return getOrMakeChannel("ADMIN").get();
        }
        if (admin.get().getID() == null) {
            admin.get().update();
            rebuildChannelList();
            return getOrMakeChannel("ADMIN").get();
        }
        return admin.get();
    }

    /**
     * Gets a player by their name
     *
     * @param name The player name
     * @return A optional, empty if no player by that name exists in the database
     * @apiNote This performs a database selection to convert their name into a UUID, but pulls their MythPlayer object from cache (or creates it if not present)
     */
    public static Optional<MythPlayer> getPlayerByName(String name) {
        MythPlayer mythPlayer = null;
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Players` WHERE `name` = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mythPlayer = getOrMakePlayer(rs.getString("UUID"));
            }
        } catch (SQLException excep) {
            excep.printStackTrace();
        }
        return Optional.ofNullable(mythPlayer);
    }

    /**
     * Overload method of {@link DataCache#rebuildChannel(String)}
     *
     * @param rebuild The channel to rebuild
     */
    public static void rebuildChannel(ChatChannel rebuild) {
        rebuildChannel(rebuild.getID());
    }

    /**
     * Rebuilds the cache entry of a chatChannel, given the ID
     *
     * @param channelID The ID of the channel to rebuild
     */
    public static void rebuildChannel(String channelID) {
        channelHashMap.put(channelID, new ChatChannel(channelID));
    }

    public static Optional<PlayerInetAddress> getOrMakeInetAddress(String ID) {
        return ipHashMap.containsKey(ID) ? Optional.ofNullable(ipHashMap.get(ID)) : playerInetAddressFor(ID);
    }
    public static Optional<PlayerInetAddress> getOrMakeInetAddress(InetAddress src){
        return getOrMakeInetAddress(src.toString());
    }
    private static Optional<PlayerInetAddress> playerInetAddressFor(String ID) {
        PlayerInetAddress pp = new PlayerInetAddress(ID);
        return pp.exists() ? Optional.ofNullable(pp) : Optional.empty();
    }

    public static void addNewInetAddress(InetAddress addr, MythPlayer player) {
        PlayerInetAddress address = new PlayerInetAddress(addr, player);
        address.update();
        ipHashMap.put(addr.toString(), address);
    }
    public static void rebuildPlayer(String UUID){
       playerHashMap.put(UUID,new IMythPlayer(UUID));
    }
}
