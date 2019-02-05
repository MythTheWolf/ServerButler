package com.myththewolf.ServerButler.lib.bungee.packets.Handlers;

import com.myththewolf.ServerButler.lib.bungee.packets.BungeePacketHandler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import org.json.JSONObject;

public class CacheRebuildHandler implements BungeePacketHandler {
    @Override
    public void onPacket(JSONObject data) {
        switch (data.getString("targetType")) {
            case "player":
                DataCache.rebuildPlayer(data.getString("target"));
                break;
            case "InetAddress":
                DataCache.rebuildPlayerInetAddress(DataCache.getOrMakeInetAddress(data.getString("target")).orElseThrow(IllegalStateException::new));
                break;
            case "channel":
                DataCache.rebuildChannel(data.getString("target"));
                break;
            case "channelList":
                DataCache.rebuildChannelList();
                break;
            case "taskList":
                DataCache.rebuildTaskList();
                break;
            default:
                break;
        }
    }
}
