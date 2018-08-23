package com.myththewolf.ServerButler.lib.inventory.handlers.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class BanPlayerHandler implements ItemPacketHandler, Loggable {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        MythPlayer target = DataCache.getOrMakePlayer(data.getString("PLAYER-UUID"));
            JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        String args[] = {target.getName()};
            ServerButler.commands.get("ban").onCommand(Optional.ofNullable(player), args, tar);

    }
}
