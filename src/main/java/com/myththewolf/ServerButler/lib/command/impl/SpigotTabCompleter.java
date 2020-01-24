package com.myththewolf.ServerButler.lib.command.impl;

import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpigotTabCompleter implements TabCompleter, Loggable, SQLAble {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String string, String[] strings) {
        List<String> ret = new ArrayList<>();
        try {
            if (strings[0].startsWith("/")) {
                ResultSet rs = prepareAndExecuteSelectExceptionally("SELECT `address` FROM `SB_IPAddresses` WHERE `address` LIKE ?", 1, strings[0] + "%");
                while (rs.next()) {
                    ret.add(rs.getString("address"));
                }
                return ret;
            }
            ResultSet resultSet = prepareAndExecuteSelectExceptionally("SELECT `name` FROM `SB_Players` WHERE `name` LIKE ?", 1, strings[0] + "%");
            while (resultSet.next()) {
                ret.add(resultSet.getString("name"));
            }
        } catch (SQLException E) {
            E.printStackTrace();
        }
        return ret;
    }
}
