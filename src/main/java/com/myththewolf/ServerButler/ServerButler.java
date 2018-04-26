package com.myththewolf.ServerButler;

import com.myththewolf.ServerButler.commands.admin.*;
import com.myththewolf.ServerButler.commands.admin.InetAddr.ips;
import com.myththewolf.ServerButler.commands.player.channelmanager;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.event.player.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.CloseChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.OpenChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.SetWriteChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.ViewChannelOptionsHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.player.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.ViewPlayerIPs;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.mySQL.SQLConnector;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class is the main plugin class
 */
public class ServerButler extends JavaPlugin implements SQLAble {
    public static SQLConnector connector;
    public static FileConfiguration configuration;
    public static HashMap<String, CommandAdapter> commands = new HashMap<>();
    public static HashMap<PacketType, List<ItemPacketHandler>> itemPacketHandlers = new HashMap<>();

    public void onEnable() {
        Arrays.stream(PacketType.values()).forEach(packetType -> itemPacketHandlers.put(packetType, new ArrayList<>()));
        getLogger().info("Received enable command");
        DataCache.makeMaps();
        checkConfiguration();
        registerPacketHandlers();
        configuration = getConfig();
        getLogger().info("Connecting to SQL server");
        connector = new SQLConnector("70.139.52.7", 3306, "Myth", "00163827", "MB_REWRITE");
        Bukkit.getPluginManager().registerEvents(new EPlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new EPlayerPreprocessEvent(), this);
        Bukkit.getPluginManager().registerEvents(new EConsoleCommand(), this);
        Bukkit.getPluginManager().registerEvents(new EInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new EPlayerChat(), this);
        getLogger().info("Constructing database");
        checkTables();
        getLogger().info("Building Channel list");
        DataCache.rebuildChannelList();
        //*********** PLAIN USER COMMANDS ***********//
        registerCommand("chan", new channelmanager());
        //*********** ADMIN COMMANDS ***********//
        registerCommand("player", new player());
        registerCommand("ban", new Ban());
        registerCommand("kick", new kick());
        registerCommand("tempban", new tempban());
        registerCommand("mute",new mute());
        registerCommand("ips",new ips());

    }

    @Override
    public void onDisable() {

    }

    public void registerPacketHandlers() {
        getLogger().info("Applying packet handlers");
        registerPacketHandler(PacketType.VIEW_CHANNEL_OPTIONS, new ViewChannelOptionsHandler());
        registerPacketHandler(PacketType.TOGGLE_CHANNEL_ON, new OpenChannelPacketHandler());
        registerPacketHandler(PacketType.TOGGLE_CHANNEL_OFF, new CloseChannelPacketHandler());
        registerPacketHandler(PacketType.SET_WRITE_CHANNEL, new SetWriteChannelPacketHandler());
        registerPacketHandler(PacketType.BAN_PLAYER, new BanPlayerHandler());
        registerPacketHandler(PacketType.MUTE_PLAYER, new MutePlayerHandler());
        registerPacketHandler(PacketType.SOFTMUTE_PLAYER, new SoftmutePlayerHandler());
        registerPacketHandler(PacketType.PARDON_PLAYER, new PardonPlayerHandler());
        registerPacketHandler(PacketType.UNMUTE_PLAYER, new UnmutePlayerHandler());
        registerPacketHandler(PacketType.TEMPBAN_PLAYER, new TempBanPlayerHandler());
        registerPacketHandler(PacketType.KICK_PLAYER, new KickPlayerHandler());
        registerPacketHandler(PacketType.VIEW_PLAYER_IPS,new ViewPlayerIPs());
    }

    public void checkConfiguration() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            getLogger().info("Config.yml not found, creating!");
            saveDefaultConfig();
        } else {
            getLogger().info("Config.yml found, loading!");
        }
    }

    public void checkTables() {
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Players` ( `ID` INT NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(255) NOT NULL , `loginStatus` VARCHAR(255) NOT NULL DEFAULT 'PERMITTED' , `chatStatus` VARCHAR(255) NOT NULL DEFAULT 'PERMITTED', `name` VARCHAR(255) NULL DEFAULT NULL , `joinDate` VARCHAR(255) NULL DEFAULT NULL , `channels` VARCHAR(255) NOT NULL DEFAULT '',`writeChannel` VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Actions` ( `ID` INT NOT NULL AUTO_INCREMENT , `type` VARCHAR(255) NULL DEFAULT NULL , `reason` VARCHAR(255) NULL DEFAULT NULL , `expireDate` VARCHAR(255) NULL DEFAULT NULL, `target` VARCHAR(255) NULL DEFAULT NULL , `moderator` VARCHAR(255) NULL DEFAULT NULL , `targetType` VARCHAR(255) NULL DEFAULT NULL , `dateApplied` VARCHAR(255) NULL DEFAULT NULL,PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Channels` ( `ID` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `shortcut` VARCHAR(255) NULL DEFAULT NULL , `prefix` VARCHAR(255) NULL DEFAULT NULL , `permission` VARCHAR(255) NULL DEFAULT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_IPAddresses` ( `ID` INT NULL AUTO_INCREMENT , `address` VARCHAR(255) NOT NULL , `playerUUIDs` TEXT NOT NULL, `loginStatus` VARCHAR(255) NOT NULL , `dateJoined` VARCHAR(255) NOT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB;",0);
    }

    public void registerCommand(String cmd, CommandAdapter executor) {
        commands.put(cmd, executor);
    }

    public void registerPacketHandler(PacketType type, ItemPacketHandler handler) {
        List<ItemPacketHandler> handlerList = new ArrayList<>(itemPacketHandlers.get(type));
        handlerList.add(handler);
        itemPacketHandlers.put(type, handlerList);
    }
}
