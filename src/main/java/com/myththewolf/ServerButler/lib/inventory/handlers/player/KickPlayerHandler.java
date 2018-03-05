package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class KickPlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getOrMakePlayer(data.getString("PLAYER-UUID"));
        EPlayerChat.inputs.put(target.getUUID(), content -> {
            target.kickPlayer(content, player);
            String chatMessage = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_KICK_CHAT, 2, player.getName(), target
                            .getName(), content);

            DataCache.getAdminChannel().push(chatMessage,null);
        });
    }
}
