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

public class unmute extends CommandAdapter {
    @Override
    @CommandPolicy(userRequiredArgs = 1, consoleRequiredArgs = 2, commandUsage = "/unmute <player> <reason>")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found: " + args[0]);
            return;
        }
        if (target.get().equals(sender.get())) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You cannot mute yourself.");
            return;
        }
        if (args.length == 1) {
            ServerButler.conversationBuilder.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return ConfigProperties.PREFIX + "Please specify the unmute reason:";
                }

                @Override
                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                    conversationContext.setSessionData("packetType", PacketType.UNMUTE_PLAYER);
                    conversationContext.setSessionData("reason", s);
                    conversationContext.setSessionData("sender", sender.orElse(null));
                    conversationContext.setSessionData("target", target.get());
                    return END_OF_CONVERSATION;
                }
            }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();
        } else {
            String reason = StringUtils.arrayToString(1, args);
            target.get().unmutePlayer(reason, sender.orElse(null));
            target.get().updatePlayer();
            String toSend = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_MUTE_CHAT, sender.map(MythPlayer::getName)
                            .orElse("CONSOLE"), target.map(MythPlayer::getName)
                            .orElse("<ERROR: MythPlayer not present>"), reason);
            DataCache.getAdminChannel().push(toSend, null);
            String playerMessage = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_UNMUTE, sender.map(MythPlayer::getName)
                            .orElse("CONSOLE"), reason);
            target.get().getBukkitPlayer().ifPresent(player -> player.sendMessage(playerMessage));
        }

    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.UNMUTE_PERMISSION;
    }
}
