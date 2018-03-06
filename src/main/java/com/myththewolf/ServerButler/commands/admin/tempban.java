package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.Optional;

public class tempban extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> targetOp = DataCache.getPlayerByName(args[0]);
        if (!targetOp.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        //TODO Pd. formatting
        EPlayerChat.inputs.put(sender.get().getName(), content -> {
          //TODO: implement the actual ban
        });
    }

    @Override
    public int getNumRequiredArgs() {
        return 2;
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.BAN_PERMISSION;
    }

    @Override
    public String getUsage() {
        return "/tempban <player name>";
    }
}
