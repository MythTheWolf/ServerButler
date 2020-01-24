package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.json.JSONObject;

public class DispatchCommandHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        if (!player.isOnline()) {
            return;
        }
        Bukkit.getServer().dispatchCommand(player.getBukkitPlayer().orElseThrow(IllegalStateException::new), data.getString("command"));
    }
}
