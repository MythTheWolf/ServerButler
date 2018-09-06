package com.myththewolf.ServerButler.commands.player;

import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class eula extends CommandAdapter {
    @Override
    @CommandPolicy(userRequiredArgs = 1, commandUsage = "/eula <accept|deny>")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (args[0].toLowerCase().equals("accept")) {
            ConfigProperties.postEulaCommands.forEach(s -> {
                String parsed = s.replace("{player}", sender.get().getName())
                        .replace("{player_id}", sender.get().getUUID());
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsed);
            });
        }
    }
}
