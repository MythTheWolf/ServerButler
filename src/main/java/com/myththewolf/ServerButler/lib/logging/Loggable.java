package com.myththewolf.ServerButler.lib.logging;

import org.bukkit.Bukkit;

import java.util.logging.Logger;

/**
 * Simple logger interface, so we can easily get loggers
 */
public interface Loggable {
    /**
     * Gets the logger for the plugin
     * @return The logger
     */
    default Logger getLogger(){
        return Bukkit.getPluginManager().getPlugin("ServerButler").getLogger();
    }

    default void handleExceptionPST(Exception exception){
        handleException(exception);
        exception.printStackTrace();
    }

    default void handleException(Exception exception){
        getLogger().severe("Internal exception: "+exception.getMessage());
    }
}
