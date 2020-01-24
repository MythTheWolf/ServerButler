package com.myththewolf.ServerButler.commands.admin.InetAddr.management;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class ipnames extends CommandAdapter {
    @Override
    @CommandPolicy(userRequiredArgs = 1, consoleRequiredArgs = 1, commandUsage = "/ipnames <user || IP address>")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<PlayerInetAddress> playerInetAddress;
        if (args[0].startsWith("/")) {
            playerInetAddress = DataCache.getPlayerInetAddressByIp(args[0]);
        } else {
            Optional<MythPlayer> player = DataCache.getPlayerByName(args[0]);
            if (!player.isPresent()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "Could not grab ip: Player not found.");
                return;
            } else if (!player.get().isOnline()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "ERROR: " + ChatColor.GOLD + "The player isn't online, cannot grab IP address. (Use /ips <playername>)");
                return;
            }
            playerInetAddress = DataCache
                    .getOrMakeInetAddress(player.get().getConnectionAddress().orElseThrow(AssertionError::new)
                            .getDatabaseId());
        }
        if (!playerInetAddress.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "IP not found");
            return;
        }
        reply(ConfigProperties.PREFIX + "All players on IP `" + playerInetAddress.toString() + "`: " + ChatColor.AQUA + playerInetAddress.get().getMappedPlayers().toString());
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.VIEW_PLAYER_IPS_PERMISSION;
    }
}
