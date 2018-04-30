package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetPardon;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

/**
 * This class captures all join events
 */
public class EPlayerJoin implements Listener, Loggable {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MythPlayer MP = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        Optional<PlayerInetAddress> ipAddress = DataCache
                .getPlayerInetAddressByIp(event.getPlayer().getAddress().getAddress().toString());
        if (!ipAddress.isPresent()) {
            DataCache.addNewInetAddress(event.getPlayer().getAddress().getAddress(), MP);
            DataCache.rebuildPlayer(event.getPlayer().getUniqueId().toString());
            MP = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        }
        if (MP.getLoginStatus() != LoginStatus.PERMITTED) {
            if (MP.getLoginStatus() == LoginStatus.BANNED) {
                ModerationAction action = MP.getLatestActionOfType(ActionType.BAN).orElse(null);
                if (action == null) {
                    MP.kickPlayer("You have been banned from the server", null);
                    return;
                }
                String kickReason = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_BAN, action.getReason(), (action
                                .getModeratorUser().isPresent() ? action.getModeratorUser().get()
                                .getName() : "CONSOLE"));
                MP.kickPlayerRaw(kickReason);
                return;
            } else if (MP.getLoginStatus() == LoginStatus.TEMP_BANNED) {
                ModerationAction moderationAction = MP.getLatestActionOfType(ActionType.TEMP_BAN).orElse(null);
                if (moderationAction == null) {
                    MP.kickPlayer("You have been temp banned from the server", null);
                    return;
                }
                if (moderationAction.getExpireDate().get().isBeforeNow()) {
                    MP.pardonPlayer(null, "The tempban has expired.");
                    return;
                }
                String REASON = moderationAction.getReason();
                String MOD_NAME = moderationAction.getModeratorUser().map(MythPlayer::getName).orElse("CONSOLE");
                String EXPIRE = moderationAction.getExpireDate().map(TimeUtils::dateToString).orElse("[error]");
                MP.kickPlayerRaw(StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_TEMPBAN, MOD_NAME, REASON, EXPIRE));
                return;
            }
            MP.getChannelList().stream().map(ChatChannel::getID).forEach(DataCache::rebuildChannel);
            return;
        }
        try {
            if (!MP.getConnectionAddress().get().getLoginStatus().equals(LoginStatus.PERMITTED)) {
                switch (MP.getConnectionAddress().get().getLoginStatus()) {
                    case PERMITTED:
                        break;
                    default:
                        break;
                    case BANNED:
                        PlayerInetAddress playerInetAddress = MP.getConnectionAddress()
                                .orElseThrow(IllegalStateException::new);
                        String reason = playerInetAddress.getLatestActionOfType(ActionType.BAN)
                                .orElseThrow(IllegalStateException::new).getReason();
                        Optional<MythPlayer> sender = playerInetAddress.getLatestActionOfType(ActionType.BAN)
                                .orElseThrow(IllegalStateException::new).getModeratorUser();
                        String KICK_REASON = StringUtils
                                .replaceParameters(ConfigProperties.FORMAT_IP_BAN, playerInetAddress.getAddress()
                                        .toString(), sender
                                        .map(MythPlayer::getName).orElse("CONSOLE"), reason);
                        MP.kickPlayerRaw(KICK_REASON);
                        DataCache.getAdminChannel()
                                .push(ConfigProperties.PREFIX + ChatColor.RED + "Rejected connection for player '" + MP
                                        .getName() + "' because their IP," + MP.getConnectionAddress()
                                        .orElseThrow(IllegalStateException::new).getAddress()
                                        .toString() + ", is banned.", null);
                        break;
                    case TEMP_BANNED:
                        PlayerInetAddress src = MP.getConnectionAddress().orElseThrow(IllegalStateException::new);
                        ModerationAction action = src
                                .getLatestActionOfType(ActionType.TEMP_BAN).orElseThrow(IllegalStateException::new);
                        if (action.getExpireDate().orElseThrow(IllegalStateException::new).isBeforeNow()) {
                            ModerationAction actionUnbanIp = new ActionInetPardon("The IP's tempban has expired.", src, null);
                            ((ActionInetPardon) actionUnbanIp).update();
                            src.setLoginStatus(LoginStatus.PERMITTED);
                            src.update();
                            DataCache.rebuildPlayerInetAddress(src);
                            DataCache.rebuildPlayer(MP.getUUID());
                            return;
                        }
                        KICK_REASON = StringUtils
                                .replaceParameters(ConfigProperties.FORMAT_IP_TEMPBAN, src.getAddress()
                                        .toString(), action.getModeratorUser().map(MythPlayer::getName)
                                        .orElse("CONSOLE"), action.getReason(), TimeUtils
                                        .dateToString(action.getExpireDate().orElseThrow(IllegalStateException::new)));
                        MP.kickPlayerRaw(KICK_REASON);
                        DataCache.getAdminChannel()
                                .push(ConfigProperties.PREFIX + ChatColor.RED + "Rejected connection for player '" + MP
                                        .getName() + "' because their IP," + MP.getConnectionAddress()
                                        .orElseThrow(IllegalStateException::new).getAddress()
                                        .toString() + ", is temp banned until: " + TimeUtils
                                        .dateToString(action.getExpireDate()
                                                .orElseThrow(IllegalStateException::new)), null);
                        break;
                }
                return;
            }
        } catch (Exception e) {
            event.getPlayer()
                    .kickPlayer(ConfigProperties.PREFIX + ChatColor.RED + "Internal error while accepting player connection event: " + e
                            .getMessage());
        }
    }
}
