package com.myththewolf.ServerButler.commands.admin.annoucement;

import com.myththewolf.ServerButler.lib.MythUtils.CustomDyeColor;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class tasks extends CommandAdapter {
    int i = 0;
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        i = 0;
        reply(ConfigProperties.PREFIX + "Reading cache...");
        Inventory I = Bukkit
                .createInventory(null, ItemUtils.findInventorySize(DataCache.annoucementHashMap.values()
                        .size() + 1), ChatColor.AQUA + "Announcements");

        DataCache.annoucementHashMap.forEach((key, val) -> {
            JSONObject packet = new JSONObject();
            packet.put("packetType", PacketType.VIEW_ANNOUNCEMENT_OPTIONS);
            packet.put("ID", val.getId());
            ItemStack woolColor = val.isRunning() ? ItemUtils.woolForColor(DyeColor.LIME) : ItemUtils
                    .woolForColor(DyeColor.RED);
            woolColor = ItemUtils.nameItem(val.getContent(), woolColor);
            woolColor = ItemUtils.applyJSON(packet, woolColor);
            I.setItem(i, woolColor);
            i++;
        });
        JSONObject packetCreateAnnouncement = new JSONObject();
        packetCreateAnnouncement.put("packetType", PacketType.CREATE_ANNOUNCEMENT);
        ItemStack itemCreateAnnouncement = ItemUtils.nameItem("Create new Announcement", ItemUtils
                .applyJSON(packetCreateAnnouncement, new ItemStack(Material.ORANGE_DYE)));
        I.setItem(ItemUtils.findInventorySize(DataCache.annoucementHashMap.values()
                .size() + 1) - 1, itemCreateAnnouncement);
        sender.flatMap(MythPlayer::getBukkitPlayer).ifPresent(player -> player.openInventory(I));
    }
    @Override
    public String getRequiredPermission() {
        return ConfigProperties.VIEW_ANNOUNCEMENT_GUI;
    }
}
