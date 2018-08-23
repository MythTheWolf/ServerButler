package com.myththewolf.ServerButler.lib.player.interfaces;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.impl.User.*;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This interface extends all sub-interfaces to form a complete MythPlayer
 */
public interface MythPlayer extends SQLAble, ChannelViewer {
    /**
     * Gets this player as a bukkit player
     *
     * @return A Optional, empty if the player is offline
     */
    default Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(java.util.UUID.fromString(getUUID())));
    }

    /**
     * Gets a list of this player's punishment history
     *
     * @return The history, most recent first
     */
    default List<ModerationAction> getPlayerHistory() {
        List<ModerationAction> his = new ArrayList<>();
        try {
            ResultSet history = prepareAndExecuteSelectExceptionally("SELECT * FROM `SB_Actions` WHERE `targetType` = ? AND `target` = ? ORDER BY `ID` DESC", 2, TargetType.BUKKIT_PLAYER, getUUID());
            while (history.next()) {
                switch (ActionType.valueOf(history.getString("type"))) {
                    case BAN:
                        his.add(new ActionUserBan(history.getString("ID")));
                        break;
                    case KICK:
                        his.add(new ActionUserKick(history.getString("ID")));
                        break;
                    case MUTE:
                        his.add(new ActionUserMute(history.getString("ID")));
                        break;
                    case SOFT_MUTE:
                        his.add(new ActionUserSoftmute(history.getString("ID")));
                        break;
                    case PARDON:
                        his.add(new ActionUserPardon(history.getString("ID")));
                        break;
                    case UNMUTE:
                        his.add(new ActionUserUnmute(history.getString("ID")));
                        break;
                    case TEMP_BAN:
                        his.add(new ActionUserTempBan(history.getString("ID")));
                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            handleExceptionPST(e);
        }
        return his;
    }

    /**
     * Gets the latest history entry of a specified type
     *
     * @param type The type
     * @return A optional, empty if no action was found
     */
    default Optional<ModerationAction> getLatestActionOfType(ActionType type) {
        return getPlayerHistory().stream().filter(moderationAction -> moderationAction.getActionType().equals(type))
                .findFirst();
    }

    /**
     * Gets this player's login status
     *
     * @return Their login status
     */
    LoginStatus getLoginStatus();

    /**
     * Sets this player's login status
     *
     * @param loginStatus The status to set to
     */
    void setLoginStatus(LoginStatus loginStatus);

    /**
     * Gets this player's unique ID (bukkit)
     *
     * @return Their UUID
     */
    String getUUID();

    /**
     * Gets this player's in game name (IGN)
     *
     * @return Their name
     */
    String getName();

    /**
     * Gets the date that this player joined
     *
     * @return The join date
     */
    DateTime getJoinDate();

    /**
     * Checks if this player is online
     *
     * @return True if they are
     */
    default boolean isOnline() {
        return getBukkitPlayer().isPresent();
    }

    /**
     * Checks if this player is permitted to join the server
     *
     * @return True if their status permits login
     */
    default boolean canJoin() {
        return getLoginStatus().equals(LoginStatus.PERMITTED);
    }

    /**
     * Checks if this player is inserted into the database
     *
     * @return True if they are
     */
    boolean playerExists();

    /**
     * Sets this player existent value
     *
     * @param existent The boolean value
     */
    void setExistent(boolean existent);

    void setName(String name);

    Optional<String> getDiscordID();

    void setDiscordID(String id);
    /**
     * Bans this player
     *
     * @param reason    The reason who for their ban
     * @param moderator The moderator who banned, null if from CONSOLE
     */
    default void banPlayer(String reason, MythPlayer moderator) {
        setLoginStatus(LoginStatus.BANNED);
        ModerationAction ban = new ActionUserBan(reason, this, moderator);
        ((ActionUserBan) ban).update();
        updatePlayer();
    }

    /**
     * Un-Mutes this player
     *
     * @param reason    The reason who for their unmute
     * @param moderator The moderator who banned, null if from CONSOLE
     */
    default void unmutePlayer(String reason, MythPlayer moderator) {
        setChatStatus(ChatStatus.PERMITTED);
        ModerationAction mute = new ActionUserUnmute(reason, this, moderator);
        ((ActionUserUnmute) mute).update();
        updatePlayer();
        String fin = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_UNMUTE, reason, (moderator != null ? moderator
                        .getName() : "CONSOLE"));
        getBukkitPlayer().ifPresent(player -> player.sendMessage(fin));
    }

    /**
     * Mutes this player
     *
     * @param reason    The reason who for their mute
     * @param moderator The moderator who banned, null if from CONSOLE
     */
    default void mutePlayer(String reason, MythPlayer moderator) {
        setChatStatus(ChatStatus.MUTED);
        ModerationAction mute = new ActionUserMute(reason, this, moderator);
        ((ActionUserMute) mute).update();
        updatePlayer();
        String fin = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_MUTE, reason, (moderator != null ? moderator
                        .getName() : "CONSOLE"));
        getBukkitPlayer().ifPresent(player -> player.sendMessage(fin));
    }

    /**
     * Softmutes this player
     *
     * @param reason    The reason who for their ban
     * @param moderator The moderator who banned, null if from CONSOLE
     */
    default void softmutePlayer(String reason, MythPlayer moderator) {
        setChatStatus(ChatStatus.SOFTMUTED);
        ModerationAction mute = new ActionUserSoftmute(reason, this, moderator);
        ((ActionUserSoftmute) mute).update();
        updatePlayer();
    }

    /**
     * Kicks this player (if online)
     *
     * @param reason    The reason who for their ban
     * @param moderator The moderator who banned, null if from CONSOLE
     */
    default void kickPlayer(String reason, MythPlayer moderator) {
        ModerationAction kick = new ActionUserKick(reason, this, moderator);
        ((ActionUserKick) kick).update();
        String fReason = (reason == null ? ConfigProperties.DEFAULT_KICK_REASON : reason);
        String pattern = ConfigProperties.FORMAT_KICK;
        String modName = (moderator != null ? moderator.getName() : "CONSOLE");
        String formatted = StringUtils.replaceParameters(pattern, modName, fReason);
        getBukkitPlayer().ifPresent(p -> p.kickPlayer(formatted));
    }

    default void pardonPlayer(MythPlayer mod, String reason) {
        ModerationAction pardon = new ActionUserPardon(reason, this, mod);
        ((ActionUserPardon) pardon).update();
        setLoginStatus(LoginStatus.PERMITTED);
        updatePlayer();
    }

    default OfflinePlayer getOfflinePlayer(){
        return Bukkit.getOfflinePlayer(UUID.fromString(getUUID()));
    }
    default void tempbanPlayer(MythPlayer mod, String reason, DateTime expire) {
        ModerationAction tBan = new ActionUserTempBan(reason, expire, this, mod);
        ((ActionUserTempBan) tBan).update();
        setLoginStatus(LoginStatus.TEMP_BANNED);
        updatePlayer();
        getLatestActionOfType(ActionType.TEMP_BAN).ifPresent(moderationAction -> {
            String REASON = moderationAction.getReason();
            String MOD_NAME = moderationAction.getModeratorUser().map(MythPlayer::getName).orElse("CONSOLE");
            String EXPIRE = moderationAction.getExpireDate().map(TimeUtils::dateToString).orElse("[error]");
            kickPlayerRaw(StringUtils.replaceParameters(ConfigProperties.FORMAT_TEMPBAN, MOD_NAME, REASON, EXPIRE));
        });
    }

    /**
     * Updates player in database
     */
    default void updatePlayer() {
        if (playerExists()) {
            String SQL = "UPDATE `SB_Players` SET `loginStatus` = ?, `chatStatus` = ?, `name` = ?,`writeChannel` = ?, `channels` = ?, `discordID` = ? WHERE `UUID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 7, getLoginStatus(), getChatStatus(), getName(), getWritingChannel()
                    .map(ChatChannel::getID).orElse(null), StringUtils
                    .serializeArray(getChannelList().stream().map(ChatChannel::getID)
                            .collect(Collectors.toList())), getDiscordID().orElse(null), getUUID());

        }else {
            String SQL = "INSERT INTO `SB_Players` (`loginStatus`, `chatStatus`, `name`,`joinDate`,`UUID`) VALUES (?,?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 5, LoginStatus.PERMITTED, ChatStatus.PERMITTED, getName(), TimeUtils
                    .dateToString(getJoinDate()), getUUID());
            setExistent(true);
        }
        DataCache.rebuildPlayer(getUUID());
    }

    default void kickPlayerRaw(String reason) {
        getBukkitPlayer().ifPresent(player -> Bukkit.getScheduler()
                .runTask(Bukkit.getPluginManager().getPlugin("ServerButler"), () -> player.kickPlayer(reason)));
    }

    List<PlayerInetAddress> getPlayerAddresses();

    Optional<PlayerInetAddress> getConnectionAddress();

    default boolean hasPermission(String node){
        if(!getBukkitPlayer().isPresent()){
            getLogger().warning("Method 'hasPermission' called on a unknown player for node: "+node);
        }
        return getBukkitPlayer().isPresent() && getBukkitPlayer().get().hasPermission(node);
    }
}
