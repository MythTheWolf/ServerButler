package com.myththewolf.ServerButler.lib.player.interfaces;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;

import java.util.List;
import java.util.Optional;

/**
 * This interface is for any classes that contain a ChatChannel viewer
 */
public interface ChannelViewer {
    /**
     * Gets the player's writing channel
     *
     * @return A Optional, empty if there is no writing channel
     */
    Optional<ChatChannel> getWritingChannel();

    /**
     * Sets the player's writing channel
     *
     * @param channel The channel to write to
     */
    void setWritingChannel(ChatChannel channel);

    /**
     * Gets a list of the player's active channels
     *
     * @return The list of channels (can be empty)
     */
    List<ChatChannel> getChannelList();

    /**
     * Gets the player's chat status
     *
     * @return The player's chat status
     */
    ChatStatus getChatStatus();

    /**
     * Sets this player's chat status
     *
     * @param chatStatus The status to set to
     */
    void setChatStatus(ChatStatus chatStatus);

    /**
     * Checks if this player is viewing a channel
     *
     * @param channel The channel to check
     * @return True if the player is viewing the channel
     */
    default boolean isViewing(ChatChannel channel) {
        return getChannelList().contains(channel);
    }

    /**
     * Checks if this player is permitted to chat
     *
     * @return True if a player is not muted and can chat
     */
    default boolean canChat() {
        return getChatStatus().equals(ChatStatus.PERMITTED);
    }

    /**
     * Adds a channel to the player's channel list
     *
     * @param channel The channel to add
     */
    void openChannel(ChatChannel channel);

    /**
     * Removes a channel from the player's channel list
     *
     * @param channel
     */
    void closeChannel(ChatChannel channel);
}
