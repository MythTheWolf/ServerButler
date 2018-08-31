package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.Optional;

public class StartTaskHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        Optional<ChatAnnoucement> annoucement = DataCache.getAnnouncement(data.getString("ID"));
        Player sender = player.getBukkitPlayer().get();
        if (!annoucement.isPresent()) {
            sender.sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "ID not found");
            return;
        }
        ChatAnnoucement chatAnnoucement = annoucement.get();
        chatAnnoucement.startTaskNow();
        sender.sendMessage(ConfigProperties.PREFIX + ChatColor.GREEN + "Submitted as task #" + chatAnnoucement
                .getTaskID());
    }
}
