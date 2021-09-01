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
import org.json.JSONObject;

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
     * The current chat status of the player
     */
    private ChatStatus chatStatus = null;
    private DateTime joinDate;
    private String name;
    private boolean exists;
    private List<ChatChannel> channelList = new ArrayList<>();
    private ChatChannel writeTo;
    private String discordID;
    private List<String> playerAddresses = new ArrayList<>();
    private boolean probate = false;
    private String ID;
    private boolean tosAccepted;
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

    }

    public IMythPlayer(DateTime joinDate, String UUID1, String name) {
        this.joinDate = joinDate;
        this.UUID = UUID1;
        this.name = name;
        this.loginStatus = LoginStatus.PERMITTED;
        this.chatStatus = ChatStatus.PERMITTED;
        exists = false;

    }

    public IMythPlayer(String playerUUID) {
        exists = false;
        this.UUID = playerUUID;
        try {
            ResultSet RS = prepareAndExecuteSelectThrow("SELECT * FROM `SB_Players` WHERE `UUID` = ?", 1, UUID);
            while (RS.next()) {
                this.exists = true;
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
                this.probate = RS.getBoolean("probate");
                this.ID = RS.getString("ID");
                this.tosAccepted = RS.getBoolean("tos");
            }

            String SQL_2 = "SELECT * FROM `SB_IPAddresses` WHERE `playerUUIDs` LIKE ?";
            ResultSet rs = prepareAndExecuteSelectExceptionally(SQL_2, 1, "%" + getUUID() + "%");
            while (rs.next()) {
                playerAddresses.add(rs.getString("ID"));
            }
        } catch (SQLException exception) {
            handleExceptionPST(exception);
            return;
        }
    }

    @Override
    public boolean tosAccepted() {
        return tosAccepted;
    }

    @Override
    public void setTosAccepted(boolean tosAccepted) {
        this.tosAccepted = tosAccepted;
    }

    @Override
    public String getDisplayName() {
        return displayName == null ? getName() : displayName;
    }

    @Override
    public boolean isProbated() {
        return probate;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof IMythPlayer) && ((IMythPlayer) o).getUUID().equals(getUUID()));
    }

    @Override
    public String getID() {
        return ID;
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
        return playerAddresses.stream().map(DataCache::getOrMakeInetAddress).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public Optional<PlayerInetAddress> getConnectionAddress() {
        if (!isOnline()) {
            return Optional.empty();
        }
        return DataCache.getPlayerInetAddressByIp(getBukkitPlayer().get().getAddress().getAddress().toString());
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
    public void setProbate(boolean probate) {
        this.probate = probate;
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

    @Override
    public JSONObject toJSON() {
        JSONObject response = new JSONObject();
        response.put("chatStaus", getChatStatus());
        response.put("loginStatus", getLoginStatus());
        response.put("discordID", getDiscordID().orElse(null));
        response.put("probated", isProbated());
        response.put("ID", getID());
        response.put("joinDate", getJoinDate());
        response.put("channelList", getChannelList().stream().map(ChatChannel::getID).collect(Collectors.toSet()));
        response.put("name", getName());
        response.put("UUID", getUUID());
        response.put("displayName", getDisplayName());
        return response;
    }
}


