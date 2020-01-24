package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EPlayerMove implements Listener {
    @EventHandler()
    public void onMove(PlayerMoveEvent event) {
        MythPlayer mythPlayer = DataCache.getPlayer(event.getPlayer().getUniqueId().toString()).orElseThrow(IllegalStateException::new);
        if (ConfigProperties.ENABLE_EULA && !mythPlayer.tosAccepted()) {
            event.setCancelled(true);
        }
    }
}
