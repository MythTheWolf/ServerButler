package com.myththewolf.ServerButler.lib.inventory.handlers.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class PardonPlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        String args[] = {data.getString("PLAYER-NAME")};
        ServerButler.commands.get("pardon").setLastPlayer(player);
        ServerButler.commands.get("pardon").onCommand(Optional.ofNullable(player), args, tar);
    }
}
