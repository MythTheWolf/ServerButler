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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class jsonImport extends CommandAdapter implements Loggable {
    long totalDone = 0;
    @Override
    @CommandPolicy(commandUsage = "/jsonimport")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        totalDone = 0;
        Thread T = new Thread(() -> {
            reply(ConfigProperties.PREFIX + "Reading ./banned-players.json");
            try {
                JSONArray root = new JSONArray(readFile("banned-players.json",Charset.defaultCharset()));
                root.forEach(o -> {
                    JSONObject ban = (JSONObject) o;
                    if (ban.getString("expires").equals("forever")) {
                        debug("Importing User: "+ban.getString("uuid"));
                        if(DataCache.playerExists(ban.getString("uuid"))){
                            MythPlayer  mp = DataCache.getOrMakePlayer("uuid");
                            mp.banPlayer(ban.getString("reason"),null);
                            mp.updatePlayer();
                            DataCache.rebuildPlayer(mp.getUUID());
                            totalDone++;
                        }else{
                            getLogger().info("Having to manually set: "+ban.getString("name"));
                           MythPlayer mp = DataCache.createPlayer(ban.getString("uuid"),ban.getString("name"));
                          MythPlayer neww = DataCache.getOrMakePlayer(ban.getString("uuid"));
                            neww.banPlayer(ban.getString("reason"),null);
                            neww.updatePlayer();
                            totalDone++;
                        }
                        debug("Imported ban from user '"+ban.getString("name")+"'.");
                    }
                });
                reply("Imported "+totalDone+" bans.");
            } catch (IOException e) {
                e.printStackTrace();
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

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
