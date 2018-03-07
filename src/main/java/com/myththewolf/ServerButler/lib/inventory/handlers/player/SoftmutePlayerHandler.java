package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class SoftmutePlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getOrMakePlayer(data.getString("PLAYER-UUID"));
        player.getBukkitPlayer().ifPresent(player1 -> player1
                .sendMessage(ConfigProperties.PREFIX + "Please type the reason for the softmute:"));
        EPlayerChat.inputs.put(player.getUUID(), content -> {
            target.softmutePlayer(content, player);
            String toSend = StringUtils.replaceParameters(ConfigProperties.FORMAT_MUTE_CHAT, 3, player.getName(), target
                    .getName(), content);
            DataCache.getAdminChannel().push("[Softmute]" + toSend, null);
        });
    }
}