package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
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
import java.util.stream.Collectors;

public class CommitChannelHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        if (!player.isOnline() || !player.getBukkitPlayer().isPresent()) {
            return;
        }
        player.getBukkitPlayer().get().sendMessage(ConfigProperties.PREFIX + "Reading Database..");
        Inventory I = Bukkit.createInventory(null, ItemUtils
                .findInventorySize(DataCache.getAllChannels().size() + 1), "Please select a channel");
        List<ChatChannel> allChannelList = DataCache.getAllChannels();
        List<ChatChannel> selectedChannels = StringUtils.deserializeArray(data.getString("selected")).stream()
                .map(ChatChannel::new).collect(Collectors.toList());
        if (data.getBoolean("isAdd"))
            for (int i = 0; i < allChannelList.size(); i++) {
                JSONObject packet = new JSONObject();
                ChatChannel c = allChannelList.get(i);
                packet.put("raw-data", data);
                packet.put("packetType", PacketType.CHANNEL_SELECTION_CONTINUE);
                packet.put("isAdd", selectedChannels.contains(c));
                packet.put("id", c.getID());
                ItemStack stack = ItemUtils.applyJSON(packet, selectedChannels.contains(c) ? ItemUtils
                        .nameItem("Deselect " + c.getName(), ItemUtils.woolForColor(DyeColor.LIME)) : ItemUtils
                        .nameItem("Select " + c.getName(), ItemUtils.woolForColor(DyeColor.RED)));
                I.setItem(i, stack);
            }
        player.getBukkitPlayer().get().openInventory(I);
    }
}
