package com.myththewolf.ServerButler.lib.event.player.Discord;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.DiscordCommandAdapter;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.Bukkit;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

public class DiscordMessageEvent implements MessageCreateListener, Loggable {
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessage().getAuthor().isYourself()) {
            return;
        }
        if (messageCreateEvent.getPrivateChannel().isPresent()) {
            return;
        }
        boolean isMcChannel = DataCache.getAllChannels().stream()
                .anyMatch(chatChannel -> chatChannel.getDiscordChannel() != null && chatChannel.getDiscordChannel()
                        .getId() == messageCreateEvent.getChannel()
                        .getId());

        if (!isMcChannel) {
            String[] split = messageCreateEvent.getMessage().getContent().split(" ");
            String[] args;
            args = Arrays.copyOfRange(split, 1, split.length);
            Optional<DiscordCommandAdapter> commandAdapter = Optional
                    .ofNullable(ServerButler.discordCommands.get(split[0]));
            commandAdapter.ifPresent(discordCommandAdapter -> {
                discordCommandAdapter.setLastChannel(messageCreateEvent.getChannel());
                Thread T = new Thread(() -> {
                    discordCommandAdapter.onCommand(messageCreateEvent.getMessage(), messageCreateEvent
                            .getChannel(), messageCreateEvent.getServer()
                            .orElse(null), DataCache
                            .getPlayerByDiscordID(messageCreateEvent.getMessage().getAuthor().getIdAsString()), args);
                });
                T.start();
            });
            return;
        }

        DataCache.getAllChannels().stream()
                .filter(chatChannel -> chatChannel.getDiscordChannel().getId() == messageCreateEvent.getChannel()
                        .getId()).findAny().ifPresent(chatChannel -> {

            if (chatChannel.getName().equals("CONSOLE")) {
                Bukkit.getScheduler().callSyncMethod(ServerButler.plugin, () -> ServerButler.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), messageCreateEvent.getMessageContent()));
                // messageCreateEvent.deleteMessage().join();
                return;
            } else {
                DataCache.getPlayerByDiscordID(messageCreateEvent.getMessage().getAuthor().getIdAsString())
                        .ifPresent(mythPlayer -> {
                            messageCreateEvent.getMessage().getAttachments().stream().filter(MessageAttachment::isImage).map(MessageAttachment::getProxyUrl).map(URL::toString).forEach(s -> chatChannel.pushViaDiscord(s, mythPlayer));
                            chatChannel.pushViaDiscord(messageCreateEvent.getMessage().getContent(), mythPlayer);


                        });
            }
            messageCreateEvent.deleteMessage().exceptionally(ExceptionLogger.get());
        });

    }
}
