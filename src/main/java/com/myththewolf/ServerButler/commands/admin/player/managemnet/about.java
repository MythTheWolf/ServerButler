package com.myththewolf.ServerButler.commands.admin.player.managemnet;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.ChatStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.LoginStatus;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class about extends CommandAdapter {
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (!sender.isPresent()) {
            return;
        }
        Optional<MythPlayer> targetOp = DataCache.getPlayerByName(args[0]);
        if (!targetOp.isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "Player not found");
            return;
        }
        MythPlayer target = targetOp.get();
        StringBuilder builder = new StringBuilder();
        List<String> stringList = new ArrayList<>();
        stringList.add("&6=================&8[&bPlayer Info&8]&6=================");
        stringList.add("&6 Known Since: &b" + TimeUtils.dateToString(target.getJoinDate()));
        stringList.add("&6Player Name: &b" + target.getName());
        stringList.add("&6Player UUID: &b" + target.getUUID());
        stringList.add("&6Discord ID: " + (target.getDiscordID().isPresent() ? "&b" + target.getDiscordID()
                .get() : "&cNot Linked"));
        stringList.add("&6Login Status: " + (target
                .getLoginStatus() == LoginStatus.PERMITTED ? "&aPERMITTED" : "&c" + target.getLoginStatus()));
        stringList
                .add("&6Chat Status: " + (target.getChatStatus() == ChatStatus.PERMITTED ? "&aPERMITTED" : "&c" + target
                        .getChatStatus().toString()));
        stringList.add("&6Ip List: &b" + Arrays.toString(target.getPlayerAddresses().toArray()));

        stringList.add("&6Opened Channels: &b" + Arrays
                .toString(target.getChannelList().stream().map(ChatChannel::getName).toArray()));
        Player player = sender.get().getBukkitPlayer().orElseThrow(IllegalStateException::new).getPlayer();
        stringList.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).forEach(player::sendMessage);
        Bukkit.createBossBar("com.myththewolf.ServerButler.IO.BukkitByteBuff.class", BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
    }
}
