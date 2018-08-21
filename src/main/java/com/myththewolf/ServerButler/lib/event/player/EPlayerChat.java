package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class captures all chat events from players
 */
public class EPlayerChat implements Listener, Loggable {
    public static HashMap<String, DirectCommandInput> inputs = new HashMap<>();
    /**
     * Used to return if the message was used to send a channel message via the channel's shortcut
     */
    private boolean shortCutRan = false;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        shortCutRan = false;
        event.setCancelled(true);
        if (inputs
                .containsKey(event.getPlayer().getUniqueId().toString())) {
            if(event.getMessage().toLowerCase().equals("^c")){
                event.getPlayer().sendMessage(ConfigProperties.PREFIX+"Cancelling.");
                inputs.remove(event.getPlayer().getUniqueId().toString());
            }else {
                CompletableFuture.supplyAsync(() -> {

                    inputs.get(event.getPlayer().getUniqueId().toString()).onInput(event.getMessage());
                    return null;
                }).thenAccept((o -> {
                    inputs.remove(event.getPlayer().getUniqueId().toString());
                }));
                return;
            }
        }
        MythPlayer sender = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        List<ChatChannel> chanList = new ArrayList<>(sender.getChannelList());
       chanList.stream()
                .filter(chatChannel -> chatChannel.getPermission().isPresent() && !sender.getBukkitPlayer().get()
                        .hasPermission(chatChannel.getPermission().get())).forEach(sender::closeChannel);
       if(!sender.getWritingChannel().isPresent()){
            sender.setWritingChannel(DataCache.getGlobalChannel());
       }

       if(!sender.isViewing(DataCache.getGlobalChannel())){
           sender.openChannel(DataCache.getGlobalChannel());
       }

        if (sender.getChatStatus() != ChatStatus.PERMITTED) {
            if (sender.getChatStatus() == ChatStatus.MUTED) {
                sender.getBukkitPlayer().ifPresent(player -> player
                        .sendMessage(ConfigProperties.PREFIX + "You are not permitted to chat."));
            } else {
                if (sender.getWritingChannel().isPresent()) {
                    sender.getBukkitPlayer().ifPresent(pl -> pl
                            .sendMessage(sender.getWritingChannel().get().getPrefix() + sender.getName() + ": " + event
                                    .getMessage()));
                    DataCache.getAdminChannel().push(ChatColor.GRAY + "[SOFTMUTED: " + sender.getName() + " - " + event
                            .getMessage(), null);
                }
            }
            return;
        }
        DataCache.getAllChannels().stream().filter(channel -> userCanSendTo(sender, channel))
                .filter(channel -> shortcutMatches(channel, event.getMessage())).forEach(channel -> {
            shortCutRan = true;
            channel.push(trimShortcut(event.getMessage(), channel), sender);
        });
        if (shortCutRan) return;

        sender.getWritingChannel().ifPresent(channel -> {channel.push(event.getMessage(), sender); getLogger().info("PUSH");}
        );
    }


    private boolean userCanSendTo(MythPlayer p, ChatChannel C) {
        return !C.getPermission().isPresent() || p.getBukkitPlayer().get().hasPermission(C.getPermission().get());
    }


    private boolean shortcutMatches(ChatChannel C, String in) {
        return !C.getShortcut().isPresent() || in.startsWith(C.getShortcut().get());
    }

    private String trimShortcut(String in, ChatChannel C) {
        return in.substring(C.getShortcut().get().length());
    }
}
