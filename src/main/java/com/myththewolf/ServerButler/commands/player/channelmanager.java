package com.myththewolf.ServerButler.commands.player;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

/**
 * This class represents the /chan command, it brings up the channel manager GUI for players
 */
public class channelmanager extends CommandAdapter {
    private int i = 0;

    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        i = 0;
        sender.ifPresent(player -> {
            if(!player.getBukkitPlayer().isPresent()) return;
            Inventory I = (DataCache.getAllChannels().size() > 9 ? Bukkit
                    .createInventory(null, DataCache.getAllChannels().size(), ChatColor
                            .translateAlternateColorCodes('&', "&8[&6All available channels&8]")) : Bukkit
                    .createInventory(null, 9, ChatColor
                            .translateAlternateColorCodes('&', "&8[&6All available channels&8]")));
            DataCache.getAllChannels().stream()
                    .filter(channel -> !channel.getPermission().isPresent() || player.getBukkitPlayer().get()
                            .hasPermission(channel.getPermission().get())).forEach(theChannel -> {
                JSONObject packet = new JSONObject();
                packet.put("packetType", PacketType.VIEW_CHANNEL_OPTIONS);
                packet.put("channelID", theChannel.getID());
                ItemStack channelItem = (player.isViewing(theChannel) ? (player.getWritingChannel() != null && player
                        .getWritingChannel()
                        .equals(theChannel) ? getWoolOfColor(DyeColor.MAGENTA) : getWoolOfColor(DyeColor.LIME)) : getWoolOfColor(DyeColor.RED));
                ItemMeta meta = channelItem.getItemMeta();
                meta.setDisplayName(theChannel.getName());
                channelItem.setItemMeta(meta);
                ItemStack json_Applied = ItemUtils.applyJSON(packet, channelItem);
                ItemMeta meta_jsonApplied = json_Applied.getItemMeta();
                List<String> oldLore = meta_jsonApplied.getLore();
                oldLore.add((player.isViewing(theChannel) ? (player.getWritingChannel() != null && player
                        .getWritingChannel()
                        .equals(theChannel) ? "You are viewing & writing to this channel." : "You are viewing this channel.") : "You are not viewing this channel."));
                meta_jsonApplied.setLore(oldLore);
                json_Applied.setItemMeta(meta_jsonApplied);
                I.setItem(i, json_Applied);
                i++;
            });
            player.getBukkitPlayer().ifPresent(p -> p.openInventory(I));
        });

    }

    private ItemStack getWoolOfColor(DyeColor color) {
        Wool w = new Wool();
        w.setColor(color);
        return w.toItemStack(1);
    }
}
