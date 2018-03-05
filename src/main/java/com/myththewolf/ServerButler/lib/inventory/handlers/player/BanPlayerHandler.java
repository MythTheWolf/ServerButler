package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class BanPlayerHandler implements ItemPacketHandler, Loggable {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getOrMakePlayer(data.getString("PLAYER-UUID"));
        MythPlayer moderator = DataCache.getOrMakePlayer(data.getString("MOD-UUID"));
        player.getBukkitPlayer()
                .ifPresent(p -> p.sendMessage(ConfigProperties.PREFIX + "Please enter the reason for the ban:"));
        EPlayerChat.inputs.put(player.getUUID(), content -> {
            target.banPlayer(content, moderator);
            target.updatePlayer();
            ChatChannel adminChat = DataCache.getAdminChannel();
            String message = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_BAN_CHAT, 3, moderator.getName(), target
                            .getName(), content);
            adminChat.push(message, null);
            EPlayerChat.inputs.remove(player.getUUID());
        });

    }
}
