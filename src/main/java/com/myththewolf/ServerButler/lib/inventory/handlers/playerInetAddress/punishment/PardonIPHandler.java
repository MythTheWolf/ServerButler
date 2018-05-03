package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class PardonIPHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        String TARGET_IP_ADDRESS = data.getString("ADDRESS");
        Optional<PlayerInetAddress> targetOptional = DataCache.getPlayerInetAddressByIp(TARGET_IP_ADDRESS);
        String[] args = new String[1];
        args[0] = TARGET_IP_ADDRESS;
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        ServerButler.commands.get("ippardon").setLastPlayer(player);
        ServerButler.commands.get("ippardon").onCommand(Optional.ofNullable(player), args, tar);
    }
}
