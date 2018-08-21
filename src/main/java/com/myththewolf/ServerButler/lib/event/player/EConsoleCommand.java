package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Arrays;

/**
 * This class captures all Pre-Console command events
 */
public class EConsoleCommand implements Listener, Loggable {
    @EventHandler
    public void onConsoleCommand(ServerCommandEvent event) {
        String[] split = event.getCommand().split(" ");
        String[] args = Arrays.copyOfRange(split,1,split.length-1);

    }
}
