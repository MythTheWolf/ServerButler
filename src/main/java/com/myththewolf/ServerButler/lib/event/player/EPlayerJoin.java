package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.MythTPSWatcher;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetPardon;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ActionType;
import com.myththewolf.ServerButler.lib.moderation.interfaces.ModerationAction;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.util.logging.ExceptionLogger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.IllformedLocaleException;
import java.util.Optional;

/**
 * This class captures all join events
 */
public class EPlayerJoin implements Listener, Loggable, SQLAble {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try {
            if (!ServerButler.connector.isConnected()) {
                throw new Exception("No connection to database");
            }
            MythPlayer MP;

            if (!DataCache.getPlayer(event.getPlayer().getUniqueId().toString()).isPresent()) {
                MP = DataCache.createPlayer(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
                ConfigProperties.EULA
                        .ifPresent(s -> event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', s)));
            } else {
                MP = DataCache.getPlayer(event.getPlayer().getUniqueId().toString()).get();
            }
            Optional<PlayerInetAddress> ipAddress = DataCache
                    .getPlayerInetAddressByIp(event.getPlayer().getAddress().getAddress().toString());

            if (ConfigProperties.ENABLE_DISCORD_BOT && MP.getDiscordID().isPresent()) {
                final MythPlayer MPL = MP;
                DataCache.getAllChannels().stream()
                        .filter(chatChannel -> !chatChannel.getPermission().isPresent() || MPL
                                .hasPermission(chatChannel.getPermission().get())).forEach(chatChannel -> {
                    ServerButler.API.getUserById(MPL.getDiscordID().get()).thenAccept(user -> {
                        PermissionsBuilder pb = new PermissionsBuilder();
                        pb.setAllowed(PermissionType.READ_MESSAGE_HISTORY, PermissionType.READ_MESSAGES, PermissionType.SEND_MESSAGES);
                        chatChannel.getDiscordChannel().asServerTextChannel().get().createUpdater()
                                .addPermissionOverwrite(user, pb.build()).update().exceptionally(ExceptionLogger.get());
                    });
                });
            }

            if (!MP.getName().equals(event.getPlayer().getName())) {
                MP.setName(event.getPlayer().getName());
                MP.updatePlayer();
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask(ServerButler.plugin, new Runnable() {
                public void run() {
                    MythPlayer mp = DataCache.getPlayer(event.getPlayer().getUniqueId().toString()).orElseThrow(IllegalStateException::new);
                    if (!mp.getDisplayName().equals(event.getPlayer().getDisplayName())) {
                        mp.setDisplayName(event.getPlayer().getDisplayName());
                        mp.updatePlayer();
                        DataCache.rebuildPlayer(mp.getUUID());
                    }
                }
            }, 20L);
            MP = DataCache.getPlayer(event.getPlayer().getUniqueId().toString()).orElseThrow(IllegalStateException::new);
            if (!ipAddress.isPresent()) {
                DataCache.addNewInetAddress(event.getPlayer().getAddress().getAddress(), MP);
                DataCache.rebuildPlayer(event.getPlayer().getUniqueId().toString());
                MP = DataCache.getPlayer(event.getPlayer().getUniqueId().toString()).orElseThrow(IllegalStateException::new);
                ipAddress = DataCache
                        .getPlayerInetAddressByIp(event.getPlayer().getAddress().getAddress().toString());
            }

            if (!ipAddress.get().getMappedPlayers().contains(MP)) {
                getLogger().info("Adding " + MP.getUUID() + " to " + ipAddress.get().toString());
                ipAddress.get().addPlayer(MP);
                ipAddress.get().update();
                DataCache.rebuildPlayerInetAddress(ipAddress.get());
                DataCache.rebuildPlayer(MP.getUUID());
                MP = DataCache.playerExists(event.getPlayer().getUniqueId().toString()) ? DataCache
                        .getPlayer(event.getPlayer().getUniqueId().toString()).orElseThrow(IllegalStateException::new) : DataCache
                        .createPlayer(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
                ipAddress = DataCache
                        .getPlayerInetAddressByIp(event.getPlayer().getAddress().getAddress().toString());
            }

            if (!MP.getWritingChannel().isPresent()) {
                MP.setWritingChannel(DataCache.getGlobalChannel());
                MP.updatePlayer();
            }

            if (!MP.isViewing(DataCache.getGlobalChannel())) {
                MP.openChannel(DataCache.getGlobalChannel());
            }
            if (MP.getLoginStatus() != LoginStatus.PERMITTED) {
                if (MP.getLoginStatus() == LoginStatus.BANNED) {
                    ModerationAction action = MP.getLatestActionOfType(ActionType.BAN).orElse(null);
                    if (action == null) {
                        MP.kickPlayer("You have been banned from the server", null);
                        DataCache.getAdminChannel()
                                .push(ChatColor.RED + "&bRejected connection for player &6'" + MP
                                        .getName() + "'&b because they are banned.");
                        return;
                    }
                    String kickReason = StringUtils
                            .replaceParameters(ConfigProperties.FORMAT_BAN, action.getReason(), (action
                                    .getModeratorUser().isPresent() ? action.getModeratorUser().get()
                                    .getName() : "CONSOLE"));
                    DataCache.getAdminChannel()
                            .push(ChatColor.RED + "&bRejected connection for player &6'" + MP
                                    .getName() + "'&b because they are banned.");
                    MP.kickPlayerRaw(kickReason);
                    return;
                } else if (MP.getLoginStatus() == LoginStatus.TEMP_BANNED) {
                    ModerationAction moderationAction = MP.getLatestActionOfType(ActionType.TEMP_BAN).orElse(null);
                    if (moderationAction == null) {
                        MP.kickPlayer("You have been temp banned from the server", null);
                        DataCache.getAdminChannel()
                                .push(ChatColor.RED + "&bRejected connection for player &6'" + MP
                                        .getName() + "'&b because they are temp-banned. ");
                        return;
                    }
                    if (moderationAction.getExpireDate().get().isBeforeNow()) {
                        MP.pardonPlayer("The tempban has expired.", null);
                        return;
                    }
                    String REASON = moderationAction.getReason();
                    String MOD_NAME = moderationAction.getModeratorUser().map(MythPlayer::getName).orElse("CONSOLE");
                    String EXPIRE = moderationAction.getExpireDate().map(TimeUtils::dateToString).orElse("[error]");
                    DataCache.getAdminChannel()
                            .push(ChatColor.RED + "&bRejected connection for player &6'" + MP
                                    .getName() + "'&b because they are temp-banned until" + TimeUtils
                                    .dateToString(moderationAction.getExpireDate()
                                            .orElseThrow(IllegalStateException::new)));
                    MP.kickPlayerRaw(StringUtils
                            .replaceParameters(ConfigProperties.FORMAT_TEMPBAN, MOD_NAME, REASON, EXPIRE));
                    return;
                }
            }

            if (!MP.getConnectionAddress().get().getLoginStatus().equals(LoginStatus.PERMITTED)) {
                switch (MP.getConnectionAddress().get().getLoginStatus()) {
                    default:
                        break;
                    case BANNED:
                        PlayerInetAddress playerInetAddress = MP.getConnectionAddress()
                                .orElseThrow(IllegalStateException::new);
                        String reason = playerInetAddress.getLatestActionOfType(ActionType.BAN)
                                .orElseThrow(IllegalStateException::new).getReason();
                        Optional<MythPlayer> sender = playerInetAddress.getLatestActionOfType(ActionType.BAN)
                                .orElseThrow(IllegalStateException::new).getModeratorUser();
                        String KICK_REASON = StringUtils
                                .replaceParameters(ConfigProperties.FORMAT_IP_BAN, playerInetAddress.getAddress()
                                        .toString(), sender
                                        .map(MythPlayer::getName).orElse("CONSOLE"), reason);
                        MP.kickPlayerRaw(KICK_REASON);
                        DataCache.getAdminChannel()
                                .push(ChatColor.RED + "&bRejected connection for player &6'" + MP
                                        .getName() + "'&b because their IP,&6" + MP.getConnectionAddress()
                                        .orElseThrow(IllegalStateException::new).getAddress()
                                        .toString() + "&b, is banned.");
                        break;
                    case TEMP_BANNED:
                        PlayerInetAddress src = MP.getConnectionAddress().orElseThrow(IllegalStateException::new);
                        ModerationAction action = src
                                .getLatestActionOfType(ActionType.TEMP_BAN).orElseThrow(IllegalStateException::new);
                        if (action.getExpireDate().orElseThrow(IllegalStateException::new).isBeforeNow()) {
                            ModerationAction actionUnbanIp = new ActionInetPardon("The IP's tempban has expired.", src, null);
                            ((ActionInetPardon) actionUnbanIp).update();
                            src.setLoginStatus(LoginStatus.PERMITTED);
                            src.update();
                            DataCache.rebuildPlayerInetAddress(src);
                            DataCache.rebuildPlayer(MP.getUUID());
                            return;
                        }
                        KICK_REASON = StringUtils
                                .replaceParameters(ConfigProperties.FORMAT_IP_TEMPBAN, src.getAddress()
                                        .toString(), action.getModeratorUser().map(MythPlayer::getName)
                                        .orElse("CONSOLE"), action.getReason(), TimeUtils
                                        .dateToString(action.getExpireDate().orElseThrow(IllegalStateException::new)));
                        MP.kickPlayerRaw(KICK_REASON);
                        DataCache.getAdminChannel()
                                .push(ChatColor.RED + "&bRejected connection for player &6'" + MP
                                        .getName() + "'&b because their IP,&6" + MP.getConnectionAddress()
                                        .orElseThrow(IllegalStateException::new).getAddress()
                                        .toString() + "&b, is temp-banned until &6" + TimeUtils
                                        .dateToString(action.getExpireDate()
                                                .orElseThrow(IllformedLocaleException::new)));
                        break;
                }
                return;
            } else {
                MP.getChannelList().forEach(DataCache::rebuildChannel);
            }
            if (ConfigProperties.ENABLE_DISCORD_BOT) {
                DataCache.getGlobalChannel().getDiscordChannel().sendMessage(":arrow_forward: " + ChatColor.stripColor(MP.getDisplayName()) + " joined the game.");
                DataCache.getAllChannels().forEach(chatChannel -> {
                    chatChannel.getDiscordChannel().asServerTextChannel().orElseThrow(IllegalStateException::new).updateTopic(Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + " players | " + Math.floor(MythTPSWatcher.getTPS()) + " TPS | Server online for " + TimeUtils.durationToString(new Duration(ServerButler.startTime, DateTime.now()))).exceptionally(ExceptionLogger.get());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            event.getPlayer()
                    .kickPlayer(ConfigProperties.PREFIX + ChatColor.RED + "Internal error while accepting player connection event: " + e
                            .getMessage());
        }

    }
}
