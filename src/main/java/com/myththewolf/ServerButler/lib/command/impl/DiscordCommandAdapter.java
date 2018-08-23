package com.myththewolf.ServerButler.lib.command.impl;

import com.myththewolf.ServerButler.lib.command.interfaces.DiscordCommandable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.util.logging.ExceptionLogger;

public abstract class DiscordCommandAdapter implements DiscordCommandable {
    private TextChannel lastChannel;

    /**
     * Sends a message to the player
     *
     * @param content The message
     */
    public void reply(String content) {
        getLastChannel().sendMessage(content).exceptionally(ExceptionLogger.get());
    }

    /**
     * Gets the last known player who ran this command
     *
     * @return The player
     */
    public TextChannel getLastChannel() {
        return lastChannel;
    }

    /**
     * Updates the lastChannel
     *
     * @param lastChannel The new player
     */
    public void setLastChannel(TextChannel lastChannel) {
        this.lastChannel = lastChannel;
    }


}
