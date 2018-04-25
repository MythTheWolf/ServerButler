package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class ips extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if(!sender.isPresent())
            return;
        if(!sender.get().getBukkitPlayer().isPresent())
            return;
        sender.get().getBukkitPlayer().get().sendMessage(ConfigProperties.PREFIX+"Building GUI for you..");
        MythPlayer target = DataCache.getOrMakePlayer(args[0]);
        int slots = target.getPlayerAddresses().size() <= 9 ? 9 : target.getPlayerAddresses().size();
        Inventory view = Bukkit.createInventory(null, slots);
        int i = 0;
        for (PlayerInetAddress I : target.getPlayerAddresses()) {
            JSONObject packet = new JSONObject();
            packet.put("ADDRESS", I.getAddress().toString());
            packet.put("packetType", PacketType.VIEW_IP_OPTIONS);
            ItemStack trigger = I.getLoginStatus().equals(LoginStatus.PERMITTED) ? ItemUtils
                    .nameItem(I.getAddress().toString(), ItemUtils.woolForColor(DyeColor.LIME)) : ItemUtils
                    .nameItem(I.getAddress().toString(), ItemUtils.woolForColor(DyeColor.RED));
            view.setItem(i, ItemUtils.applyJSON(packet, trigger));
            i++;
        }
        sender.get().getBukkitPlayer().get().openInventory(view);

    }
}
