package com.myththewolf.ServerButler.commands.admin.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * This class represents the /kick command
 */
public class kick extends CommandAdapter {

    @Override
    @CommandPolicy(commandUsage = "/kick <username> <reason>", userRequiredArgs = 1, consoleRequiredArgs = 2)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found.");
            return;
        }
        if (sender.isPresent() && target.get().equals(sender.get())) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You cannot kick yourself.");
            return;
        }
        if (args.length == 1) {
            ServerButler.conversationBuilder.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return ConfigProperties.PREFIX + "Please specify the kick reason.";
                }

                @Override
                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                    conversationContext.setSessionData("packetType", PacketType.KICK_PLAYER);
                    conversationContext.setSessionData("reason", s);
                    conversationContext.setSessionData("sender", sender.orElse(null));
                    conversationContext.setSessionData("target", target.get());
                    return END_OF_CONVERSATION;
                }
            }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();
        } else {
            String reason = StringUtils.arrayToString(1, args);
            String modName = sender.map(MythPlayer::getName).orElse("CONSOLE");
            String message = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_KICK_CHAT, modName, target.get().getName(), reason);
            DataCache.getAdminChannel().push(message, null);
            target.get().kickPlayer(reason, sender.orElse(null));
        }
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.KICK_PERMISSION;
    }
}
