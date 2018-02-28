package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;
import java.util.stream.Collectors;

public class EPlayerChat implements Listener {
    boolean shortCutRan = false;

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        shortCutRan = false;
        MythPlayer player = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
       List<ChatChannel> shortCutted =  DataCache.allChannels.stream()
                .filter(channel -> (!channel.getPermission().isPresent() || (player.getBukkitPlayer().get()
                        .hasPermission(channel.getPermission().get())) && (channel
                        .getShortcut().isPresent() && event.getMessage().startsWith(channel.getShortcut().get())))).collect(Collectors.toList());
       if(shortCutted.size() > 0){
           //TODO: Implement shorcut channel posting
           return;
       }else{

       }

    }
}
