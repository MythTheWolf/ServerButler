package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * This class captures all Pre-command events
 */
public class EPlayerPreprocessEvent implements Listener, Loggable {
    int spot = 0;
    @EventHandler
    public void onPreCommand(PlayerCommandPreprocessEvent event) {

        String raw = event.getMessage();
        String[] split = raw.split(" ");
        if (!ServerButler.commands.containsKey(split[0].substring(1))) {
            return;
        }

        event.setCancelled(true);
        String chop = split[0].substring(1);
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        ServerButler.commands.entrySet().stream()
                .filter(stringCommandAdapterEntry -> stringCommandAdapterEntry.getKey().equals(chop))
                .map(Map.Entry::getValue).forEach(commandAdapter -> {
            commandAdapter.setLastPlayer(DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString()));
            try {
                CommandPolicy CP = commandAdapter.getClass()
                        .getMethod("onCommand", Optional.class, String[].class, JavaPlugin.class)
                        .getAnnotation(CommandPolicy.class);
                boolean isAnnoPresent = commandAdapter.getClass().getMethod("onCommand", Optional.class, String[].class, JavaPlugin.class).isAnnotationPresent(CommandPolicy.class);
                if (!isAnnoPresent) {
                    debug("No annotations found for command executor class '" + commandAdapter.getClass()
                            .getName() + "', no checks will be made by the system!");
                }
                int commandUserReq = isAnnoPresent ? CP.userRequiredArgs() : -1;
                int commandConsoleReq = isAnnoPresent ? CP.consoleRequiredArgs() : -1;
                String usage = isAnnoPresent ? CP.commandUsage() : "<<NOT DEFINED>>";
                String permission = commandAdapter.getRequiredPermission();
                if (event.getPlayer() == null && args.length < commandConsoleReq) {
                    getLogger().warning("Could not run command '" + split[0]
                            .substring(1) + "': Required args do not match supplied args. Usage (optional args are required in this context): " + usage);
                    return;
                } else if (event.getPlayer() == null && args.length >= commandConsoleReq) {
                    commandAdapter.onCommand(Optional.empty(), args, (JavaPlugin) Bukkit.getPluginManager()
                            .getPlugin("ServerButler"));
                    return;
                }
                if (event.getPlayer() != null && args.length < commandUserReq) {
                    event.getPlayer()
                            .sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "This command requires " + commandUserReq + " arguments, got " + args.length + ".");
                    event.getPlayer().sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "Usage: " + usage);
                    return;
                }
                if (event.getPlayer() != null && permission != null && !event.getPlayer().hasPermission(permission)) {
                    event.getPlayer()
                            .sendMessage(ConfigProperties.PREFIX + "You do not have permission for this command.");
                    return;
                }
            } catch (NoSuchMethodException ex) {
                getLogger().severe("Could not find runner for command executor class: '" + commandAdapter.getClass()
                        .getName() + "'");
            }

            commandAdapter.onCommand(Optional.ofNullable(DataCache
                    .getOrMakePlayer(event.getPlayer().getUniqueId().toString())), args, (JavaPlugin) Bukkit
                    .getPluginManager().getPlugin("ServerButler"));
        });

    }
}
