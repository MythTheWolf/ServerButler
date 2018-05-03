package com.myththewolf.ServerButler.commands.admin.InetAddr;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetBan;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.stream.Collectors;

public class inetBan extends CommandAdapter implements Loggable {
    String REASON;
    String[] args;
    @Override
    @CommandPolicy(commandUsage = "/ipban <IP address || username> [reason]", userRequiredArgs = 1, consoleRequiredArgs = 2)
    public void onCommand(Optional<MythPlayer> sender, String[] Theargs, JavaPlugin javaPlugin) {
        args = Theargs;
        if (args.length == 1 && sender.isPresent()) {
            reply("Please supply a reason:");
            EPlayerChat.inputs.put(sender.get().getUUID(), content -> { REASON = content; doThing(sender); });
            return;
        } else if (args.length > 1) {
            REASON = StringUtils.arrayToString(2, args);
        }
        doThing(sender);
    }
    private void doThing(Optional<MythPlayer> sender){
        PlayerInetAddress target;
        if (args[0].startsWith("/")) {
            Optional<PlayerInetAddress> optionalInetAddress = DataCache.getPlayerInetAddressByIp(args[0]);
            if (!optionalInetAddress.isPresent()) {
                sender.flatMap(MythPlayer::getBukkitPlayer).ifPresent(player -> player
                        .sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "IP address not found"));
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
        ActionInetBan actionInetBan = new ActionInetBan(REASON, target, sender.orElse(null));
        actionInetBan.update();
        target.setLoginStatus(LoginStatus.BANNED);
        DataCache.rebuildPlayerInetAddress(target);
        String KICK_REASON = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_IP_BAN, target.getAddress().toString(), sender
                        .map(MythPlayer::getName).orElse("CONSOLE"), REASON);

        String affected = StringUtils.serializeArray(target.getMappedPlayers().stream().map(MythPlayer::getName)
                .collect(Collectors.toList()));
        String CHAT_MESSAGE = StringUtils
                .replaceParameters(ConfigProperties.FORMAT_IPBAN_CHAT, target.getAddress().toString(), sender
                        .map(MythPlayer::getName).orElse("CONSOLE"), REASON, affected);
        DataCache.getAdminChannel().push(CHAT_MESSAGE, null);

        target.getMappedPlayers().stream().filter(MythPlayer::isOnline)
                .forEachOrdered(player -> player.kickPlayerRaw(KICK_REASON));
    }
}

