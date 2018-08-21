package com.myththewolf.ServerButler.commands.admin.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;

import java.util.Optional;

public class tempban extends CommandAdapter implements Loggable {
    DateTime expireDate;
    String reason;
    MythPlayer target;
    @Override
    @CommandPolicy(commandUsage = "/tempban <player name> [period String] [reason]", consoleRequiredArgs = 3, userRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Optional<MythPlayer> targetOp = DataCache.getPlayerByName(args[0]);
        if (!targetOp.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        if(sender.isPresent() && targetOp.get().equals(sender.get())){
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You cannot temp-ban yourself.");
            return;
        }
        target = targetOp.get();
        if (args.length >= 3) {
           if(!args[1].matches("((\\d{1,2}y\\s?)?(\\d{1,2}mo\\s?)?(\\d{1,2}w\\s?)?(\\d{1,2}d\\s?)?(\\d{1,2}h\\s?)?(\\d{1,2}m\\s?)?(\\d{1,2}s\\s?)?)|\\d{1,2}")){
               reply(ConfigProperties.PREFIX+ChatColor.RED+"Invalid date string: "+args[1]);
               return;
           }
           expireDate = new DateTime().withPeriodAdded(TimeUtils.TIME_INPUT_FORMAT().parsePeriod(args[1]),1);
           reason = StringUtils.arrayToString(2,args);
           commit(sender.orElse(null));
        } else {
            ServerButler.conversationBuilder.withEscapeSequence("^c").withFirstPrompt(new RegexPrompt("((\\d{1,2}y\\s?)?(\\d{1,2}mo\\s?)?(\\d{1,2}w\\s?)?(\\d{1,2}d\\s?)?(\\d{1,2}h\\s?)?(\\d{1,2}m\\s?)?(\\d{1,2}s\\s?)?)|\\d{1,2}") {
                @Override
                protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                    expireDate = new DateTime().withPeriodAdded(TimeUtils.TIME_INPUT_FORMAT().parsePeriod(s),1);
                    return new StringPrompt() {
                        @Override
                        public String getPromptText(ConversationContext conversationContext) {
                            return ConfigProperties.PREFIX + "Please specify the ban reason.";
                        }

                        @Override
                        public Prompt acceptInput(ConversationContext conversationContext, String s) {
                            reason = s;
                            commit(sender.orElse(null));
                            return Prompt.END_OF_CONVERSATION;
                        }
                    };

                }

                @Override
                public String getPromptText(ConversationContext conversationContext) {
                    return ConfigProperties.PREFIX + "Please specify the time for the ban. (Format: 1d 2h...)";
                }
            }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();

        }

    }
    private void commit(MythPlayer mod){
        try {
            Thread.sleep(1000);
        }catch (Exception e){
            e.printStackTrace();
        }
        target.tempbanPlayer(mod,reason,expireDate);
        Optional<MythPlayer> modOp = Optional.ofNullable(mod);
        String ChatMessage = StringUtils.replaceParameters(ConfigProperties.FORMAT_TEMPBAN_CHAT,modOp.map(MythPlayer::getName).orElse("CONSOLE"),target.getName(),reason,TimeUtils.dateToString(expireDate));
        DataCache.getAdminChannel().push(ChatMessage,null);
    }
    @Override
    public String getRequiredPermission() {
        return ConfigProperties.TEMPBAN_PERMISSION;
    }

}
