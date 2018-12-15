package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.CustomDyeColor;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommitChannelHandler implements ItemPacketHandler, Loggable {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        if (!player.isOnline() || !player.getBukkitPlayer().isPresent()) {
            return;
        }
        player.getBukkitPlayer().get().sendMessage(ConfigProperties.PREFIX + "Reading Database..");
        Inventory I = Bukkit.createInventory(null, ItemUtils
                .findInventorySize(DataCache.getAllChannels().size() + 1), "Please select a channel");
        List<ChatChannel> allChannelList = DataCache.getAllChannels();
        List<ChatChannel> selectedChannels = data.isNull("selectedChannels") ? new ArrayList<>() : StringUtils
                .deserializeArray(data.getString("selectedChannels")).stream()
                .map(ChatChannel::new).collect(Collectors.toList());
        if (!data.isNull("add")) {
            ChatChannel c = DataCache.getOrMakeChannel(Integer.parseInt(data.getString("channelChosenID"))).get();
            if (data.getBoolean("add")) {
                selectedChannels.add(c);
            } else {
                selectedChannels.remove(c);
            }
        }
        String compiled = StringUtils
                .serializeArray(selectedChannels.stream().map(ChatChannel::getID).collect(Collectors.toList()));
        for (int i = 0; i < allChannelList.size(); i++) {
            ChatChannel c = allChannelList.get(i);
            ItemStack stack = selectedChannels.contains(c) ? ItemUtils
                    .nameItem("Deselect " + c.getName(), ItemUtils
                            .applyJSON(data.put("add", false).put("channelChosenID", c.getID())
                                    .put("selectedChannels", compiled), ItemUtils
                                    .woolForColor(DyeColor.LIME))) : ItemUtils
                    .nameItem("Select " + c.getName(), ItemUtils
                            .applyJSON(data.put("add", true).put("channelChosenID", c.getID())
                                    .put("selectedChannels", compiled), ItemUtils
                                    .woolForColor(DyeColor.RED)));
            I.setItem(i, stack);
        }
        ItemStack packetCommit = ItemUtils.nameItem("Confirm Changes", ItemUtils.applyJSON(data
                .put("selectedChannels", compiled).put("packetType", PacketType.valueOf(data
                        .getString("targetPacketType"))), new ItemStack(Material.INK_SACK, 1, CustomDyeColor.LIME
                .getData())));
        I.setItem(ItemUtils.findInventorySize(DataCache.getAllChannels().size() + 1) - 1, packetCommit);
        player.getBukkitPlayer().get().openInventory(I);
    }
}