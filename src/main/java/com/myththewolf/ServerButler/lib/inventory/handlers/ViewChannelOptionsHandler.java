package com.myththewolf.ServerButler.lib.inventory.handlers;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.json.JSONObject;

import java.util.Optional;

public class ViewChannelOptionsHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        Optional<ChatChannel> optionalChatChannel = data.isNull("CHANNEL_ID") ? DataCache
                .getOrMakeChannel(-1) : DataCache.getOrMakeChannel(data.getInt("CHANNEL_ID"));
        optionalChatChannel.ifPresent(channel -> {
            boolean isCurrentlyViewing = player.isViewing(channel);
            if (!isCurrentlyViewing) {
                Inventory I = Bukkit.createInventory(null, 9, "Channel options: " + channel.getName());
                I.setItem(0, ItemUtils.makeOpenChannelItemStack(channel));
                I.setItem(1, ItemUtils.makeWriteToChannelItemStack(channel));
                player.getBukkitPlayer().ifPresent(player1 -> player1.openInventory(I));
            } else {
                Inventory I = Bukkit.createInventory(null, 9, "Channel options: " + channel.getName());
                I.setItem(0, ItemUtils.makeCloseChanneItemStack(channel));
                I.setItem(1, ItemUtils.makeWriteToChannelItemStack(channel));
                player.getBukkitPlayer().ifPresent(player1 -> player1.openInventory(I));
            }
        });
    }
}
