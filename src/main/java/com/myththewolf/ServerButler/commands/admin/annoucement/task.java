package com.myththewolf.ServerButler.commands.admin.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.MythUtils.CustomDyeColor;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class task extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (!sender.isPresent()) {
            return;
        }
        reply(ConfigProperties.PREFIX + "Reading database...");
        ChatAnnoucement target = DataCache.getAnnouncement(args[0]).get();
        JSONObject packetAddChannel = new JSONObject();
        packetAddChannel.put("packetType", PacketType.COMMIT_CHANNEL);
        packetAddChannel.put("targetPacketType", PacketType.ADD_CHANNEL);
        packetAddChannel.put("ID", target.getId());
        ItemStack itemAddChannel = ItemUtils.nameItem("Add a channel", ItemUtils
                .applyJSON(packetAddChannel, ItemUtils.woolForColor(DyeColor.LIME)));
        JSONObject packetRemoveChannel = new JSONObject();
        packetRemoveChannel.put("packetType", PacketType.COMMIT_CHANNEL);
        packetRemoveChannel.put("targetPacketType", PacketType.REMOVE_CHANNEL);
        packetRemoveChannel.put("ID", target.getId());
        ItemStack itemRemoveChannel = ItemUtils.nameItem("Remove a channel", ItemUtils
                .applyJSON(packetRemoveChannel, ItemUtils.woolForColor(DyeColor.RED)));
        JSONObject packetUpdateContent = new JSONObject();
        packetUpdateContent.put("packetType", PacketType.UPDATE_CONTENT);
        packetUpdateContent.put("ID", target.getId());
        ItemStack itemUpdateContent = ItemUtils.nameItem("Update Content", ItemUtils
                .applyJSON(packetUpdateContent, new ItemStack(Material.BOOK_AND_QUILL, 1)));
        JSONObject packetUpdateInterval = new JSONObject();
        packetUpdateInterval.put("packetType", PacketType.UPDATE_INTERVAL);
        packetUpdateInterval.put("ID", target.getId());
        ItemStack itemUpdateInterval = ItemUtils.nameItem("Update Interval", ItemUtils
                .applyJSON(packetUpdateInterval, new ItemStack(Material.WATCH, 1)));
        JSONObject packetUpdatePermission = new JSONObject();
        packetUpdatePermission.put("packetType", PacketType.UPDATE_PERMISSION);
        packetUpdatePermission.put("ID", target.getId());
        ItemStack itemUpdatePermission = ItemUtils.nameItem("Update Permission", ItemUtils
                .applyJSON(packetUpdatePermission, new ItemStack(Material.SHIELD, 1)));
        JSONObject packetStartTask = new JSONObject();
        packetStartTask.put("packetType", PacketType.START_ANNOUNCEMENT);
        packetStartTask.put("ID", target.getId());
        ItemStack itemStartTask = ItemUtils.nameItem("Start task", ItemUtils
                .applyJSON(packetStartTask, new ItemStack(Material.INK_SACK, 1, CustomDyeColor.LIME.getData())));
        JSONObject packetStopTask = new JSONObject();
        packetStopTask.put("packetType", PacketType.STOP_ANNOUNCEMENT);
        packetStopTask.put("ID", target.getId());
        ItemStack itemStopTask = ItemUtils.nameItem("Stop task", ItemUtils
                .applyJSON(packetStopTask, new ItemStack(Material.INK_SACK, 1, CustomDyeColor.RED.getData())));
        JSONObject packetDeleteItem = new JSONObject();
        packetDeleteItem.put("packetType", PacketType.DELETE_ANNOUNCEMENT);
        packetDeleteItem.put("ID", target.getId());
        ItemStack itemDelete = ItemUtils.nameItem("Delete announcement", ItemUtils
                .applyJSON(packetDeleteItem, new ItemStack(Material.BARRIER, 1)));
        Inventory I = Bukkit.createInventory(null, 9, "Options for Announcement #" + target.getId());
        I.setItem(0, itemAddChannel);
        I.setItem(1, itemRemoveChannel);
        I.setItem(2, itemUpdateContent);
        I.setItem(3, itemUpdateInterval);
        I.setItem(4, itemUpdatePermission);
        I.setItem(5, itemStartTask);
        I.setItem(6, itemStopTask);
        I.setItem(7, itemDelete);
        sender.get().getBukkitPlayer().ifPresent(player -> player.openInventory(I));
    }
}
