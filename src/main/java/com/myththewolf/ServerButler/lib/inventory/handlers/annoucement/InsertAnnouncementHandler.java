package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.json.JSONObject;

public class InsertAnnouncementHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        ChatAnnoucement annoucement = new ChatAnnoucement(data.getString("content"), TimeUtils.TIME_INPUT_FORMAT()
                .parsePeriod(data.getString("interval")), data.isNull("permission") ? null : data
                .getString("permission"));
        annoucement.update();
        StringUtils.deserializeArray(data.getString("selectedChannels")).stream().map(ChatChannel::new)
                .forEach(annoucement::addChannel);
        annoucement.update();
        player.getBukkitPlayer()
                .ifPresent(player1 -> player1.sendMessage(ConfigProperties.PREFIX + "Created new announcement!"));
        DataCache.rebuildTaskList();
    }
}
