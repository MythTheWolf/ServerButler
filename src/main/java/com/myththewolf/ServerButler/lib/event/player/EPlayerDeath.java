package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.javacord.api.util.logging.ExceptionLogger;

public class EPlayerDeath implements Listener {
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            DataCache.getGlobalChannel().getDiscordChannel().sendMessage(ChatColor.stripColor(event.getDeathMessage())).exceptionally(ExceptionLogger.get());
        }
    }
}
