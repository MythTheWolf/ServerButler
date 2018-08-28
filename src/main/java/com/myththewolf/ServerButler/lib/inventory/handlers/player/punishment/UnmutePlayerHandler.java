package com.myththewolf.ServerButler.lib.inventory.handlers.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class UnmutePlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        String args[] = {data.getString("PLAYER-NAME")};
        ServerButler.commands.get("unmute").setLastPlayer(player);
        ServerButler.commands.get("unmute").onCommand(Optional.ofNullable(player), args, tar);
    }
}
