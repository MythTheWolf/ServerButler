package com.myththewolf.ServerButler.lib.player.impl;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.impl.ActionUserBan;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
public class MythPlayer implements SQLAble {
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

    public MythPlayer(DateTime joinDate, String UUID1) {
        this.joinDate = joinDate;
        this.UUID = UUID1;
        this.name = Bukkit.getPlayer(java.util.UUID.fromString(UUID1)).getName();
        exists = false;
        updatePlayer();
    }

    public MythPlayer(String playerUUID) {
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
                        .map(Integer::parseInt).map(integer -> DataCache.getOrMakeChannel(integer).get())
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

    public Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(java.util.UUID.fromString(this.UUID)));
    }

    public boolean isOnline() {
        return getBukkitPlayer().isPresent();
    }

    public boolean canLogin() {
        return loginStatus.equals(LoginStatus.PERMITTED);
    }

    public boolean canChat() {
        return chatStatus.equals(ChatStatus.PERMITTED);
    }

    public String getName() {
        return name;
    }

    public String getUUID() {
        return UUID;
    }

    public List<ChatChannel> getChannelList() {
        return channelList;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof MythPlayer) && ((MythPlayer) o).getUUID().equals(getUUID()));
    }

    public void updatePlayer() {
        if (exists) {
            String SQL = "UPDATE `SB_Players` SET `loginStatus` = ?, `chatStatus` = ?, `name` = ?,`writeChannel` = ?, `channels` = ? WHERE `UUID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 6, getLoginStatus(), getChatStatus(), getName(), getWritingChannel()
                    .orElse(null), StringUtils.serializeArray(channelList.stream().map(ChatChannel::getID)
                    .collect(Collectors.toList())), getUUID());

        } else {
            String SQL = "INSERT INTO `SB_Players` (`loginStatus`, `chatStatus`, `name`,`joinDate`,`UUID`) VALUES (?,?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 5, LoginStatus.PERMITTED, ChatStatus.PERMITTED, getName(), TimeUtils
                    .dateToString(joinDate), getUUID());
            exists = true;
        }
    }

    public void banPlayer(String reason, MythPlayer moderator) {
        setLoginStatus(LoginStatus.BANNED);
        ModerationAction ban = new ActionUserBan(reason, this, moderator);
        ((ActionUserBan) ban).update();
        updatePlayer();
        getBukkitPlayer().ifPresent(player -> {
            String fin = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_BAN, 2, reason, (moderator != null ? moderator
                            .getName() : "CONSOLE"));
            player.kickPlayer(fin);
        });
    }

    public void openChannel(ChatChannel channel) {
        this.channelList.remove(channel);
        this.channelList.add(channel);
    }

    public void closeChannel(ChatChannel channel) {
        if (!getWritingChannel().isPresent() || getWritingChannel().get().equals(channel)) {
            setWritingChannel(null);
        }
        this.channelList.remove(channel);
    }

    public void kickPlayer(String reason, MythPlayer moderator) {
        String fReason = (reason == null ? ConfigProperties.DEFAULT_KICK_REASON : reason);
        String pattern = ConfigProperties.FORMAT_KICK;
        String modName = (moderator != null ? moderator.getName() : "CONSOLE");
        String formatted = StringUtils.replaceParameters(pattern, 2, modName, fReason);
        getBukkitPlayer().ifPresent(p -> p.kickPlayer(formatted));
    }

    public LoginStatus getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(LoginStatus loginStatus) {
        this.loginStatus = loginStatus;
    }

    public ChatStatus getChatStatus() {
        return chatStatus;
    }

    public void setChatStatus(ChatStatus chatStatus) {
        this.chatStatus = chatStatus;
    }

    public boolean Exists() {
        return exists;
    }

    @Override
    public String toString() {
        return getUUID();
    }

    public Optional<ModerationAction> getLatestActionOfType(ActionType type) {
        return getPlayerHistory().stream().filter(moderationAction -> moderationAction.getActionType().equals(type))
                .findFirst();
    }

    public List<ModerationAction> getPlayerHistory() {
        List<ModerationAction> his = new ArrayList<>();
        try {
            ResultSet history = prepareAndExecuteSelectExceptionally("SELECT * FROM `SB_Actions` WHERE `targetType` = ? AND `target` = ? ORDER BY `ID` DESC", 2, TargetType.BUKKIT_PLAYER, getUUID());
            while (history.next()) {
                switch (ActionType.valueOf(history.getString("type"))) {
                    case BAN:
                        his.add(new ActionUserBan(history.getString("ID")));
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            handleExceptionPST(e);
        }
        return his;
    }

    public Optional<ChatChannel> getWritingChannel() {
        return Optional.ofNullable(writeTo);
    }

    public void setWritingChannel(ChatChannel channel) {
        this.writeTo = channel;
    }

    public boolean isViewing(ChatChannel channel) {
        return getChannelList().contains(channel);
    }
}
