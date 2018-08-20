package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.impl.User.ActionUserKick;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * This class represents the /kick command
 */
public class kick extends CommandAdapter {
    @Override
    @CommandPolicy(commandUsage = "/kick <username> <reason>",userRequiredArgs = 1,consoleRequiredArgs = 2)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found.");
            return;
        }
        if(sender.isPresent() && target.get().equals(sender.get())){
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You cannot kick yourself.");
            return;
        }
        target.ifPresent(target1 -> {
            String modName = sender.map(MythPlayer::getName).orElse("CONSOLE");
            String reason = StringUtils.arrayToString(1, args);
            ModerationAction action = new ActionUserKick(reason, target1, sender.orElse(null));
            ((ActionUserKick) action).update();
            String message = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_KICK_CHAT, modName, target.get().getName(), reason);
            DataCache.getAdminChannel().push(message, null);
        });
    }
}
