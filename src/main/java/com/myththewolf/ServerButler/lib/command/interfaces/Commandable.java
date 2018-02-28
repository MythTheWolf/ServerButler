package com.myththewolf.ServerButler.lib.command.interfaces;

import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

/**
 * This interface represents a command method
 */
public interface Commandable {
    /**
     * Runs when the command trigger is invoked
     * @param sender The player sender
     * @param args The arguments
     * @param javaPlugin The instance of this plugin
     */
    public void onCommand(Optional<IMythPlayer> sender, String[] args, JavaPlugin javaPlugin);
}
