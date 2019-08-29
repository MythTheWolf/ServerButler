package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class version extends CommandAdapter {
    @Override
    @CommandPolicy(commandUsage = "/version")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        final String PLUGIN_VERSION = ServerButler.plugin.getDescription().getVersion();
        final String CACHE_USERS_SIZE = DataCache.playerHashMap.size()+"";
        final String CACHE_IPS_SIZE = DataCache.ipHashMap.size()+"";
        String header = ChatColor.translateAlternateColorCodes('&',"&6------------&8[&bServerButler&8]&6------------");
        StringBuilder reply = new StringBuilder(header);
        reply.append("\n"+ChatColor.RED+"Version:"+ChatColor.AQUA+PLUGIN_VERSION);
        reply.append("\n"+ChatColor.RED+"Total Cached Users: "+ChatColor.AQUA+CACHE_USERS_SIZE);
        reply.append("\n"+ChatColor.RED+"Total Cached IPs: "+ChatColor.AQUA+CACHE_IPS_SIZE);
        reply(reply.toString());
    }
}
