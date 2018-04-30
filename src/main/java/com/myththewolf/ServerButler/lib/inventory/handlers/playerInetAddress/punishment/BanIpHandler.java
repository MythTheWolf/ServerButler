package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class BanIpHandler implements ItemPacketHandler{
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        String[] args = new String[1];
        args[0] = data.getString("ADDRESS");
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        ServerButler.commands.get("ipban").setLastPlayer(player);
        ServerButler.commands.get("ipban").onCommand(Optional.ofNullable(player), args, tar);
    }
}
