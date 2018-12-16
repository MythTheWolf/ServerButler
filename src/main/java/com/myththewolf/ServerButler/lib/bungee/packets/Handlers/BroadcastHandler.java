package com.myththewolf.ServerButler.lib.bungee.packets.Handlers;

import com.myththewolf.ServerButler.lib.bungee.packets.BungeePacketHandler;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.json.JSONObject;

public class BroadcastHandler implements BungeePacketHandler, Loggable {
    @Override
    public void onPacket(JSONObject data) {
        getLogger().info(data.getString("sender") + " did a thing:" + data.getString("message"));
    }
}
