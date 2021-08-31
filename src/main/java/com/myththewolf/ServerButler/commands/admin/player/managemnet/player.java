package com.myththewolf.ServerButler.commands.admin.player.managemnet;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class player extends CommandAdapter {
    @Override
    @CommandPolicy(commandUsage = "/player <player name>", userRequiredArgs = 1, consoleRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> send, String[] args, JavaPlugin javaPlugin) {
        reply(ConfigProperties.PREFIX + "Reading database..");
        send.ifPresent(player -> {
            Optional<MythPlayer> optionalMythPlayer = DataCache.getPlayerByName(args[0]);
            if (!optionalMythPlayer.isPresent()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
                return;
            }
            MythPlayer target = optionalMythPlayer.get();
            Inventory targetInventory = Bukkit.createInventory(null, 9, "Options for " + target.getName());
            List<ItemStack> actions = new ArrayList<>();
            player.getBukkitPlayer().ifPresent(sender -> {
                actions.add(target.getLoginStatus().equals(LoginStatus.PERMITTED) ? player
                        .hasPermission(ConfigProperties.BAN_PERMISSION) ? ItemUtils
                        .makeBanUserItem(target, player) : null : player
                        .hasPermission(ConfigProperties.PARDON_PERMISSION) ? ItemUtils
                        .makePardonUserItem(target, player) : null);
                ItemStack tempBanItem = ItemUtils.woolForColor(DyeColor.ORANGE);
                JSONObject tempBanPacket = new JSONObject();
                tempBanPacket.put("packetType", PacketType.TEMPBAN_PLAYER);
                tempBanPacket.put("PLAYER-NAME", target.getName());
                actions.add(player.hasPermission(ConfigProperties.TEMPBAN_PERMISSION) ? ItemUtils
                        .nameItem("Temp Ban player", ItemUtils.applyJSON(tempBanPacket, tempBanItem)) : null);
                ItemStack muteUnmute = target.getChatStatus().equals(ChatStatus.MUTED) || target.getChatStatus()
                        .equals(ChatStatus.SOFTMUTED) ? player.hasPermission(ConfigProperties.UNMUTE_PERMISSION) ? ItemUtils.makeUnmuteUserItem(target, send.get()) : null : ItemUtils
                        .makeMuteUserItem(target, send.get());
                actions.add(muteUnmute);
                actions.add(player.hasPermission(ConfigProperties.MUTE_PERMISSION) ? ItemUtils.makeSoftmuteUserItem(target, send.get()) : null);
                JSONObject viewPlayerIPsPacket = new JSONObject();
                viewPlayerIPsPacket.put("packetType", PacketType.VIEW_PLAYER_IPS);
                viewPlayerIPsPacket.put("PLAYER-UUID", target.getUUID());
                ItemStack itemViewIPs = ItemUtils.applyJSON(viewPlayerIPsPacket, ItemUtils
                        .nameItem("View Player IPs", ItemUtils.getSkullofPlayer(target.getUUID())));
                actions.add(player.hasPermission(ConfigProperties.VIEW_PLAYER_IPS_PERMISSION) ? itemViewIPs : null);

                JSONObject viewExtraInfoPacket = new JSONObject();
                viewExtraInfoPacket.put("PLAYER-NAME", target.getName());
                viewExtraInfoPacket.put("packetType", PacketType.VIEW_PLAYER_EXTA_INFO);
                ItemStack viewExtra = ItemUtils.applyJSON(viewExtraInfoPacket,ItemUtils
                        .nameItem("View Player Info", ItemUtils.getSkullofPlayer(target.getUUID())));
                actions.add(viewExtra);
                actions.stream().filter(Objects::nonNull).forEach(targetInventory::addItem);
                sender.openInventory(targetInventory);
            });
        });
    }

    @Override
    public String getRequiredPermission() {
        return "sb.player";
    }
}