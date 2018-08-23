package com.myththewolf.ServerButler.lib.command.interfaces;

import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.server.Server;

import java.util.Optional;

public interface DiscordCommandable {
    void onCommand(Message orgin, TextChannel channel, Server server, Optional<MythPlayer> sender, String[] args);
}
