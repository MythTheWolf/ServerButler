package com.myththewolf.ServerButler.commands.player;

import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class token extends CommandAdapter implements SQLAble {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (!sender.isPresent()) {
            return;
        }

        MythPlayer player = sender.get();
        if (player.getDiscordID().isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "You are already linked!");
            return;
        }
        reply(ConfigProperties.PREFIX + "Generating key...");
        String key = StringUtils.getToken(10);
        reply(ConfigProperties.PREFIX + "Done! Use this command in discord: ;" + "link " + key);

        prepareAndExecuteUpdateExceptionally("INSERT INTO `SB_Discord` (`token`,`UUID`) VALUES (?,?)", 2, key, player
                .getUUID());
    }

}
