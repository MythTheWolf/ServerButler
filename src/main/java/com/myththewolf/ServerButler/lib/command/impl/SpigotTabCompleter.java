package com.myththewolf.ServerButler.lib.command.impl;

import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class SpigotTabCompleter implements TabCompleter, Loggable {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] strings) {
        List<String> ret = new ArrayList<>();
        if (strings[strings.length - 1].startsWith("/")) {
            DataCache.getAllIps().stream()
                    .filter(s -> s.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase()))
                    .forEach(ret::add);
            return ret;
        }
        DataCache.getPlayerNameMap().keySet().stream()
                .filter(s -> s.toLowerCase().startsWith(strings[strings.length - 1].toLowerCase())).forEach(ret::add);
        return ret;
    }
}
