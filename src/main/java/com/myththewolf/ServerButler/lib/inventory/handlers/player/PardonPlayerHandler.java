package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class PardonPlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getOrMakePlayer(data.getString("PLAYER-UUID"));
        player.getBukkitPlayer().ifPresent(pl -> pl.sendMessage(ConfigProperties.PREFIX + "Please type out the reason for the pardon:"));
        EPlayerChat.inputs.put(player.getUUID(), content -> {
            target.pardonPlayer(player, content);
            String msg = StringUtils.replaceParameters(ConfigProperties.FORMAT_PARDON_CHAT, 3, player.getName(), target
                    .getName(), content);
            DataCache.getAdminChannel().push(msg, null);
        });
    }
}
