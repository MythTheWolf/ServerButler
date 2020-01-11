package com.myththewolf.ServerButler.lib.inventory.handlers.player.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class KickPlayerHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getPlayer(data.getString("PLAYER-NAME")).orElseThrow(IllegalStateException::new);
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        String[] args = {target.getName()};
        ServerButler.commands.get("kick").setLastPlayer(player);
        ServerButler.commands.get("kick").onCommand(Optional.ofNullable(player), args, tar);
    }
}
