package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * This class captures all Pre-Console command events
 */
public class EConsoleCommand implements Listener, Loggable {
    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        getLogger().info(event.getCommand());
    }
}
