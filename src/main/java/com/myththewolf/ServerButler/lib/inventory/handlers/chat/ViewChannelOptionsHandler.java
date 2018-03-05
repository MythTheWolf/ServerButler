package com.myththewolf.ServerButler.lib.inventory.handlers.chat;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.json.JSONObject;

import java.util.Optional;

/**
 * This class handles all packets received from items that are to show the player options,given a channel
 */
public class ViewChannelOptionsHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        Optional<ChatChannel> optionalChatChannel = data.isNull("channelID") ? DataCache
                .getOrMakeChannel(-1) : DataCache.getOrMakeChannel(data.getInt("channelID"));
        optionalChatChannel.ifPresent(channel -> {
            boolean isCurrentlyViewing = player.isViewing(channel);
            if (!isCurrentlyViewing) {
                Inventory I = Bukkit.createInventory(null, 9, "Channel options: " + channel.getName());
                I.setItem(0, ItemUtils.makeOpenChannelItemStack(channel));
                I.setItem(1, ItemUtils.makeWriteToChannelItemStack(channel));
                player.getBukkitPlayer().ifPresent(player1 -> player1.openInventory(I));
            } else {
                Inventory I = Bukkit.createInventory(null, 9, "Channel options: " + channel.getName());
                I.setItem(0, ItemUtils.makeCloseChannelItemStack(channel));
                I.setItem(1, ItemUtils.makeWriteToChannelItemStack(channel));
                player.getBukkitPlayer().ifPresent(player1 -> player1.openInventory(I));
            }
        });
    }
}
