package com.myththewolf.ServerButler.lib.command.impl;

import com.myththewolf.ServerButler.lib.command.interfaces.Commandable;
import com.myththewolf.ServerButler.lib.player.impl.MythPlayer;

/**
 * This class represents a extendable command handler
 */
public abstract class CommandAdapter implements Commandable {
    private MythPlayer lastPlayer;

    /**
     * Sends a message to the player
     *
     * @param content The message
     */
    public void reply(String content) {
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
     * Gets number of required args
     *
     * @return The number
     */
    public int getNumRequiredArgs() {
        return 0;
    }

    /**
     * Gets the command usage
     *
     * @return The usage
     */
    public String getUsage() {
        return "<<NOT DEFINED>>";
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
