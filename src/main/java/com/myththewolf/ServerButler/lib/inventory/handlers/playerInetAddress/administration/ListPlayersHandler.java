package com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.administration;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.impl.PlayerInetAddress;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONObject;

public class ListPlayersHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        if (!player.getBukkitPlayer().isPresent()) {
            return;
        }
        PlayerInetAddress inetAddress = DataCache.getPlayerInetAddressByIp(data.getString("ADDRESS")).orElseThrow(IllegalStateException::new);
        int index = 0;
        int sizeSlots = ItemUtils.findInventorySize(inetAddress.getMappedPlayers().size());
        Inventory inventory = Bukkit.createInventory(null, sizeSlots, "Players assigned to " + inetAddress.toString());
        for (MythPlayer pl : inetAddress.getMappedPlayers()) {

            JSONObject pakcet = new JSONObject();
            pakcet.put("packetType", PacketType.DISPATCH_COMMAND);
            pakcet.put("command", "player " + pl.getName());
            ItemStack stack = ItemUtils.getSkullofPlayer(player.getUUID());
            inventory.setItem(index, ItemUtils.applyJSON(pakcet, ItemUtils.nameItem(pl.getName(), stack)));
            index++;
        }
        player.getBukkitPlayer().orElseThrow(IllegalStateException::new).openInventory(inventory);
    }
}
