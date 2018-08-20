package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileReader;
import java.util.Optional;

public class jsonImport extends CommandAdapter implements Loggable {
    @Override
    @CommandPolicy(commandUsage = "/jsonimport")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        Thread T = new Thread(() -> {
            reply(ConfigProperties.PREFIX + "Reading ./banned-players.json");
            try {
                JSONArray root = new JSONArray(new FileReader("banned-players.json"));
                root.forEach(o -> {
                    JSONObject ban = (JSONObject) o;
                    if (ban.getString("expires").equals("forever")) {
                        MythPlayer mythPlayer = DataCache.getOrMakePlayer(ban.getString("uuid"));
                        mythPlayer.banPlayer("(Imported from banned-players.json): " + ban.getString("reason"), sender
                                .orElse(null));
                        mythPlayer.updatePlayer();
                        DataCache.rebuildPlayer(mythPlayer.getUUID());
                        debug("Imported ban from user '"+ban.getString("name")+"'.");
                    }
                });
            } catch (Exception e) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "Could not read file: " + e.getMessage());
            }
        });
        T.setName("SBJSONImporter");
        T.start();
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.IMPORT_JSON_DATA;
    }
}
