package com.myththewolf.ServerButler.lib.inventory.interfaces;


import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

/**
 * This interface is implemented by handlers, which register specific packet types
 */
public interface ItemPacketHandler {
    /**
     * Runs when a packet of the registered type is recived by a player's InventoryClickEvent
     * @param player The player who initiated the packet reception
     * @param data The data received from the packet
     */
    void onPacketReceived(MythPlayer player, JSONObject data);
}
