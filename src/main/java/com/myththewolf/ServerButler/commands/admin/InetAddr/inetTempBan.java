package com.myththewolf.ServerButler.commands.admin.InetAddr;

import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class inetTempBan extends CommandAdapter implements Loggable {
    @Override
    @CommandPolicy(consoleRequiredArgs = 3, userRequiredArgs = 1, commandUsage = "/ipban <username> [period string] [reason..]")
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
    }
}
