package com.myththewolf.ServerButler.lib.inventory.interfaces;

import com.myththewolf.ServerButler.lib.player.impl.IMythPlayer;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public interface ItemPacketHandler {
    public void onPacketReceived(MythPlayer player, JSONObject data);
}
