package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.moderation.impl.ActionUserKick;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class kick extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found.");
            return;
        }
        target.ifPresent(target1 -> {
            String modName = sender.map(MythPlayer::getName).orElse("CONSOLE");
            String reason = StringUtils.arrayToString(1, args);
            ModerationAction action = new ActionUserKick(reason, target1, sender.orElse(null));
            ((ActionUserKick) action).update();
            String message = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_KICK_CHAT, 3, modName, target.get().getName(), reason);
            DataCache.getAdminChannel().push(message, null);
        });
    }
}
