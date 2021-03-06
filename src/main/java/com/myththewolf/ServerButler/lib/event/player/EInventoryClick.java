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

/**
 * This class captures all inventory click events
 */
public class EInventoryClick implements Listener, Loggable {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (event.getCurrentItem() == null) {
            return;
        }

        if (event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getItemMeta()
                .getLore() == null || event.getCurrentItem().getItemMeta().getLore().size() == 0) {
            return;
        }
        if (!StringUtils.getEmeddedString(event.getCurrentItem()).isPresent()) {
            return;
        }
        String decoded = StringUtils.getEmeddedString(event.getCurrentItem()).get();
        if (!StringUtils.parseJSONObject(decoded).isPresent()) {
            return;
        }
        JSONObject parsed = StringUtils.parseJSONObject(decoded).get();
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
