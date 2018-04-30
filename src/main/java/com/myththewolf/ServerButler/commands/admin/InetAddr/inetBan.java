package com.myththewolf.ServerButler.commands.admin.InetAddr;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
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
    String reason;
    PlayerInetAddress playerInetAddress;
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {


        if (!sender.isPresent() && !(args.length == 2)) {
            getLogger().warning("Console usage for command /ipban: /ipban <username> <reason>");
            return;
        }
        String IP = args[0].charAt(0) == '/' ? args[0] : "/" + args[0];
        Optional<PlayerInetAddress> target = DataCache.getPlayerInetAddressByIp(IP);
        if (!target.isPresent()) {
            if (sender.isPresent()) {
                sender.get().getBukkitPlayer().ifPresent(player -> player
                        .sendMessage(ConfigProperties.PREFIX + ChatColor.DARK_RED + "Could not find that IP: " + IP));
            }
            getLogger().warning("IP not found: " + IP);
            return;
        }

        if (sender.isPresent() && args.length == 1) {
            sender.get().getBukkitPlayer().get()
                    .sendMessage(ConfigProperties.PREFIX + "Please give the reason for the ban: ");
            EPlayerChat.inputs.put(sender.get().getUUID(), content -> {
                reason = content;
                doThing(target,sender);
            });
            return;
        } else {
            reason = args[1];
            doThing(target,sender);
        }
    }
    private void doThing(Optional<PlayerInetAddress> target, Optional<MythPlayer> sender){
        target.ifPresent(playerInetAddress -> {
            playerInetAddress.setLoginStatus(LoginStatus.BANNED);
            playerInetAddress.update();
            ActionInetBan actionInetBan = new ActionInetBan(reason, playerInetAddress, sender.orElse(null));
            actionInetBan.update();
            String affectedPlayers = StringUtils
                    .serializeArray(playerInetAddress.getMappedPlayers().stream().map(MythPlayer::getName)
                            .collect(Collectors.toList()));
            String KICK_REASON = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_IP_BAN, playerInetAddress.getAddress().toString(), sender
                            .map(MythPlayer::getName).orElse("CONSOLE"), reason);
            String CHAT_MESSAGE = StringUtils
                    .replaceParameters(ConfigProperties.FORMAT_IPBAN_CHAT, playerInetAddress.getAddress()
                            .toString(), sender.map(MythPlayer::getName).orElse("CONSOLE"), reason, affectedPlayers);
            playerInetAddress.getMappedPlayers().stream().filter(MythPlayer::isOnline)
                    .forEach(mp -> mp.kickPlayerRaw(KICK_REASON));
            DataCache.getAdminChannel().push(CHAT_MESSAGE, null);
            DataCache.rebuildPlayerInetAddress(playerInetAddress);
        });
    }
    @Override
    public int getNumRequiredArgs() {
        return 1;
    }

    @Override
    public String getUsage() {
        return "/ipban <IP address> [reason]";
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.BAN__IP_PERMISSION;
    }
}

