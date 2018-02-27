package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class Ban extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        String reason = StringUtils.arrayToString(1, args);
        target.get().banPlayer(reason, sender.orElse(null));
        ChatChannel adminChat = DataCache.getAdminChannel();
        String message = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_BAN_CHAT, 3, (sender.isPresent() ? sender.get()
                        .getName() : "CONSOLE"), target.get().getName(), reason);
        adminChat.push(message, null);
    }


    @Override
    public String getRequiredPermission() {
        return ConfigProperties.BAN_PERMISSION;
    }

    @Override
    public int getNumRequiredArgs() {
        return 2;
    }

    @Override
    public String getUsage() {
        return "/ban <username> <reason>";
    }
}
