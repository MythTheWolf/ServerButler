package com.myththewolf.ServerButler.lib.player.interfaces;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;

import java.util.List;
import java.util.Optional;

public interface ChannelViewer {
    Optional<ChatChannel> getWritingChannel();

    void setWritingChannel(ChatChannel channel);

    public List<ChatChannel> getChannelList();


    ChatStatus getChatStatus();

    void setChatStatus(ChatStatus chatStatus);

    default boolean isViewing(ChatChannel channel) {
        return getChannelList().contains(channel);
    }


    default boolean canChat() {
        return getChatStatus().equals(ChatStatus.PERMITTED);
    }

    void openChannel(ChatChannel channel);

    void closeChannel(ChatChannel channel);
}
