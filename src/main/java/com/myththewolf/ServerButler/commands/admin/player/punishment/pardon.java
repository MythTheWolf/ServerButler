package com.myththewolf.ServerButler.commands.admin.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
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

public class pardon extends CommandAdapter {
    @Override
    @CommandPolicy(consoleRequiredArgs = 2, userRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> target = DataCache.getPlayerByName(args[0]);
        if (!target.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        if (sender.isPresent() && target.get().equals(sender.get())) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You cannot pardon yourself.");
            return;
        }
        if (args.length == 1) {
            ServerButler.conversationBuilder.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return ConfigProperties.PREFIX + "Please specify the pardon reason.";
                }

                @Override
                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                    conversationContext.setSessionData("packetType", PacketType.PARDON_PLAYER);
                    conversationContext.setSessionData("reason", s);
                    conversationContext.setSessionData("sender", sender.orElse(null));
                    conversationContext.setSessionData("target", target.get());
                    return END_OF_CONVERSATION;
                }
            }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();
        } else {
            String reason = StringUtils.arrayToString(1, args);
            target.get().pardonPlayer(reason, sender.orElse(null));
            ChatChannel adminChat = DataCache.getPunishmentInfoChannel();
            String CHAT_MESSAGE = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_PARDON_CHAT, (sender.isPresent() ? sender.get()
                            .getName() : "CONSOLE"), target.get().getName(), reason);

            adminChat.push(CHAT_MESSAGE);
        }

    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.PARDON_PERMISSION;
    }
}
