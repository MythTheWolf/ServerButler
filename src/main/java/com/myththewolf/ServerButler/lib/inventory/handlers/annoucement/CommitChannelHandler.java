package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.List;

public class CommitChannelHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        if (!player.isOnline() || !player.getBukkitPlayer().isPresent()) {
            return;
        }
        player.getBukkitPlayer().get().sendMessage(ConfigProperties.PREFIX + "Reading Database..");
        Inventory I = Bukkit.createInventory(null, ItemUtils
                .findInventorySize(DataCache.getAllChannels().size()), "Please select a channel");
        PacketType packetType = PacketType.valueOf(data.getString("targetPacketType"));
        ChatAnnoucement target = DataCache.getAnnouncement(data.getString("ID")).get();
        List<ChatChannel> allChannelList = DataCache.getAllChannels();
        for (int i = 0; i < allChannelList.size(); i++) {
            JSONObject packet = new JSONObject();
            packet.put("packetType", packetType);
            packet.put("ID", target.getId());
            packet.put("channelID", allChannelList.get(i).getID());
            ItemStack itemStack = ItemUtils.nameItem(allChannelList.get(i).getName(), ItemUtils
                    .applyJSON(packet, ItemUtils.woolForColor(DyeColor.CYAN)));
            I.setItem(i, itemStack);
        }
        player.getBukkitPlayer().get().openInventory(I);
    }
}
