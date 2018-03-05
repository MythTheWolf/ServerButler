package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * This class captures all join events
 */
public class Join implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        MythPlayer MP = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        if (MP.getLoginStatus() != LoginStatus.PERMITTED) {
            if (MP.getLoginStatus() == LoginStatus.BANNED) {
                ModerationAction action = MP.getLatestActionOfType(ActionType.BAN).orElse(null);
                if (action == null) {
                    MP.kickPlayer("You have been banned from the server", null);
                    return;
                }
                String kickReason = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_BAN, 2, action.getReason(), (action
                                .getModeratorUser().isPresent() ? action.getModeratorUser().get()
                                .getName() : "CONSOLE"));
                MP.kickPlayerRaw(kickReason);
                return;
            }
            MP.getChannelList().stream().map(ChatChannel::getID).forEach(DataCache::rebuildChannel);
            return;
        }
    }
}
