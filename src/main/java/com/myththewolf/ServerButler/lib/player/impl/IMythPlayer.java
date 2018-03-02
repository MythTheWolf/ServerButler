package com.myththewolf.ServerButler.lib.player.impl;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
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
public class IMythPlayer implements MythPlayer {
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

    public IMythPlayer(DateTime joinDate, String UUID1) {
        this.joinDate = joinDate;
        this.UUID = UUID1;
        this.name = Bukkit.getPlayer(java.util.UUID.fromString(UUID1)).getName();
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
                        .map(Integer::parseInt).map(integer -> DataCache.getOrMakeChannel(integer).orElse(null))
                        .collect(Collectors.toList());
                this.writeTo = RS.getString("writeChannel") != null ? DataCache
                        .getOrMakeChannel(RS.getInt("ID")).get() : null;
            }
            return;
        } catch (SQLException exception) {
            handleExceptionPST(exception);
            return;
        }
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
        return null;
    }

    @Override
    public void setLoginStatus(LoginStatus loginStatus) {

    }

    @Override
    public String getUUID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public DateTime getJoinDate() {
        return null;
    }

    @Override
    public boolean playerExists() {
        return false;
    }

    @Override
    public void setExistent(boolean existant) {

    }

    @Override
    public Optional<ChatChannel> getWritingChannel() {
        return Optional.empty();
    }

    @Override
    public void setWritingChannel(ChatChannel channel) {

    }

    @Override
    public List<ChatChannel> getChannelList() {
        return null;
    }

    @Override
    public ChatStatus getChatStatus() {
        return null;
    }

    @Override
    public void setChatStatus(ChatStatus chatStatus) {

    }

    @Override
    public void openChannel(ChatChannel channel) {

    }

    @Override
    public void closeChannel(ChatChannel channel) {

    }
}
