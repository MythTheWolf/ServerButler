package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class PreCommand implements Listener, Loggable {
    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {
        String raw = event.getMessage();
        event.getPlayer().sendMessage(raw);
        String[] split = raw.split(" ");
        Command command = Bukkit.getPluginCommand(split[0]);
        if (command != null && !ServerButler.commands.containsKey(split[0].substring(1))) {
            return;
        }
        event.setCancelled(true);
        String chop = split[0].substring(1);
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        ServerButler.commands.entrySet().stream()
                .filter(stringCommandAdapterEntry -> stringCommandAdapterEntry.getKey().equals(chop))
                .map(Map.Entry::getValue).forEach(commandAdapter -> {
            commandAdapter.setLastPlayer(DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString()));
            if (commandAdapter.getNumRequiredArgs() > args.length) {
                commandAdapter.reply(ConfigProperties.PREFIX + ChatColor.RED + "This command requires " + commandAdapter
                        .getNumRequiredArgs() + " arguments: \n" + commandAdapter.getUsage());
                return;
            }
            if (commandAdapter.getRequiredPermission() != null && !(event.getPlayer()
                    .hasPermission(commandAdapter.getRequiredPermission()))) {
                commandAdapter
                        .reply(ConfigProperties.PREFIX + ChatColor.RED + "You dont have permissions to execute this command.");
                return;
            }
            commandAdapter.onCommand(Optional.ofNullable(DataCache
                    .getOrMakePlayer(event.getPlayer().getUniqueId().toString())), args, (JavaPlugin) Bukkit
                    .getPluginManager().getPlugin("ServerButler"));
        });
    }
}
