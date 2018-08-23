package com.myththewolf.ServerButler.commands.admin.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class mute extends CommandAdapter {
    String reason;

    @Override
    @CommandPolicy(commandUsage = "/mute <username> [reason]", consoleRequiredArgs = 2, userRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (args.length == 1) {
            ServerButler.conversationBuilder.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return ConfigProperties.PREFIX + "Please specify the mute reason.";
                }

                @Override
                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                    reason = s;
                    return END_OF_CONVERSATION;
                }
            }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();
        } else {
            reason = StringUtils.arrayToString(1, args);
        }
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        if (target.get().equals(sender.get())) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You cannot mute yourself.");
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
