package com.myththewolf.ServerButler.lib.event.player;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.json.JSONObject;

public class EInventoryClick implements Listener, Loggable {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!StringUtils.getEmeddedString(event.getCurrentItem()).isPresent()) {
            return;
        }
        String decoded = StringUtils.getEmeddedString(event.getCurrentItem()).get();
        if (!StringUtils.parseJSON(decoded).isPresent()) {
            return;
        }
        JSONObject parsed = StringUtils.parseJSON(decoded).get();
        if (parsed.isNull("packetType") || !ServerButler.itemPacketHandlers
                .containsKey(PacketType.valueOf(parsed.getString("packetType")))) {
            return;
        }
        event.setCancelled(true);
        getLogger().info("Got packet from ItemStack:" + parsed.toString());
        Player player = (Player) event.getWhoClicked();
        player.closeInventory();
        ServerButler.itemPacketHandlers.get(PacketType.valueOf(parsed.getString("packetType"))).forEach(handler -> {
            handler.onPacketReceived(DataCache.getOrMakePlayer(player.getUniqueId().toString()), parsed);
        });
    }
}
