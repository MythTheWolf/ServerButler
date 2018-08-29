package com.myththewolf.ServerButler.commands.admin.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
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

public class ViewAnnouncements extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        reply("Reading cache...");
        Inventory I = Bukkit
                .createInventory(null, DataCache.annoucementHashMap.values().size(), ChatColor.AQUA + "Announcements");
        for (int i = 0; i < DataCache.annoucementHashMap.values().size(); i++) {
            ChatAnnoucement annoucement = (ChatAnnoucement) DataCache.annoucementHashMap.values().toArray()[i];
            JSONObject packet = new JSONObject();
            packet.put("packetType", PacketType.VIEW_ANNOUNCEMENT_OPTIONS);
            packet.put("ID", annoucement.getId());
            ItemStack woolColor = annoucement.isRunning() ? ItemUtils.woolForColor(DyeColor.LIME) : ItemUtils
                    .woolForColor(DyeColor.RED);
            woolColor = ItemUtils.nameItem("Task #" + annoucement.getId(), woolColor);
            woolColor = ItemUtils.applyJSON(packet, woolColor);
            I.setItem(i, woolColor);
        }
        sender.flatMap(MythPlayer::getBukkitPlayer).ifPresent(player -> player.openInventory(I));
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.VIEW_ANNOUNCEMENT_GUI;
    }
}
