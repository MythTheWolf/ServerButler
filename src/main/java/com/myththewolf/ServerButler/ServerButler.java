package com.myththewolf.ServerButler;

import com.myththewolf.ServerButler.commands.admin.ChannelBuilder;
import com.myththewolf.ServerButler.commands.admin.InetAddr.management.ips;
import com.myththewolf.ServerButler.commands.admin.InetAddr.punishment.inetBan;
import com.myththewolf.ServerButler.commands.admin.InetAddr.punishment.inetTempBan;
import com.myththewolf.ServerButler.commands.admin.eval;
import com.myththewolf.ServerButler.commands.admin.jsonImport;
import com.myththewolf.ServerButler.commands.admin.player.managemnet.player;
import com.myththewolf.ServerButler.commands.admin.player.punishment.Ban;
import com.myththewolf.ServerButler.commands.admin.player.punishment.kick;
import com.myththewolf.ServerButler.commands.admin.player.punishment.mute;
import com.myththewolf.ServerButler.commands.admin.player.punishment.tempban;
import com.myththewolf.ServerButler.commands.futureTest;
import com.myththewolf.ServerButler.commands.player.channelmanager;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.CloseChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.OpenChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.SetWriteChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.ViewChannelOptionsHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.player.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.ViewIpOptions;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.ViewPlayerIPs;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.administration.DeleteIpHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment.BanIpHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment.PardonIPHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment.TempBanIpHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.mySQL.SQLConnector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is the main plugin class
 */
public class ServerButler extends JavaPlugin implements SQLAble {
    public static SQLConnector connector;
    public static FileConfiguration configuration;
    public static HashMap<String, CommandAdapter> commands = new HashMap<>();
    public static HashMap<PacketType, List<ItemPacketHandler>> itemPacketHandlers = new HashMap<>();
    public static ConversationFactory conversationBuilder;
    private CommandMap commandMap = null;
    public void onEnable() {
        conversationBuilder = new ConversationFactory(this);
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
        getLogger().info("Building command list");
        registerCommands();
        getLogger().info("Creating command proxies");

        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (org.bukkit.command.CommandMap) f.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
        }
        commands.forEach((trigger,runner) -> {
            commandMap.register(trigger, new Command(trigger) {
                @Override
                public boolean execute(CommandSender commandSender, String s, String[] args) {
                    checkAndRun(StringUtils.arrayToString(0,args),(Player) commandSender);
                    return true;
                }
            });
        });
    }

    @Override
    public void onDisable() {

    }

    public void registerCommands() {
        //*********** PLAIN USER COMMANDS ***********//
        registerCommand("chan", new channelmanager());
        //*********** ADMIN COMMANDS ***********//
        registerCommand("player", new player());
        registerCommand("ban", new Ban());
        registerCommand("kick", new kick());
        registerCommand("tempban", new tempban());
        registerCommand("mute", new mute());
        registerCommand("ips", new ips());
        registerCommand("ipban", new inetBan());
        registerCommand("iptempban", new inetTempBan());
        registerCommand("intest", new futureTest());
        registerCommand("jsonimport", new jsonImport());
        registerCommand("eval", new eval(this));
        registerCommand("createchannel", new ChannelBuilder());
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
        registerPacketHandler(PacketType.VIEW_PLAYER_IPS, new ViewPlayerIPs());
        registerPacketHandler(PacketType.VIEW_IP_OPTIONS, new ViewIpOptions());
        registerPacketHandler(PacketType.BAN_IP, new BanIpHandler());
        registerPacketHandler(PacketType.TEMPBAN_IP, new TempBanIpHandler());
        registerPacketHandler(PacketType.PARDON_IP, new PardonIPHandler());
        registerPacketHandler(PacketType.DELETE_IP, new DeleteIpHandler());

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
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Channels` ( `ID` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `shortcut` VARCHAR(255) NULL DEFAULT NULL , `prefix` VARCHAR(255) NULL DEFAULT NULL , `permission` VARCHAR(255) NULL DEFAULT NULL ,`format` VARCHAR(255) NOT NULL ,PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_IPAddresses` ( `ID` INT NULL AUTO_INCREMENT , `address` VARCHAR(255) NOT NULL , `playerUUIDs` TEXT NOT NULL, `loginStatus` VARCHAR(255) NOT NULL , `dateJoined` VARCHAR(255) NOT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
    }

    public void registerCommand(String cmd, CommandAdapter executor) {
        commands.put(cmd, executor);
    }

    public void registerPacketHandler(PacketType type, ItemPacketHandler handler) {
        List<ItemPacketHandler> handlerList = new ArrayList<>(itemPacketHandlers.get(type));
        handlerList.add(handler);
        itemPacketHandlers.put(type, handlerList);
    }
    public void checkAndRun(String raw,Player sender){
        sender.sendMessage(raw);
        String[] split = raw.split(" ");
        if (!ServerButler.commands.containsKey(split[0].substring(1))) {
            return;
        }
        String chop = split[0].substring(1);
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        ServerButler.commands.entrySet().stream()
                .filter(stringCommandAdapterEntry -> stringCommandAdapterEntry.getKey().equals(chop))
                .map(Map.Entry::getValue).forEach(commandAdapter -> {
            commandAdapter.setLastPlayer(DataCache.getOrMakePlayer(sender.getUniqueId().toString()));
            try {
                CommandPolicy CP = commandAdapter.getClass()
                        .getMethod("onCommand", Optional.class, String[].class, JavaPlugin.class)
                        .getAnnotation(CommandPolicy.class);
                boolean isAnnoPresent = commandAdapter.getClass().getMethod("onCommand", Optional.class, String[].class, JavaPlugin.class).isAnnotationPresent(CommandPolicy.class);
                if (!isAnnoPresent) {
                    debug("No annotations found for command executor class '" + commandAdapter.getClass()
                            .getName() + "', no checks will be made by the system!");
                }
                int commandUserReq = isAnnoPresent ? CP.userRequiredArgs() : -1;
                int commandConsoleReq = isAnnoPresent ? CP.consoleRequiredArgs() : -1;
                String usage = isAnnoPresent ? CP.commandUsage() : "<<NOT DEFINED>>";
                String permission = commandAdapter.getRequiredPermission();
                if (args.length < commandConsoleReq) {
                    getLogger().warning("Could not run command '" + split[0]
                            .substring(1) + "': Required args do not match supplied args. Usage (optional args are required in this context): " + usage);
                    return;
                } else if (args.length >= commandConsoleReq) {
                    commandAdapter.onCommand(Optional.empty(), args, (JavaPlugin) Bukkit.getPluginManager()
                            .getPlugin("ServerButler"));
                    return;
                }
                if (args.length < commandUserReq) {
                    sender
                            .sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "This command requires " + commandUserReq + " arguments, got " + args.length + ".");
                    sender.sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "Usage: " + usage);
                    return;
                }
                if (permission != null && !sender.hasPermission(permission)) {
                    sender
                            .sendMessage(ConfigProperties.PREFIX + "You do not have permission for this command.");
                    return;
                }
            } catch (NoSuchMethodException ex) {
                getLogger().severe("Could not find runner for command executor class: '" + commandAdapter.getClass()
                        .getName() + "'");
            }

            commandAdapter.onCommand(Optional.ofNullable(DataCache
                    .getOrMakePlayer(sender.getUniqueId().toString())), args, this);
        });
    }
}
