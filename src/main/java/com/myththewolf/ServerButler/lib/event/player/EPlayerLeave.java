package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.MythTPSWatcher;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.javacord.api.util.logging.ExceptionLogger;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class EPlayerLeave implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        MythPlayer MP = DataCache.getOrMakePlayer(event.getPlayer().getUniqueId().toString());
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            DataCache.getGlobalChannel().getDiscordChannel().sendMessage(":arrow_backward: " + ChatColor.stripColor(MP.getDisplayName()) + " left the game. (" + event.getQuitMessage() + ")");
            DataCache.getAllChannels().forEach(chatChannel -> {
                chatChannel.getDiscordChannel().asServerTextChannel().orElseThrow(IllegalStateException::new).updateTopic(Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + " players | " + Math.floor(MythTPSWatcher.getTPS()) + " TPS | Server online for " + TimeUtils.durationToString(new Duration(ServerButler.startTime, DateTime.now()))).exceptionally(ExceptionLogger.get());
            });
        }
    }
}
