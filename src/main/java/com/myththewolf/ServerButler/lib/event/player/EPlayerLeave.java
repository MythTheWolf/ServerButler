package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class EPlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        MythPlayer MP = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            DataCache.getGlobalChannel().getDiscordChannel().sendMessage(":arrow_backward: " + ChatColor.stripColor(MP.getDisplayName()) + " left the game. (" + event.getQuitMessage() + ")");
        }
    }
}
