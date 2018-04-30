package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class TempBanIpHandler implements ItemPacketHandler {
    String timeStr;
    String reason;

    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        String[] args = new String[2];
        args[0] = data.getString("ADDRESS");
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        player.getBukkitPlayer().ifPresent(p -> p
                .sendMessage(ConfigProperties.PREFIX + "Please specify the amount of time for the ban: (Example: 1d2h.. 12w4d..)"));
        args[1] = doDateProcess(player);
        player.getBukkitPlayer()
                .ifPresent(p -> p.sendMessage(ConfigProperties.PREFIX + "Please specify the reason for the ban: "));
        EPlayerChat.inputs.put(player.getUUID(), content -> {
            reason = content;
        });

        args[2] = reason;
        ServerButler.commands.get("iptempban").setLastPlayer(player);
        ServerButler.commands.get("iptempban").onCommand(Optional.ofNullable(player), args, tar);

    }

    private String doDateProcess(MythPlayer player) {
        EPlayerChat.inputs.put(player.getUUID(), content -> {
            try {
                TimeUtils.timeFromString(content);
                timeStr = content;
                return;
            } catch (IllegalArgumentException e) {
                player.getBukkitPlayer().ifPresent(p -> p
                        .sendMessage(ConfigProperties.PREFIX + "Invalid Date string, please try again (Example: 1d2h.. 12w4d..): "));
                doDateProcess(player);
            }
        });
        return timeStr;
    }
}
