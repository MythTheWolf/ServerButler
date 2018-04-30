package com.myththewolf.ServerButler.commands.admin.InetAddr;

import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class inetTempBan extends CommandAdapter implements Loggable{
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
    }

    @Override
    public String getUsage() {
        return "/ipban <username> [period string] [reason..]";
    }

    @Override
    public int getNumRequiredArgs() {
        return 1;
    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.BAN__IP_PERMISSION;
    }
}
