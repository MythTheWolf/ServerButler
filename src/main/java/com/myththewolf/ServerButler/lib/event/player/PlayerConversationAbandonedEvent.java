package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.moderation.impl.InetAddr.ActionInetBan;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.Optional;
import java.util.stream.Collectors;

public class PlayerConversationAbandonedEvent implements ConversationAbandonedListener, Loggable {
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent conversationAbandonedEvent) {
        if (!conversationAbandonedEvent.gracefulExit()) {
            conversationAbandonedEvent.getContext().getForWhom()
                    .sendRawMessage(ConfigProperties.PREFIX + "Cancelling.");
            return;
        }
        String reason = (String) conversationAbandonedEvent.getContext().getSessionData("reason");
        MythPlayer target = (MythPlayer) conversationAbandonedEvent.getContext().getSessionData("target");
        Optional<MythPlayer> sender = Optional
                .ofNullable((MythPlayer) conversationAbandonedEvent.getContext().getSessionData("sender"));
        Optional<ChatAnnoucement> annoucement = DataCache
                .getAnnouncement((String) conversationAbandonedEvent.getContext().getSessionData("ID"));
        Conversable conversable = conversationAbandonedEvent.getContext().getForWhom();
        switch ((PacketType) conversationAbandonedEvent.getContext().getSessionData("packetType")) {
            case BAN_PLAYER:
                target.banPlayer(reason, sender.orElse(null));
                ChatChannel adminChat = DataCache.getPunishmentInfoChannel();
                String CHAT_MESSAGE = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_BAN_CHAT, (sender.isPresent() ? sender.get()
                                .getName() : "CONSOLE"), target.getName(), reason);

                adminChat.push(CHAT_MESSAGE);
                String KICK_MESSAGE = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_BAN, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), reason);
                target.kickPlayerRaw(KICK_MESSAGE);
                break;
            case TEMPBAN_PLAYER:
                DateTime expireDate = (DateTime) conversationAbandonedEvent.getContext().getSessionData("expireDate");
                target.tempbanPlayer(sender.orElse(null), reason, expireDate);
                String ChatMessage = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_TEMPBAN_CHAT, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), target.getName(), reason, TimeUtils.dateToString(expireDate));
                DataCache.getPunishmentInfoChannel().push(ChatMessage);
                break;
            case PARDON_PLAYER:
                target.pardonPlayer(reason, sender.orElse(null));
                adminChat = DataCache.getPunishmentInfoChannel();
                CHAT_MESSAGE = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_PARDON_CHAT, (sender.isPresent() ? sender.get()
                                .getName() : "CONSOLE"), target.getName(), reason);

                adminChat.push(CHAT_MESSAGE);
                break;
            case KICK_PLAYER:
                String modName = sender.map(MythPlayer::getName).orElse("CONSOLE");
                String message = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_KICK_CHAT, modName, target
                                .getName(), reason);
                DataCache.getPunishmentInfoChannel().push(message);
                target.kickPlayer(reason, sender.orElse(null));
                break;
            case MUTE_PLAYER:
                target.mutePlayer(reason, sender.orElse(null));
                target.updatePlayer();
                String toSend = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_MUTE_CHAT, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), target.getName(), reason);
                DataCache.getPunishmentInfoChannel().push(toSend);
                String playerMuteMessage = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_MUTE, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), reason);
                target.getBukkitPlayer().ifPresent(player -> player.sendMessage(playerMuteMessage));
            case UNMUTE_PLAYER:
                target.unmutePlayer(reason, sender.orElse(null));
                target.updatePlayer();
                String unmuteMessage = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_UNMUTE_CHAT, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), target.getName(), reason);
                DataCache.getPunishmentInfoChannel().push(unmuteMessage);
                String playerMessage = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_UNMUTE, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), reason);
                target.getBukkitPlayer().ifPresent(player -> player.sendMessage(playerMessage));
                break;
            case UPDATE_CONTENT:
                annoucement.get()
                        .setContent((String) conversationAbandonedEvent.getContext().getSessionData("content"));
                annoucement.get().update();
                conversable.sendRawMessage(ConfigProperties.PREFIX + "Updated.");
                break;
            case UPDATE_PERMISSION:
                annoucement.get()
                        .setRequiredPerm((String) conversationAbandonedEvent.getContext().getSessionData("permission"));
                annoucement.get().update();
                conversable.sendRawMessage(ConfigProperties.PREFIX + "Updated.");
                break;
            case UPDATE_INTERVAL:
                annoucement.get().setInterval(TimeUtils.TIME_INPUT_FORMAT()
                        .parsePeriod((String) conversationAbandonedEvent.getContext().getSessionData("interval")));
                annoucement.get().update();
                conversable.sendRawMessage(ConfigProperties.PREFIX + "Updated.");
                break;
            case CREATE_ANNOUNCEMENT:
                ServerButler.itemPacketHandlers.get(PacketType.CREATE_ANNOUNCEMENT)
                        .forEach(itemPacketHandler -> itemPacketHandler
                                .onPacketReceived((MythPlayer) conversationAbandonedEvent.getContext()
                                        .getSessionData("player"), (JSONObject) conversationAbandonedEvent.getContext()
                                        .getSessionData("packet")));
            case CHANNEL_SELECTION_CONTINUE:
                ServerButler.itemPacketHandlers.get(PacketType.CHANNEL_SELECTION_CONTINUE)
                        .forEach(itemPacketHandler -> itemPacketHandler
                                .onPacketReceived((MythPlayer) conversationAbandonedEvent.getContext()
                                        .getSessionData("player"), (JSONObject) conversationAbandonedEvent.getContext()
                                        .getSessionData("packet")));
                break;
            case BAN_IP:
                PlayerInetAddress targetIp = (PlayerInetAddress) conversationAbandonedEvent.getContext()
                        .getSessionData("target-ip");
                ActionInetBan actionInetBan = new ActionInetBan(reason, targetIp, sender.orElse(null));
                actionInetBan.update();
                targetIp.setLoginStatus(LoginStatus.BANNED);
                targetIp.update();
                DataCache.rebuildPlayerInetAddress(targetIp);
                String KICK_REASON = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_IP_BAN, targetIp.getAddress().toString(), sender
                                .map(MythPlayer::getName).orElse("CONSOLE"), reason);

                String affected = StringUtils
                        .serializeArray(targetIp.getMappedPlayers().stream().map(MythPlayer::getName)
                                .collect(Collectors.toList()));

                CHAT_MESSAGE = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_IPBAN_CHAT, targetIp.getAddress().toString(), sender
                                .map(MythPlayer::getName).orElse("CONSOLE"), reason, affected);
                DataCache.getPunishmentInfoChannel().push(CHAT_MESSAGE);

                targetIp.getMappedPlayers().stream().filter(MythPlayer::isOnline)
                        .forEachOrdered(player -> player.kickPlayerRaw(KICK_REASON));
                break;
            default:
                break;
        }

    }
}
