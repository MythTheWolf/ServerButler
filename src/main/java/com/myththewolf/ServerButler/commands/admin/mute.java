package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class mute extends CommandAdapter {
    String reason;

    @Override
    @CommandPolicy(commandUsage = "/mute <username> [reason]",consoleRequiredArgs = 2,userRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        reason = ConfigProperties.DEFAULT_MUTE_REASON;
        if (args.length < 2 && sender.isPresent()) {
            reply("Please type out a reason for the mute:");
            EPlayerChat.inputs.put(sender.get().getUUID(), content -> reason = content);
        } else {
            reason = StringUtils.arrayToString(1, args);
        }
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        target.get().mutePlayer(reason, sender.orElse(null));
        target.get().updatePlayer();
        String toSend = StringUtils.replaceParameters(ConfigProperties.FORMAT_MUTE_CHAT, sender.map(MythPlayer::getName)
                .orElse("CONSOLE"), target.map(MythPlayer::getName).orElse("<ERROR: MythPlayer not present>"), reason);
        DataCache.getAdminChannel().push(toSend, null);
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.MUTE_PERMISSION;
    }

}
