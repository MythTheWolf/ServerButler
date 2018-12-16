package com.myththewolf.ServerButler.lib.bungee.packets;

import org.json.JSONObject;

public interface BungeePacketHandler {
    void onPacket(JSONObject data);
}
