package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.joda.time.DateTime;

import java.util.Optional;

public class PlayerConversationAbandonedEvent implements ConversationAbandonedListener, Loggable {
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent conversationAbandonedEvent) {
        String reason = (String) conversationAbandonedEvent.getContext().getSessionData("reason");
        MythPlayer target = (MythPlayer) conversationAbandonedEvent.getContext().getSessionData("target");
        Optional<MythPlayer> sender = Optional
                .ofNullable((MythPlayer) conversationAbandonedEvent.getContext().getSessionData("sender"));
        switch ((PacketType) conversationAbandonedEvent.getContext().getSessionData("packetType")) {
            case BAN_PLAYER:
                target.banPlayer(reason, sender.orElse(null));
                ChatChannel adminChat = DataCache.getAdminChannel();
                String CHAT_MESSAGE = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_BAN_CHAT, (sender.isPresent() ? sender.get()
                                .getName() : "CONSOLE"), target.getName(), reason);

                adminChat.push(CHAT_MESSAGE, null);
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
                DataCache.getAdminChannel().push(ChatMessage, null);
                break;
            case PARDON_PLAYER:
                target.pardonPlayer(reason, sender.orElse(null));
                adminChat = DataCache.getAdminChannel();
                CHAT_MESSAGE = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_PARDON_CHAT, (sender.isPresent() ? sender.get()
                                .getName() : "CONSOLE"), target.getName(), reason);

                adminChat.push(CHAT_MESSAGE, null);
                break;
            case KICK_PLAYER:
                String modName = sender.map(MythPlayer::getName).orElse("CONSOLE");
                String message = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_KICK_CHAT, modName, target
                                .getName(), reason);
                DataCache.getAdminChannel().push(message, null);
                target.kickPlayer(reason, sender.orElse(null));
                break;
            case MUTE_PLAYER:
                target.mutePlayer(reason, sender.orElse(null));
                target.updatePlayer();
                String toSend = StringUtils
                        .replaceParameters(ConfigProperties.FORMAT_MUTE_CHAT, sender.map(MythPlayer::getName)
                                .orElse("CONSOLE"), target.getName(), reason);
                DataCache.getAdminChannel().push(toSend, null);
                break;
        }

    }
}