package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class MutePlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getOrMakePlayer(data.getString("PLAYER-UUID"));
        player.getBukkitPlayer().ifPresent(player1 -> player1
                .sendMessage(ConfigProperties.PREFIX + "Please type the reason for the mute:"));
        EPlayerChat.inputs.put(player.getUUID(), content -> {
            target.mutePlayer(content, player);
        });
    }
}
