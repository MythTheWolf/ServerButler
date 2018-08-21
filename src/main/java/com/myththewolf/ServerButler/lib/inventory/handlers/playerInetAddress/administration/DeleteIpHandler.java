package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.administration;

import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class DeleteIpHandler implements ItemPacketHandler,Loggable {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        getLogger().info(data.toString());
    }
}
