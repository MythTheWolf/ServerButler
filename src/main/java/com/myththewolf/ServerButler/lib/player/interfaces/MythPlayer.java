package com.myththewolf.ServerButler.lib.player.interfaces;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.impl.ActionUserBan;
import com.myththewolf.ServerButler.lib.moderation.impl.ActionUserKick;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.moderation.interfaces.TargetType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface MythPlayer extends SQLAble, ChannelViewer {

    default Optional<Player> getBukkitPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(java.util.UUID.fromString(getUUID())));
    }

    default List<ModerationAction> getPlayerHistory() {
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

    default Optional<ModerationAction> getLatestActionOfType(ActionType type) {
        return getPlayerHistory().stream().filter(moderationAction -> moderationAction.getActionType().equals(type))
                .findFirst();
    }

    LoginStatus getLoginStatus();

    void setLoginStatus(LoginStatus loginStatus);

    String getUUID();

    String getName();

    DateTime getJoinDate();

    default boolean isOnline() {
        return getBukkitPlayer().isPresent();
    }

    default boolean canLogin() {
        return getLoginStatus().equals(LoginStatus.PERMITTED);
    }

    boolean playerExists();

    void setExistant(boolean existant);

    default void banPlayer(String reason, MythPlayer moderator) {
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

    default void kickPlayer(String reason, IMythPlayer moderator) {
        ModerationAction kick = new ActionUserKick(reason, this, moderator);
        ((ActionUserKick) kick).update();
        String fReason = (reason == null ? ConfigProperties.DEFAULT_KICK_REASON : reason);
        String pattern = ConfigProperties.FORMAT_KICK;
        String modName = (moderator != null ? moderator.getName() : "CONSOLE");
        String formatted = StringUtils.replaceParameters(pattern, 2, modName, fReason);
        getBukkitPlayer().ifPresent(p -> p.kickPlayer(formatted));
    }


    default void updatePlayer() {
        if (playerExists()) {
            String SQL = "UPDATE `SB_Players` SET `loginStatus` = ?, `chatStatus` = ?, `name` = ?,`writeChannel` = ?, `channels` = ? WHERE `UUID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 6, getLoginStatus(), getChatStatus(), getName(), getWritingChannel()
                    .map(ChatChannel::getID).orElse(null), StringUtils
                    .serializeArray(getChannelList().stream().map(ChatChannel::getID)
                            .collect(Collectors.toList())), getUUID());

        } else {
            String SQL = "INSERT INTO `SB_Players` (`loginStatus`, `chatStatus`, `name`,`joinDate`,`UUID`) VALUES (?,?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 5, LoginStatus.PERMITTED, ChatStatus.PERMITTED, getName(), TimeUtils
                    .dateToString(getJoinDate()), getUUID());
            setExistant(true);
        }
    }


}
