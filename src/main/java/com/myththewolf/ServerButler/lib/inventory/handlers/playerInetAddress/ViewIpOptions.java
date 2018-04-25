package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

import java.util.Optional;

public class ViewIpOptions implements ItemPacketHandler, Loggable {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        Optional<PlayerInetAddress> target = DataCache.getPlayerInetAddressByIp(data.getString("ADDRESS"));
        if (!target.isPresent()) {
            getLogger().warning("Could not handle packet of type VIEW_IP_OPTIONS: IP '" + data
                    .getString("ADDRESS") + "' doesn't exist in the cache (or database)!");
            player.getBukkitPlayer().get()
                    .sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "Could not handle packet of type VIEW_IP_OPTIONS: IP '" + data
                            .getString("ADDRESS") + "' doesn't exist in the cache (or database)!");
            return;
        }

        PlayerInetAddress address = DataCache.getPlayerInetAddressByIp(data.getString("ADDRESS")).get();
        JSONObject packet_ban_ip = new JSONObject();
        packet_ban_ip.put("packetType", PacketType.BAN_IP);
        packet_ban_ip.put("ADDRESS", address.getAddress().toString());
        ItemStack stack_ban_ip = ItemUtils
                .applyJSON(packet_ban_ip, ItemUtils.nameItem("Ban this IP", new ItemStack(Material.BARRIER, 1)));
        JSONObject packet_pardon_ip = new JSONObject();
        packet_pardon_ip.put("packetType", PacketType.PARDON_IP);
        packet_pardon_ip.put("ADDRESS", address.getAddress().toString());
        ItemStack stack_pardon_ip = ItemUtils.applyJSON(packet_pardon_ip, ItemUtils
                .nameItem("Pardon this IP", ItemUtils.woolForColor(DyeColor.LIME)));

    }
}
