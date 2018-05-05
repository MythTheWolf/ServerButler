package com.myththewolf.ServerButler.commands;

import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.event.player.EPlayerChat;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class futureTest extends CommandAdapter implements Loggable{
    boolean waiting = true;

    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        EPlayerChat.inputs.put(sender.get().getUUID(), content -> {
            sender.get().getBukkitPlayer().get().sendMessage("1->" + content);

            EPlayerChat.inputs.put(sender.get().getUUID(), content1 -> {
                sender.get().getBukkitPlayer().get().sendMessage("2->" + content1);
                waiting = false;
            });
            while (waiting) {
                //Just  keeping the thread busy
            }
        });
    }
}
