package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
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
                .applyJSON(packet_ban_ip, ItemUtils.nameItem("Ban this IP", ItemUtils.woolForColor(DyeColor.RED)));
        JSONObject packet_pardon_ip = new JSONObject();
        packet_pardon_ip.put("packetType", PacketType.PARDON_IP);
        packet_pardon_ip.put("ADDRESS", address.getAddress().toString());
        ItemStack stack_pardon_ip = ItemUtils.applyJSON(packet_pardon_ip, ItemUtils
                .nameItem("Pardon this IP", ItemUtils.woolForColor(DyeColor.LIME)));
        JSONObject packet_tempban_ip = new JSONObject();
        packet_tempban_ip.put("packetType", PacketType.TEMPBAN_IP);
        packet_tempban_ip.put("ADDRESS", address.getAddress().toString());
        ItemStack stack_tempban_ip = ItemUtils.applyJSON(packet_tempban_ip, ItemUtils
                .nameItem("TempBan this IP", ItemUtils.woolForColor(DyeColor.YELLOW)));
        JSONObject packet_list_players = new JSONObject();
        packet_list_players.put("packetType", PacketType.LIST_PLAYERS);
        packet_list_players.put("ADDRESS", address.getAddress().toString());
        packet_list_players.put("PAGE", 0);
        ItemStack stack_list_players = ItemUtils.applyJSON(packet_list_players, ItemUtils
                .nameItem("List players who have this IP", new ItemStack(Material.SKULL_ITEM, 1)));
        JSONObject packet_delete_ip = new JSONObject();
        packet_delete_ip.put("packetType", PacketType.DELETE_IP);
        packet_delete_ip.put("ADDRESS", address.getAddress().toString());
        ItemStack stack_delete_ip = ItemUtils
                .applyJSON(packet_delete_ip, ItemUtils.nameItem("Delete this IP", new ItemStack(Material.BARRIER, 1)));

        Inventory view = Bukkit.createInventory(null, 9, "Options for " + address.getAddress().toString());
        if (address.getLoginStatus().equals(LoginStatus.PERMITTED)) {
            view.setItem(0, stack_ban_ip);
            view.setItem(1, stack_tempban_ip);
        } else if (address.getLoginStatus().equals(LoginStatus.BANNED)) {
            view.setItem(0, stack_tempban_ip);
            view.setItem(1, stack_pardon_ip);
        } else {
            view.setItem(0, stack_tempban_ip);
            view.setItem(1, stack_pardon_ip);
        }
        view.setItem(2, stack_list_players);
        view.setItem(3, stack_delete_ip);
        player.getBukkitPlayer().ifPresent(p -> p.openInventory(view));
    }
}
