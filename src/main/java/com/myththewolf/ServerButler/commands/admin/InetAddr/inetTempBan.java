package com.myththewolf.ServerButler.commands.admin.InetAddr;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetTempBan;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class inetTempBan extends CommandAdapter implements Loggable {
    String DATE_STRING;
    String REASON;
    String[] supArgs;
    @Override
    @CommandPolicy(consoleRequiredArgs = 3, userRequiredArgs = 1, commandUsage = "/iptempban <username|ip address> [period string] [reason..]")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {

       if(args.length == 1){
           PlayerInetAddress target;
          reply("Please specify the target");
           EPlayerChat.inputs.put(sender.get().getUUID(),content -> {
              args[0] = content;
               reply("Please specify the ban duration");
               EPlayerChat.inputs.put(sender.get().getUUID(),content1 -> {
                  args[1] = content1;
                   reply("Please specify the ban reason");
                  EPlayerChat.inputs.put(sender.get().getUUID(),content2 -> {
                     args[2] = content2;
                  });
               });
           });
                debug(Arrays.toString(args));
           return;
       }
    }
    public void doThing(Optional<MythPlayer> sender,String[] args){
        PlayerInetAddress target;
        if (args[0].startsWith("/")) {
            Optional<PlayerInetAddress> optionalInetAddress = DataCache.getPlayerInetAddressByIp(args[0]);
            if (!optionalInetAddress.isPresent()) {
                sender.flatMap(MythPlayer::getBukkitPlayer).ifPresent(player -> player.sendMessage(ChatColor.RED+"That IP was not found."));
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
        Period p = TimeUtils.TIME_INPUT_FORMAT().parsePeriod(DATE_STRING);
        ActionInetTempBan actionInetTempBan = new ActionInetTempBan(REASON, (new DateTime())
                .withPeriodAdded(p, 1), target, sender
                .orElse(null));
        actionInetTempBan.update();

        target.setLoginStatus(LoginStatus.TEMP_BANNED);
        target.update();

        String KICK_MESSAGE = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_IP_TEMPBAN, target.getAddress().toString(), sender
                        .map(MythPlayer::getName).orElse("CONSOLE"), REASON, TimeUtils
                        .dateToString(actionInetTempBan.getExpireDate().get()));
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

}
