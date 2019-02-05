package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.bungee.packets.BungeePacketType;
import com.myththewolf.ServerButler.lib.bungee.packets.BungeeSender;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class cb extends CommandAdapter implements BungeeSender {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        reply("Sending message..");
        JSONObject object = new JSONObject();
        object.put("message", "TEST!!!");
        sendToAll(BungeePacketType.BROADCAST_MESSAGE, object);
    }
}
