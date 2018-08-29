package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class ViewAnnouncementOptionsHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        String args[] = {data.getString("ID")};
        JavaPlugin tar = (JavaPlugin) Bukkit.getPluginManager().getPlugin("ServerButler");
        ServerButler.commands.get("task").setLastPlayer(player);
        ServerButler.commands.get("task").onCommand(Optional.ofNullable(player), args, tar);
    }
}
