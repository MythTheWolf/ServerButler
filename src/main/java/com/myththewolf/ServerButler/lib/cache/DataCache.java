package com.myththewolf.ServerButler.lib.cache;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
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
    public static HashMap<String, ChatAnnoucement> annoucementHashMap = new HashMap<>();
    private static HashMap<String, PlayerInetAddress> ipHashMap = new HashMap<>();
    /**
     * Gets a player from cache if present, but makes a new player object
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
        }
        return playerHashMap.get(UUID);
    }

    /**
     * This inserts a player into the database
     *
     * @param UUID The UUID of the player to create
     * @param name The name
     * @return The created player
     * @note This does not check for valid UUIDs, so do not pass possibly invalid UUIDs.
     */
    public static MythPlayer createPlayer(String UUID, String name) {
        if (ConfigProperties.DEBUG) {
            getLogger().info("Player doesn't exist in database. Inserting w/ name.");
        }
        MythPlayer MP = new IMythPlayer(new DateTime(), UUID, name);
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
        playerHashMap.put(UUID, MP);
        return MP;
    }

    /**
     * Instantiates the cache maps
     */
    public static void makeMaps() {
        playerHashMap = new HashMap<>();
        channelHashMap = new HashMap<>();
        annoucementHashMap = new HashMap<>();
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
        return channelHashMap.values().stream()
                .filter(chatChannel -> chatChannel.getName().equals(name)).findFirst();
    }

    /**
     * Empties the current cached channel list and re-populates it by a database selection
     */
    public static void rebuildChannelList() {
        channelHashMap.clear();
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Channels`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                channelHashMap.put(rs.getString("ID"), new ChatChannel(rs.getString("ID")));
            }
            boolean adminCExist = channelHashMap.values().stream()
                    .anyMatch(chatChannel -> chatChannel.getName().equals("ADMIN"));
            boolean globalCExist = channelHashMap.values().stream()
                    .anyMatch(chatChannel -> chatChannel.getName().equals("GLOBAL"));

            if (!adminCExist) {
                makeAdminChatChannel();
                rebuildChannelList();
                return;
            }
            if (!globalCExist) {
                makeGlobalChatChannel();
                rebuildChannelList();
                return;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        ChatChannel admin = new ChatChannel("ADMIN", ConfigProperties.ADMIN_CHAT_PERMISSION, "#", pre, ConfigProperties.DEFAULT_CHAT_PATTERN);
        admin.update();
        return admin;
    }

    private static ChatChannel makeGlobalChatChannel() {
        String pre = "";
        ChatChannel global = new ChatChannel("GLOBAL", null, "@", pre, ConfigProperties.DEFAULT_CHAT_PATTERN);
        global.update();
        return global;
    }

    /**
     * Pulls the hard-coded admin chat channel from cache
     *
     * @return The cached Admin chat channel
     */

    public static ChatChannel getPunishmentInfoChannel() {
        Optional<ChatChannel> optionalChatChannel = DataCache
                .getOrMakeChannel(ConfigProperties.PUNISHMENT_INFO_CHANNEL);
        return optionalChatChannel.orElseGet(DataCache::getAdminChannel);
    }

    public static ChatChannel getAdminChannel() {
        return getOrMakeChannel("ADMIN").get();
    }


    public static void rebuildTaskList() {
        List<ChatAnnoucement> runnning = DataCache.annoucementHashMap.values().stream()
                .filter(ChatAnnoucement::isRunning).collect(Collectors.toList());
        runnning.forEach(ChatAnnoucement::stopTask);
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Announcements`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                annoucementHashMap.put(rs.getString("ID"), new ChatAnnoucement(rs.getString("ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        runnning.stream().map(ChatAnnoucement::getId).map(annoucementHashMap::get).forEach(ChatAnnoucement::startTask);
        runnning.clear();
    }

    public static Optional<ChatAnnoucement> getAnnouncement(String ID) {
        return Optional.ofNullable(annoucementHashMap.get(ID));
    }

    public static ChatChannel getGlobalChannel() {
        return getOrMakeChannel("GLOBAL").get();
    }

    /**
     * Gets a player by their name
     *
     * @param name The player name
     * @return A optional, empty if no player by that name exists in the database
     * @apiNote This performs a database selection to convert their name into a UUID, but pulls their MythPlayer object from cache (or creates it if not present)
     */
    public static Optional<MythPlayer> getPlayerByName(String name) {
        try {
            PreparedStatement ps = ServerButler.connector.getConnection().prepareStatement("SELECT * FROM `SB_Players` WHERE `name` = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.ofNullable(getOrMakePlayer(rs.getString("UUID")));
            }
        } catch (SQLException E) {
            E.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Overload method of {@link DataCache#rebuildChannel(String)}
     *
     * @param rebuild The channel to rebuild
     */
    public static void rebuildChannel(ChatChannel rebuild) {

        if (rebuild == null) {
            getLogger().warning("Cannot rebuild NULL channel!");
        }
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

    public static Optional<PlayerInetAddress> getOrMakeInetAddress(InetAddress src) {
        return getOrMakeInetAddress(src.toString());
    }

    private static Optional<PlayerInetAddress> playerInetAddressFor(String ID) {
        PlayerInetAddress pp = new PlayerInetAddress(ID);
        return pp.exists() ? Optional.ofNullable(pp) : Optional.empty();
    }

    public static Optional<PlayerInetAddress> getPlayerInetAddressByIp(String IP) {
        Optional<PlayerInetAddress> cache = ipHashMap.isEmpty() ? Optional.empty() : ipHashMap.entrySet().stream()
                .map(Map.Entry::getValue).filter(add -> add.getAddress().toString().equals(IP)).findAny();
        if (cache.isPresent()) {
            return cache;
        }
        try {
            String SQL = "SELECT * FROM `SB_IPAddresses` WHERE `address` = ?";
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement(SQL);
            ps.setString(1, IP);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return Optional.empty();
            }
            PlayerInetAddress address = new PlayerInetAddress(rs.getString("ID"));
            ipHashMap.put(address.getDatabaseId(), address);
            return Optional.of(address);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static void addNewInetAddress(InetAddress addr, MythPlayer player) {
        PlayerInetAddress address = new PlayerInetAddress(addr, player);
        address.update();
        ipHashMap.put(addr.toString(), address);
    }

    public static void rebuildPlayer(String UUID) {
        playerHashMap.put(UUID, new IMythPlayer(UUID));

    }

    public static void rebuildPlayerInetAddress(PlayerInetAddress src) {
        String dbId = src.getDatabaseId();
        ipHashMap.put(dbId, new PlayerInetAddress(dbId));
    }

    public static boolean playerExists(String UUID) {
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Players` WHERE `UUID` = ?");
            ps.setString(1, UUID);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Optional<MythPlayer> getPlayerByDiscordID(String ID) {
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Players` WHERE `discordID` = ?");
            ps.setString(1, ID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(getOrMakePlayer(rs.getString("UUID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static void debug(String out) {
        if (ConfigProperties.DEBUG) {
            getLogger().info(out);
        }
    }

    /*public static HashMap<String, String> getPlayerNameMap() {
        return new HashMap<>(playerNameMap);
    }
    */
}
