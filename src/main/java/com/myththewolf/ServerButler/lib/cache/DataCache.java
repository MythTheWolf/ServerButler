package com.myththewolf.ServerButler.lib.cache;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.joda.time.DateTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


public class DataCache {
    public static HashMap<String, MythPlayer> playerHashMap;
    public static List<ChatChannel> allChannels;

    public static MythPlayer getOrMakePlayer(String UUID) {
        if (playerHashMap.containsKey(UUID)) {
            return playerHashMap.get(UUID);
        }
        MythPlayer player = makeNewPlayerObj(UUID);
        if (player.Exists()) {
            playerHashMap.put(UUID, player);
        } else {
            player = createPlayer(UUID);
            playerHashMap.put(UUID, player);
        }
        return playerHashMap.get(UUID);
    }

    private static MythPlayer createPlayer(String UUID) {
        if (ConfigProperties.DEBUG) {
            getLogger().info("Player doesn't exist in database. Inserting.");
        }
        MythPlayer MP = new MythPlayer(new DateTime(), UUID);
        playerHashMap.put(UUID, MP);
        return MP;
    }

    private static MythPlayer makeNewPlayerObj(String UUID) {
        if (ConfigProperties.DEBUG) {
            getLogger().info("Player doesn't exist in cache. Creating.");
        }
        MythPlayer MP = new MythPlayer(UUID);
        playerHashMap.put(UUID, MP);
        return MP;
    }

    public static void makeMaps() {
        playerHashMap = new HashMap<>();
        allChannels = new ArrayList<>();
    }

    public static Optional<ChatChannel> getOrMakeChannel(int ID) {
        getLogger().info((allChannels  == null) + "" + ID);
        return allChannels.stream().filter(chatChannel -> chatChannel.getID().equals(Integer.toString(ID))).findFirst();
    }

    public static Optional<ChatChannel> getOrMakeChannel(String name) {
        return allChannels.stream().filter(chatChannel -> chatChannel.getName().equals(name)).findFirst();
    }

    public static void rebuildChannel(ChatChannel channel) {
        rebuildChannel(channel.getID());
    }

    public static void rebuildChannelList() {
        allChannels = new ArrayList<>();
        try {
            PreparedStatement ps = ServerButler.connector.getConnection()
                    .prepareStatement("SELECT * FROM `SB_Channels`");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                allChannels.add(new ChatChannel(rs.getString("ID")));
            }
            allChannels.add(getAdminChatChannel());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void rebuildChannel(String ID) {
        ChatChannel newChan = new ChatChannel(ID);
        allChannels.remove(newChan);
        allChannels.add(newChan);
    }

    private static Logger getLogger() {
        return Bukkit.getPluginManager().getPlugin("ServerButler").getLogger();
    }

    private static ChatChannel getAdminChatChannel() {
        String pre = ChatColor.GRAY + "[" + ChatColor.RED + "#STAFF" + ChatColor.GRAY + "]";
        ChatChannel admin = new ChatChannel("ADMIN", ConfigProperties.ADMIN_CHAT_PERMISSION, "#", pre);
        return admin;
    }

    public static ChatChannel getAdminChannel() {
        return getOrMakeChannel("ADMIN").get();
    }

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
}
