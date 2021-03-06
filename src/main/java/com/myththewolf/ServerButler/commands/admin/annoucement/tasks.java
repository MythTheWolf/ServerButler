package com.myththewolf.ServerButler.commands.admin.annoucement;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class tasks extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        reply(ConfigProperties.PREFIX + "Reading cache...");
        Inventory I = Bukkit
                .createInventory(null, ItemUtils.findInventorySize(DataCache.annoucementHashMap.values()
                        .size()), ChatColor.AQUA + "Announcements");

        int i = 0;
        DataCache.annoucementHashMap.forEach((key, val) -> {
            JSONObject packet = new JSONObject();
            packet.put("packetType", PacketType.VIEW_ANNOUNCEMENT_OPTIONS);
            packet.put("ID", val.getId());
            ItemStack woolColor = val.isRunning() ? ItemUtils.woolForColor(DyeColor.LIME) : ItemUtils
                    .woolForColor(DyeColor.RED);
            woolColor = ItemUtils.nameItem("Task #" + val.getId(), woolColor);
            woolColor = ItemUtils.applyJSON(packet, woolColor);
            I.setItem(i, woolColor);
        });

        sender.flatMap(MythPlayer::getBukkitPlayer).ifPresent(player -> player.openInventory(I));
    }
    @Override
    public String getRequiredPermission() {
        return ConfigProperties.VIEW_ANNOUNCEMENT_GUI;
    }
}
