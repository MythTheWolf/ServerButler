package com.myththewolf.ServerButler.commands.admin.InetAddr.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class inetBan extends CommandAdapter implements Loggable {
    @Override
    @CommandPolicy(commandUsage = "/ipban <IP address || username> [reason]", userRequiredArgs = 1, consoleRequiredArgs = 2)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<PlayerInetAddress> playerInetAddress;
        if (args[0].startsWith("/")) {
            playerInetAddress = DataCache.getPlayerInetAddressByIp(args[0]);
        } else {
            Optional<MythPlayer> player = DataCache.getPlayerByName(args[0]);
            if (!player.isPresent()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "Could not grab ip: Player not found.");
                return;
            } else if (!player.get().isOnline()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "ERROR: " + ChatColor.GOLD + "The player isn't online, cannot grab IP address. (Use /ips <playername>)");
                return;
            }
            playerInetAddress = DataCache
                    .getOrMakeInetAddress(player.get().getConnectionAddress().orElseThrow(AssertionError::new)
                            .getDatabaseId());
        }
        if (!playerInetAddress.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "IP not found");
            return;
        }
        if (args.length == 1) {
            ServerButler.conversationBuilder.withEscapeSequence("^c").withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return ConfigProperties.PREFIX + "Please specify the reason";
                }

                @Override
                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                    conversationContext.setSessionData("packetType", PacketType.BAN_IP);
                    conversationContext.setSessionData("reason", s);
                    conversationContext.setSessionData("target-ip", playerInetAddress.get());
                    return END_OF_CONVERSATION;
                }
            }).buildConversation(sender.get().getBukkitPlayer().get()).begin();
        }
    }


    @Override
    public String getRequiredPermission() {
        return ConfigProperties.BAN__IP_PERMISSION;
    }
}

