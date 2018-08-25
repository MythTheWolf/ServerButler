package com.myththewolf.ServerButler.lib.player.impl;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents a rich player, proprietary to this plugin
 */
public class IMythPlayer implements MythPlayer, Loggable {
    /**
     * The UUID of the player
     */
    private String UUID;
    /**
     * The current login status of the player
     */
    private LoginStatus loginStatus = null;
    /**
     * The current chat status ofthe player
     */
    private ChatStatus chatStatus = null;
    private DateTime joinDate;
    private String name;
    private boolean exists;
    private List<ChatChannel> channelList = new ArrayList<>();
    private ChatChannel writeTo;
    private String discordID;
    private List<PlayerInetAddress> playerAddresses = new ArrayList<>();
    /**
     * Bukkit display name
     */
    private String displayName;
    public IMythPlayer(DateTime joinDate, String UUID1) {
        this.joinDate = joinDate;
        this.UUID = UUID1;
        this.loginStatus = LoginStatus.PERMITTED;
        this.chatStatus = ChatStatus.PERMITTED;
        exists = false;
        updatePlayer();
    }

    public IMythPlayer(DateTime joinDate, String UUID1, String name) {
        this.joinDate = joinDate;
        this.UUID = UUID1;
        this.name = name;
        this.loginStatus = LoginStatus.PERMITTED;
        this.chatStatus = ChatStatus.PERMITTED;
        exists = false;
        updatePlayer();
    }

    public IMythPlayer(String playerUUID) {
        exists = false;
        this.UUID = playerUUID;
        try {
            ResultSet RS = prepareAndExecuteSelectThrow("SELECT * FROM `SB_Players` WHERE `UUID` = ?", 1, UUID);
            while (RS.next()) {
                exists = true;
                this.loginStatus = LoginStatus.valueOf(RS.getString("loginStatus"));
                this.chatStatus = ChatStatus.valueOf(RS.getString("chatStatus"));
                this.joinDate = TimeUtils.timeFromString(RS.getString("joinDate"));
                this.name = RS.getString("name");
                this.channelList = StringUtils.deserializeArray(RS.getString("channels")).stream()
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt).map(integer -> DataCache.getOrMakeChannel(integer).orElseGet(() -> {
                            getLogger().severe("Removing Reference to no-longer valid channel ID:" + integer);
                            return null;
                        })).filter(chatChannel -> !(chatChannel == null))
                        .collect(Collectors.toList());
                this.writeTo = RS.getString("writeChannel") != null ? DataCache
                        .getOrMakeChannel(RS.getInt("writeChannel")).get() : null;
                this.discordID = RS.getString("discordID");
                this.displayName = RS.getString("displayName");
            }

            String SQL_2 = "SELECT * FROM `SB_IPAddresses` WHERE `playerUUIDs` LIKE ?";
            ResultSet rs = prepareAndExecuteSelectExceptionally(SQL_2, 1, "%" + getUUID() + "%");
            while (rs.next()) {
                Optional<PlayerInetAddress> address = DataCache.getOrMakeInetAddress(rs.getString("ID"));
                if (!address.isPresent()) {
                    debug("Could not map address to object: " + rs.getString("address"));
                    return;
                }
                address.ifPresent(playerAddresses::add);
            }
        } catch (SQLException exception) {
            handleExceptionPST(exception);
            return;
        }
    }

    @Override
    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof IMythPlayer) && ((IMythPlayer) o).getUUID().equals(getUUID()));
    }

    @Override
    public String toString() {
        return getUUID();
    }


    @Override
    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    @Override
    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    @Override
    public String getUUID() {
        return UUID;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public DateTime getJoinDate() {
        return joinDate;
    }

    @Override
    public boolean playerExists() {
        return exists;
    }

    @Override
    public void setExistent(boolean existant) {
        this.exists = existant;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public List<PlayerInetAddress> getPlayerAddresses() {
        return playerAddresses;
    }

    @Override
    public Optional<PlayerInetAddress> getConnectionAddress() {
        if (!isOnline()) {
            return Optional.empty();
        }
        return playerAddresses.stream()
                .filter(a -> {
                    return getBukkitPlayer().get().getAddress().getAddress().toString()
                            .equals(a.getAddress().toString());
                }).findAny();
    }

    @Override
    public Optional<ChatChannel> getWritingChannel() {
        return Optional.ofNullable(writeTo);
    }

    @Override
    public void setWritingChannel(ChatChannel channel) {
        this.writeTo = channel;
    }

    @Override
    public List<ChatChannel> getChannelList() {
        return channelList;
    }

    @Override
    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    @Override
    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus;
    }

    @Override
    public void openChannel(ChatChannel channel) {
        this.channelList.add(channel);
        updatePlayer();
    }

    @Override
    public void closeChannel(ChatChannel channel) {
        this.channelList.remove(channel);
        updatePlayer();
    }

    @Override
    public Optional<String> getDiscordID() {
        return Optional.ofNullable(discordID);
    }

    @Override
    public void setDiscordID(String id) {
        this.discordID = id;
    }
}


