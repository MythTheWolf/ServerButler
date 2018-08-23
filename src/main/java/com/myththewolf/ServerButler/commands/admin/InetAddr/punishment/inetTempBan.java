package com.myththewolf.ServerButler.commands.admin.InetAddr.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetTempBan;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Optional;
import java.util.stream.Collectors;

public class inetTempBan extends CommandAdapter implements Loggable {
    String DATE_STRING;
    String REASON;
    PlayerInetAddress target;

    @Override
    @CommandPolicy(consoleRequiredArgs = 3, userRequiredArgs = -1, commandUsage = "/iptempban <username|/{ip address}> [period string] [reason..]")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (args.length <= 1) {
            ServerButler.conversationBuilder.withEscapeSequence("^c")
                    .thatExcludesNonPlayersWithMessage("Must be in-game for this command")
                    .withFirstPrompt(new RegexPrompt("((\\d{1,2}y\\s?)?(\\d{1,2}mo\\s?)?(\\d{1,2}w\\s?)?(\\d{1,2}d\\s?)?(\\d{1,2}h\\s?)?(\\d{1,2}m\\s?)?(\\d{1,2}s\\s?)?)|\\d{1,2}") {
                        @Override
                        protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                            DATE_STRING = s;
                            return new StringPrompt() {
                                @Override
                                public String getPromptText(ConversationContext conversationContext) {
                                    return ConfigProperties.PREFIX + "Please specify the reason";
                                }

                                @Override
                                public Prompt acceptInput(ConversationContext conversationContext, String s) {
                                    REASON = s;
                                    return Prompt.END_OF_CONVERSATION;
                                }
                            };
                        }

                        @Override
                        public String getPromptText(ConversationContext conversationContext) {
                            return ConfigProperties.PREFIX + "Please specify the time for the ban. (Format: 1d 2h...)";
                        }
                    }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();
        } else {
            if (!args[1]
                    .matches("((\\d{1,2}y\\s?)?(\\d{1,2}mo\\s?)?(\\d{1,2}w\\s?)?(\\d{1,2}d\\s?)?(\\d{1,2}h\\s?)?(\\d{1,2}m\\s?)?(\\d{1,2}s\\s?)?)|\\d{1,2}")) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "Invalid date string format: " + args[1]);
                return;
            }
            REASON = StringUtils.arrayToString(2, args);
            if (args[0].startsWith("/")) {
                Optional<PlayerInetAddress> optionalInetAddress = DataCache.getPlayerInetAddressByIp(args[0]);
                if (!optionalInetAddress.isPresent()) {
                    sender.flatMap(MythPlayer::getBukkitPlayer)
                            .ifPresent(player -> player.sendMessage(ChatColor.RED + "That IP was not found."));
                    sender.orElseGet(() -> {
                        getLogger().warning("IP address not found!");
                        return null;
                    });
                    return;
                }
                target = optionalInetAddress.get();
            } else {
                Optional<PlayerInetAddress> optionalMythPlayer = DataCache.getPlayerByName(args[0])
                        .flatMap(MythPlayer::getConnectionAddress);
                if (!optionalMythPlayer.isPresent()) {
                    sender.flatMap(MythPlayer::getBukkitPlayer).ifPresent(player -> player
                            .sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "Could not bind to player's connection address! (Are they offline or not existent?)"));
                    sender.orElseGet(() -> {
                        getLogger()
                                .warning("Could not bind to player's connection address! (Are they offline or not existent?)");
                        return null;
                    });
                    return;
                }
                target = optionalMythPlayer.get();
            }
        }
        Period p = TimeUtils.TIME_INPUT_FORMAT().parsePeriod(DATE_STRING);
        ActionInetTempBan actionInetTempBan = new ActionInetTempBan(REASON, (new DateTime())
                .withPeriodAdded(p, 1), target, sender
                .orElse(null));
        actionInetTempBan.update();

        target.setLoginStatus(LoginStatus.TEMP_BANNED);
        target.update();

        String KICK_MESSAGE = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_IP_TEMPBAN, target.getAddress().toString(), sender
                        .map(MythPlayer::getName).orElse("CONSOLE"), TimeUtils
                        .dateToString(actionInetTempBan.getExpireDate().get()), REASON);
        String CHAT_MESSAGE = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_IP_TEMPBAN_CHAT, target.getAddress().toString(), sender
                        .map(MythPlayer::getName).orElse("CONSOLE"), REASON, StringUtils
                        .serializeArray(target.getMappedPlayers().stream().map(MythPlayer::getName)
                                .collect(Collectors.toList())), TimeUtils
                        .dateToString(actionInetTempBan.getExpireDate().get()));
        DataCache.getAdminChannel().push(CHAT_MESSAGE, null);
        target.getMappedPlayers().stream().filter(MythPlayer::isOnline)
                .forEachOrdered(player -> player.kickPlayerRaw(KICK_MESSAGE));
        DataCache.rebuildPlayerInetAddress(target);
    }


    @Override
    public String getRequiredPermission() {
        return ConfigProperties.TEMPBAN_IP_PERMISSION;
    }
}
