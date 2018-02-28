package com.myththewolf.ServerButler.lib.inventory.handlers;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import org.json.JSONObject;

import java.util.Optional;

public class SetWriteChannelPacketHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(IMythPlayer player, JSONObject data) {
        Optional<ChatChannel> optionalChatChannel = data.isNull("channelID") ? DataCache
                .getOrMakeChannel(-1) : DataCache.getOrMakeChannel(data.getInt("channelID"));
        optionalChatChannel.ifPresent(channel -> {
            player.setWritingChannel(channel);
            player.updatePlayer();
            DataCache.rebuildChannelList();
        });
    }
}
