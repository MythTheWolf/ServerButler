package com.myththewolf.ServerButler.lib.command.impl;

import com.myththewolf.ServerButler.lib.command.interfaces.Commandable;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;

/**
 * This class represents a extendable command handler
 */
public abstract class CommandAdapter implements Commandable, Loggable {
    /**
     * The last player who ran this command, used for {@link CommandAdapter#reply(String)}
     */
    private MythPlayer lastPlayer;

    /**
     * Sends a message to the player
     *
     * @param content The message
     */
    public void reply(String content) {
        if (getLastPlayer() == null) {
            getLogger().info(content);
            return;
        }
        getLastPlayer().getBukkitPlayer().ifPresent(player -> player.sendMessage(content));
    }

    /**
     * Gets the last known player who ran this command
     *
     * @return The player
     */
    public MythPlayer getLastPlayer() {
        return lastPlayer;
    }

    /**
     * Updates the lastPlayer
     *
     * @param lastPlayer The new player
     */
    public void setLastPlayer(MythPlayer lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    /**
     * Gets the required permissions
     *
     * @return The permission
     */
    public String getRequiredPermission() {
        return null;
    }
}
