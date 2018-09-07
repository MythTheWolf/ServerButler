package com.myththewolf.ServerButler.commands.admin.player.punishment;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class probate extends CommandAdapter {
    @Override
    @CommandPolicy(commandUsage = "/probate <Player> [true/false]", userRequiredArgs = 1, consoleRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        boolean updated = args.length > 1 ? Boolean.parseBoolean(args[1]) : !target.get().isProbated();
        target.get().setProbate(updated);
        target.get().updatePlayer();
        DataCache.getPunishmentInfoChannel().push(target.get().getName() + "'s probate status was set to " + updated);
        DataCache.rebuildPlayer(target.get().getUUID());
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.PROBATE_PERMISSION;
    }
}
