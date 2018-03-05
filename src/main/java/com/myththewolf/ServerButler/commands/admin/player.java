package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class player extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> send, String[] args, JavaPlugin javaPlugin) {
        send.ifPresent(player -> {
            MythPlayer target = DataCache.getPlayerByName(args[0]).get();
            Inventory targetInventory = Bukkit.createInventory(null, 9, "Options for " + target.getName());
            player.getBukkitPlayer().ifPresent(sender -> {
                targetInventory.setItem(0, target.getLoginStatus().equals(LoginStatus.PERMITTED) ? ItemUtils
                        .makeBanUserItem(target, player) : ItemUtils.makePardonUserItem(target, player));
                if (target.getChatStatus().equals(ChatStatus.MUTED)) {
                    targetInventory.setItem(1, ItemUtils.makeSoftmuteUserItem(target, player));
                    targetInventory.setItem(2, ItemUtils.makUnmuteUserItem(target, player));
                } else if (target.getChatStatus().equals(ChatStatus.SOFTMUTED)) {
                    targetInventory.setItem(1, ItemUtils.makeMuteUserItem(target, player));
                    targetInventory.setItem(2, ItemUtils.makUnmuteUserItem(target, player));
                } else {
                    targetInventory.setItem(1, ItemUtils.makeMuteUserItem(target, player));
                    targetInventory.setItem(2, ItemUtils.makeSoftmuteUserItem(target, player));
                }
                sender.openInventory(targetInventory);
            });
        });
    }

    @Override
    public int getNumRequiredArgs() {
        return 1;
    }
}
