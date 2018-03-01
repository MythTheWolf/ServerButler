package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class captures all chat events from players
 */
public class EPlayerChat implements Listener {
    /**
     * Used to return if the message was used to send a channel message via the channel's shortcut
     */
    boolean shortCutRan = false;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        shortCutRan = false;
        MythPlayer player = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        List<ChatChannel> shortCut = DataCache.getAllChannels().stream()
                .filter(channel -> (!channel.getPermission().isPresent() || (player.getBukkitPlayer().get()
                        .hasPermission(channel.getPermission().get())) && (channel
                        .getShortcut().isPresent() && event.getMessage().startsWith(channel.getShortcut().get()))))
                .collect(Collectors.toList());
        if (shortCut.size() > 0) {
            shortCut.forEach(chatChannel -> {
                String cut = event.getMessage().substring(chatChannel.getShortcut().get().length());
                chatChannel.push(cut, player);
            });
            return;
        } else {
            player.getWritingChannel().ifPresent(chatChannel -> chatChannel.push(event.getMessage(), player));
        }

    }
}
