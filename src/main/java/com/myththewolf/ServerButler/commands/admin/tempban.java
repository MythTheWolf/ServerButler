package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Optional;

public class tempban extends CommandAdapter {
    DateTime expireDate = new DateTime();

    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> targetOp = DataCache.getPlayerByName(args[0]);
        if (!targetOp.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        expireDate = null;
        reply(ConfigProperties.PREFIX + "Please supply a interval:");
        reply(ConfigProperties.PREFIX + "(Format: [Integer][Period], example: 1d4h)");
        EPlayerChat.inputs.put(sender.get().getName(), content -> {
            Period p = TimeUtils.TIME_INPUT_FORMAT().parsePeriod(content);
            expireDate = (new DateTime()).withPeriodAdded(p, 1);
        });
        reply(ConfigProperties.PREFIX + "Please supply a reason:");
        EPlayerChat.inputs.put(sender.get().getName(), content -> {
            targetOp.get().tempbanPlayer(sender.orElse(null), content, expireDate);
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
