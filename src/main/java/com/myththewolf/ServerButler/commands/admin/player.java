package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class player extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> send, String[] args, JavaPlugin javaPlugin) {
        send.ifPresent(player -> {
            Optional<MythPlayer> optionalMythPlayer = DataCache.getPlayerByName(args[0]);
            if (!optionalMythPlayer.isPresent()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
                return;
            }
            MythPlayer target = optionalMythPlayer.get();
            Inventory targetInventory = Bukkit.createInventory(null, 9, "Options for " + target.getName());
            player.getBukkitPlayer().ifPresent(sender -> {
                targetInventory.setItem(0, target.getLoginStatus().equals(LoginStatus.PERMITTED) ? ItemUtils
                        .makeBanUserItem(target, player) : ItemUtils.makePardonUserItem(target, player));
                ItemStack tempBanItem = ItemUtils.woolForColor(DyeColor.ORANGE);
                JSONObject tempBanPacket = new JSONObject();
                tempBanPacket.put("packetType", PacketType.TEMPBAN_PLAYER);
                tempBanPacket.put("PLAYER-NAME", target.getName());
                targetInventory.setItem(1, ItemUtils
                        .nameItem("Temp Ban player", ItemUtils.applyJSON(tempBanPacket, tempBanItem)));
                if (target.getChatStatus().equals(ChatStatus.MUTED)) {
                    targetInventory.setItem(2, ItemUtils.makeSoftmuteUserItem(target, player));
                    targetInventory.setItem(3, ItemUtils.makUnmuteUserItem(target, player));
                } else if (target.getChatStatus().equals(ChatStatus.SOFTMUTED)) {
                    targetInventory.setItem(2, ItemUtils.makeMuteUserItem(target, player));
                    targetInventory.setItem(3, ItemUtils.makUnmuteUserItem(target, player));
                } else {
                    targetInventory.setItem(2, ItemUtils.makeMuteUserItem(target, player));
                    targetInventory.setItem(3, ItemUtils.makeSoftmuteUserItem(target, player));
                }
                JSONObject viewPlayerIPsPacket = new JSONObject();
                viewPlayerIPsPacket.put("packetType", PacketType.VIEW_PLAYER_IPS);
                viewPlayerIPsPacket.put("PLAYER-UUID", target.getUUID());
                ItemStack itemViewIPs = ItemUtils.applyJSON(viewPlayerIPsPacket, ItemUtils
                        .nameItem("View Player IPs", ItemUtils.getSkullofPlayer(target.getUUID())));
                targetInventory.setItem(4,itemViewIPs);
                sender.openInventory(targetInventory);
            });
        });
    }

    @Override
    public int getNumRequiredArgs() {
        return 1;
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.BAN_PERMISSION;
    }

    @Override
    public String getUsage() {
        return "/player <playername>";
    }
}
