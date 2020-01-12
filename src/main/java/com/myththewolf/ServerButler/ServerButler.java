package com.myththewolf.ServerButler;

import com.myththewolf.ServerButler.commands.admin.*;
import com.myththewolf.ServerButler.commands.admin.InetAddr.management.ips;
import com.myththewolf.ServerButler.commands.admin.InetAddr.punishment.inetBan;
import com.myththewolf.ServerButler.commands.admin.InetAddr.punishment.inetTempBan;
import com.myththewolf.ServerButler.commands.admin.annoucement.task;
import com.myththewolf.ServerButler.commands.admin.annoucement.tasks;
import com.myththewolf.ServerButler.commands.admin.chat.ChatClear;
import com.myththewolf.ServerButler.commands.admin.player.managemnet.about;
import com.myththewolf.ServerButler.commands.admin.player.managemnet.player;
import com.myththewolf.ServerButler.commands.admin.player.punishment.*;
import com.myththewolf.ServerButler.commands.player.channelmanager;
import com.myththewolf.ServerButler.commands.player.discord.link;
import com.myththewolf.ServerButler.commands.player.eula;
import com.myththewolf.ServerButler.commands.player.token;
import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.MythUtils.MythTPSWatcher;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import com.myththewolf.ServerButler.lib.bungee.packets.BungeePacketHandler;
import com.myththewolf.ServerButler.lib.bungee.packets.BungeePacketType;
import com.myththewolf.ServerButler.lib.bungee.packets.Handlers.BroadcastHandler;
import com.myththewolf.ServerButler.lib.bungee.packets.Handlers.CacheRebuildHandler;
import com.myththewolf.ServerButler.lib.bungee.packets.MythSocketServer;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.impl.DiscordCommandAdapter;
import com.myththewolf.ServerButler.lib.command.impl.SpigotTabCompleter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.event.player.Discord.DiscordMessageEvent;
import com.myththewolf.ServerButler.lib.event.player.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.annoucement.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.CloseChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.OpenChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.SetWriteChannelPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.chat.ViewChannelOptionsHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.player.ViewPlayerExtraInfoHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.player.punishment.*;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.ViewIpOptions;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.ViewPlayerIPs;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.administration.DeleteIpHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment.BanIpHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment.PardonIPHandler;
import com.myththewolf.ServerButler.lib.inventory.handlers.playerInetAddress.punishment.TempBanIpHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.ConsoleAppender;
import com.myththewolf.ServerButler.lib.logging.ConsoleMessageQueueWorker;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.mySQL.SQLConnector;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import com.myththewolf.ServerButler.lib.webserver.ServerButlerJettyServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ChannelCategory;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.util.logging.ExceptionLogger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * This class is the main plugin class
 */
public class ServerButler extends JavaPlugin implements SQLAble, Loggable {
    public static SQLConnector connector;
    public static FileConfiguration configuration;
    public static HashMap<String, CommandAdapter> commands = new HashMap<>();
    public static HashMap<String, DiscordCommandAdapter> discordCommands = new HashMap<>();
    public static HashMap<PacketType, List<ItemPacketHandler>> itemPacketHandlers = new HashMap<>();
    public static HashMap<BungeePacketType, List<BungeePacketHandler>> bungeePacketHandlers = new HashMap<>();
    public static ConversationFactory conversationBuilder;
    public static DiscordApi API;
    public static Plugin plugin;
    public static DateTime startTime = new DateTime();
    private static ChannelCategory channelCategory;
    private TabCompleter mythTabCompleter;
    private List<Message> loadingMessages = new ArrayList<>();
    private Queue<String> consoleMessageQueue = new LinkedList<>();
    private ConsoleMessageQueueWorker consoleMessageQueueWorker = null;
    private ServerButlerJettyServer webServer;

    public static ServerButler getInstance() {
        return ((ServerButler) plugin);
    }

    public Queue<String> getConsoleMessageQueue() {
        return consoleMessageQueue;
    }

    public void onEnable() {
        plugin = this;
        conversationBuilder = new ConversationFactory(this);
        conversationBuilder.addConversationAbandonedListener(new PlayerConversationAbandonedEvent());
        mythTabCompleter = new SpigotTabCompleter();
        Arrays.stream(PacketType.values()).forEach(packetType -> itemPacketHandlers.put(packetType, new ArrayList<>()));
        Arrays.stream(BungeePacketType.values()).forEach(bungeePacketType -> bungeePacketHandlers.put(bungeePacketType, new ArrayList<>()));
        getLogger().info("Received enable command");
        DataCache.makeMaps();
        checkConfiguration();
        registerPacketHandlers();
        configuration = getConfig();
        getLogger().info("Connecting to SQL server");
        connector = new SQLConnector(ConfigProperties.SQL_HOST, Integer
                .parseInt(ConfigProperties.SQL_PORT), ConfigProperties.SQL_USER, ConfigProperties.SQL_PASS, ConfigProperties.SQL_DATABASE);
        Bukkit.getPluginManager().registerEvents(new EPlayerJoin(), this);
        Bukkit.getPluginManager().registerEvents(new EPlayerPreprocessEvent(), this);
        Bukkit.getPluginManager().registerEvents(new EConsoleCommand(), this);
        Bukkit.getPluginManager().registerEvents(new EInventoryClick(), this);
        Bukkit.getPluginManager().registerEvents(new EPlayerChat(), this);
        Bukkit.getPluginManager().registerEvents(new EPlayerLeave(), this);
        Bukkit.getPluginManager().registerEvents(new EPlayerDeath(), this);
        getLogger().info("Constructing database");
        checkTables();

        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            getLogger().info("Starting token bot from token dir..");
            API = new DiscordApiBuilder().setToken(ConfigProperties.DISCORD_BOT_TOKEN).login().join();
        }
        getLogger().info("Building Channel list");
        DataCache.rebuildChannelList();
        getLogger().info("Caching all announcement tasks");
        DataCache.rebuildTaskList();
        getLogger().info("Starting all announcement tasks");
        DataCache.annoucementHashMap.values().stream().filter(ChatAnnoucement::isEnabled).forEach(ChatAnnoucement::startTask);
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new MythTPSWatcher(), 100L, 1L);
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> DataCache.getAllChannels().forEach(chatChannel -> chatChannel.getDiscordChannel().asServerTextChannel().orElseThrow(IllegalStateException::new).updateTopic(Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getServer().getMaxPlayers() + " players | " + Math.floor(MythTPSWatcher.getTPS()) + " TPS | Server online for " + TimeUtils.durationToString(new Duration(startTime, DateTime.now()))).exceptionally(ExceptionLogger.get())), 20, 1200);
        }
        getLogger().info("Building command list");
        registerCommands();

        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            File botFolder = new File(getDataFolder().getAbsolutePath() + File.separator + "discordBot");
            if (!botFolder.exists()) {
                boolean ok = botFolder.mkdir();
                if (!ok)
                    throw new IllegalStateException("Must have perms to write!");
            }
            File dataFile = new File(botFolder.getAbsolutePath() + File.separator + "bot-config.json");
            if (!dataFile.exists()) {
                JSONObject tmp = new JSONObject();
                tmp.put("category-id", "NOT_A_ID");
                StringUtils.writeFile(dataFile.getAbsolutePath(), tmp.toString(4));
            }
            JSONObject conf = new JSONObject(StringUtils.readFile(dataFile.getAbsolutePath()));
            Server thisServer = null;
            try {
                thisServer = API.getServerById(ConfigProperties.DISCORD_GUILD_ID).orElseThrow(Exception::new);
            } catch (Exception e) {
                getLogger().warning("Failed to enable discord bot - No such guild for that ID!");
                Bukkit.getPluginManager().disablePlugin(this);
                e.getStackTrace();
            }


            if (!thisServer.getChannelCategoryById(conf.getString("category-id")).isPresent()) {
                getLogger().info("Creating MC category");
                channelCategory = thisServer.createChannelCategoryBuilder().setName(ConfigProperties.DISCORD_CATEGORY_NAME).create().join();
                JSONObject tmp = new JSONObject();
                tmp.put("category-id", channelCategory.getIdAsString());
                StringUtils.writeFile(dataFile.getAbsolutePath(), tmp.toString(4));
                conf = new JSONObject(StringUtils.readFile(dataFile.getAbsolutePath()));
                getLogger().info("Setting up one-time permissions");
                thisServer.getRoles().forEach(role -> {
                    getLogger().info(role.getName());
                    Permissions p = new PermissionsBuilder().setAllDenied().build();
                    channelCategory.createUpdater().addPermissionOverwrite(role, p).update()
                            .exceptionally(ExceptionLogger.get());
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            channelCategory = thisServer.getChannelCategoryById(conf.getString("category-id")).get();
            DataCache.getAllChannels().forEach(chatChannel -> {
                if (channelCategory.getChannels().stream()
                        .noneMatch(c -> c.getName().equals(ConfigProperties.SERVER_NAME + chatChannel.getName().toLowerCase()))) {
                    TextChannel tc = channelCategory.getServer().createTextChannelBuilder().setCategory(channelCategory)
                            .setName(ConfigProperties.SERVER_NAME + chatChannel.getName()).create().join();
                    chatChannel.setChannel(tc);

                    if (!chatChannel.getPermission().isPresent()) {
                        getLogger()
                                .info("Reversing Permissions to allow all users for channel " + chatChannel.getName());
                        chatChannel.getDiscordChannel()
                                .sendMessage(":timer: I'm still setting permissions! Chat will not be fully accessible!");
                        PermissionsBuilder pb = new PermissionsBuilder();
                        pb.setAllowed(PermissionType.READ_MESSAGE_HISTORY, PermissionType.READ_MESSAGES, PermissionType.ATTACH_FILE);
                        API.getServerById(ConfigProperties.DISCORD_GUILD_ID).orElseThrow(IllegalStateException::new).getRoles().forEach(role -> {

                            chatChannel.getDiscordChannel().asServerTextChannel().orElseThrow(IllegalStateException::new).createUpdater()
                                    .addPermissionOverwrite(role, pb.build()).update()
                                    .exceptionally(ExceptionLogger.get());
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        chatChannel.getDiscordChannel().sendMessage(":white_check_mark: Permissions done!");
                    }
                }
                channelCategory.getChannels().stream()
                        .filter(serverChannel -> serverChannel.getName().equals(chatChannel.getName().toLowerCase()))
                        .findFirst().flatMap(Channel::asServerTextChannel).ifPresent(c -> {
                    chatChannel.setChannel(c);
                    chatChannel.update();
                    chatChannel.getDiscordChannel().sendMessage(":timer: Server starting").exceptionally(ExceptionLogger.get()).thenAccept(message -> {
                        loadingMessages.add(message);
                    });
                });
            });
        }

        if (ConfigProperties.ENABLE_DISCORD_BOT) {

            getLogger().info("Starting the Discord Command Engine");
            API.addMessageCreateListener(new DiscordMessageEvent());
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
                getLogger().info("Enabling the logger bridge");
                // check log4j capabilities
                boolean serverIsLog4jCapable = false;
                boolean serverIsLog4j21Capable = false;
                try {
                    serverIsLog4jCapable = Class.forName("org.apache.logging.log4j.core.Logger") != null;
                } catch (ClassNotFoundException e) {
                    getLogger().severe("Log4j classes are NOT available, console channel will not be attached");
                }
                try {
                    serverIsLog4j21Capable = Class.forName("org.apache.logging.log4j.core.Filter") != null;
                } catch (ClassNotFoundException e) {
                    getLogger().severe("Log4j 2.1 classes are NOT available, JDA messages will NOT be formatted properly");
                }

                new ConsoleAppender();

                // start console message queue worker thread
                if (consoleMessageQueueWorker != null) {
                    if (consoleMessageQueueWorker.getState() != Thread.State.NEW) {
                        consoleMessageQueueWorker.interrupt();
                        consoleMessageQueueWorker = new ConsoleMessageQueueWorker();
                    }
                } else {
                    consoleMessageQueueWorker = new ConsoleMessageQueueWorker();
                }
                consoleMessageQueueWorker.start();
                loadingMessages.forEach(message -> {
                    message.edit("**:arrow_up: Server Online**").exceptionally(ExceptionLogger.get());
                });
            });
        }
        getLogger().info("Creating command proxies");
        try {

            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commands.forEach((trigger, runner) -> {
                try {
                    Constructor<PluginCommand> c = PluginCommand.class
                            .getDeclaredConstructor(String.class, Plugin.class);
                    c.setAccessible(true);
                    PluginCommand pluginCommand = c.newInstance(trigger, plugin);
                    pluginCommand.setTabCompleter(mythTabCompleter);
                    pluginCommand.setExecutor((commandSender, command1, s, strings) -> {
                        String[] arr = new String[strings.length + 1];
                        arr[0] = "/" + command1.getLabel();
                        int spot = 1;
                        for (String S : strings) {
                            arr[spot] = S;
                            spot++;
                        }
                        checkAndRun(StringUtils.arrayToString(0, arr), commandSender);
                        return true;
                    });
                    commandMap.register(trigger, pluginCommand);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            if (ConfigProperties.ENABLE_BUNGEE_SUPPORT && ConfigProperties.ENABLE_CACHE) {
                getLogger().info("Enabling bungee socket servers..");
                Thread thread = new Thread(new MythSocketServer(ConfigProperties.SOCKET_PORT));
                thread.start();
                registerBungeePacketHandler(BungeePacketType.REBUILD_CACHE, new CacheRebuildHandler());
                registerBungeePacketHandler(BungeePacketType.BROADCAST_MESSAGE, new BroadcastHandler());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread webThread = new Thread(() -> {
            getLogger().info("Starting web server");
            webServer = new ServerButlerJettyServer(25577);
            webServer.start();
        });
        webThread.start();
    }

    @Override
    public void onDisable() {
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            DataCache.getAllChannels().forEach(chatChannel -> {
                chatChannel.getDiscordChannel().sendMessage("**:arrow_down: Server Offline**")
                        .join();
                chatChannel.getDiscordChannel().asServerTextChannel().orElseThrow(IllegalStateException::new).updateTopic("[Server offline]").join();
            });
            try {
                getSQLConnection().close();
                webServer.stop();
                consoleMessageQueueWorker.interrupt();
                API.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void registerCommands() {
        //*********** PLAIN USER COMMANDS ***********//
        registerCommand("chan", new channelmanager());
        registerCommand("token", new token());
        //*********** ADMIN COMMANDS ***********//
        registerCommand("player", new player());
        registerCommand("ban", new Ban());
        registerCommand("kick", new kick());
        registerCommand("tempban", new tempban());
        registerCommand("mute", new mute());
        registerCommand("ips", new ips());
        registerCommand("ipban", new inetBan());
        registerCommand("iptempban", new inetTempBan());
        registerCommand("jsonimport", new jsonImport());
        registerCommand("eval", new eval(this));
        registerCommand("createchannel", new ChannelBuilder());
        registerCommand("unmute", new unmute());
        registerCommand("about", new about());
        registerCommand("pardon", new pardon());
        registerCommand("tasks", new tasks());
        registerCommand("task", new task());
        registerCommand("clearchat", new ChatClear());
        registerCommand("eula", new eula());
        registerCommand("probate", new probate());
        registerCommand("test", new cb());
        registerCommand("sb", new version());
        registerCommand("softmute", new softmute());
        //*************** DISCORD COMMANDS ****************//
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            registerDiscordCommand(";link", new link());
            registerDiscordCommand(";eval", new com.myththewolf.ServerButler.commands.admin.discord.eval(this));
        }
        DataCache.rebuildChannelList();
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
        registerPacketHandler(PacketType.VIEW_PLAYER_EXTA_INFO, new ViewPlayerExtraInfoHandler());
        registerPacketHandler(PacketType.VIEW_ANNOUNCEMENT_OPTIONS, new ViewAnnouncementOptionsHandler());
        registerPacketHandler(PacketType.DELETE_ANNOUNCEMENT, new DeleteHandler());
        registerPacketHandler(PacketType.UPDATE_CONTENT, new UpdateContentHandler());
        registerPacketHandler(PacketType.UPDATE_INTERVAL, new UpdateIntervalHandler());
        registerPacketHandler(PacketType.CHANNEL_SELECTION_CONTINUE, new CommitChannelHandler());
        registerPacketHandler(PacketType.START_ANNOUNCEMENT, new StartTaskHandler());
        registerPacketHandler(PacketType.STOP_ANNOUNCEMENT, new StopTaskHandler());
        registerPacketHandler(PacketType.ADD_CHANNEL, new AddChannelHandler());
        registerPacketHandler(PacketType.REMOVE_CHANNEL, new RemoveChannelHandler());
        registerPacketHandler(PacketType.CREATE_ANNOUNCEMENT, new CreateAnnouncementHandler());
        registerPacketHandler(PacketType.INSERT_ANNOUNCEMENT, new InsertAnnouncementHandler());
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
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Players` ( `ID` INT NOT NULL AUTO_INCREMENT , `UUID` VARCHAR(255) NOT NULL, `loginStatus` VARCHAR(255) NOT NULL DEFAULT 'PERMITTED' , `probate` VARCHAR(255) NOT NULL DEFAULT 'false',`chatStatus` VARCHAR(255) NOT NULL DEFAULT 'PERMITTED', `name` VARCHAR(255) NULL DEFAULT NULL , `displayName` VARCHAR(255) NULL DEFAULT NULL,`joinDate` VARCHAR(255) NULL DEFAULT NULL , `channels` VARCHAR(255) NOT NULL DEFAULT '',`writeChannel` VARCHAR(255) NULL DEFAULT NULL, `discordID` VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Actions` ( `ID` INT NOT NULL AUTO_INCREMENT , `type` VARCHAR(255) NULL DEFAULT NULL , `reason` VARCHAR(255) NULL DEFAULT NULL , `expireDate` VARCHAR(255) NULL DEFAULT NULL, `target` VARCHAR(255) NULL DEFAULT NULL , `moderator` VARCHAR(255) NULL DEFAULT NULL , `targetType` VARCHAR(255) NULL DEFAULT NULL , `dateApplied` VARCHAR(255) NULL DEFAULT NULL,PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Channels` ( `ID` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(255) NOT NULL , `shortcut` VARCHAR(255) NULL DEFAULT NULL , `prefix` VARCHAR(255) NULL DEFAULT NULL , `permission` VARCHAR(255) NULL DEFAULT NULL ,`format` VARCHAR(255) NOT NULL , `discord_id` VARCHAR(255) NULL DEFAULT NULL, PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Discord` ( `ID` INT NOT NULL AUTO_INCREMENT , `token` VARCHAR(255) NOT NULL , `UUID` VARCHAR(255) NOT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_IPAddresses` ( `ID` INT NULL AUTO_INCREMENT , `address` VARCHAR(255) NOT NULL , `playerUUIDs` TEXT NOT NULL, `loginStatus` VARCHAR(255) NOT NULL , `dateJoined` VARCHAR(255) NOT NULL , PRIMARY KEY (`ID`)) ENGINE = InnoDB;", 0);
        prepareAndExecuteUpdateExceptionally("CREATE TABLE IF NOT EXISTS `SB_Announcements` ( `ID` INT NOT NULL AUTO_INCREMENT , `content` TEXT NOT NULL , `channels` VARCHAR(255) NOT NULL , `permission` VARCHAR(255) NULL , `time` VARCHAR(255) NOT NULL , PRIMARY KEY (`ID`), `enabled` VARCHAR(255) NOT NULL) ENGINE = InnoDB;", 0);
    }

    public void registerCommand(String cmd, CommandAdapter executor) {
        commands.put(cmd, executor);
    }

    public void registerDiscordCommand(String cmd, DiscordCommandAdapter discordCommandAdapter) {
        discordCommands.put(cmd, discordCommandAdapter);
    }

    public void registerPacketHandler(PacketType type, ItemPacketHandler handler) {
        List<ItemPacketHandler> handlerList = new ArrayList<>(itemPacketHandlers.get(type));
        handlerList.add(handler);
        itemPacketHandlers.put(type, handlerList);
    }

    public void registerBungeePacketHandler(BungeePacketType type, BungeePacketHandler handler) {
        List<BungeePacketHandler> handlerList = new ArrayList<>(bungeePacketHandlers.get(type));
        handlerList.add(handler);
        bungeePacketHandlers.put(type, handlerList);
    }

    public void checkAndRun(String raw, CommandSender sender) {
        String[] split = raw.split(" ");
        if (!ServerButler.commands.containsKey(split[0].substring(1))) {
            return;
        }
        String chop = split[0].substring(1);
        String[] args;
        if (split.length < 2) {
            args = new String[0];
        } else {
            args = Arrays.copyOfRange(split, 1, split.length);
        }

        ServerButler.commands.entrySet().stream()
                .filter(stringCommandAdapterEntry -> stringCommandAdapterEntry.getKey().equals(chop))
                .map(Map.Entry::getValue).forEach(commandAdapter -> {

            try {
                CommandPolicy CP = commandAdapter.getClass()
                        .getMethod("onCommand", Optional.class, String[].class, JavaPlugin.class)
                        .getAnnotation(CommandPolicy.class);
                boolean isAnnoPresent = commandAdapter.getClass()
                        .getMethod("onCommand", Optional.class, String[].class, JavaPlugin.class)
                        .isAnnotationPresent(CommandPolicy.class);
                if (!isAnnoPresent) {
                    debug("No annotations found for command executor class '" + commandAdapter.getClass()
                            .getName() + "', no checks will be made by the system!");
                }
                int commandUserReq = isAnnoPresent ? CP.userRequiredArgs() : 0;
                int commandConsoleReq = isAnnoPresent ? CP.consoleRequiredArgs() : 0;
                String usage = isAnnoPresent ? CP.commandUsage() : "<<NOT DEFINED>>";
                String permission = commandAdapter.getRequiredPermission();
                if (isAnnoPresent && commandConsoleReq == -1) {
                    sender.sendMessage("This command cannot be run from the console.");
                    return;
                }
                if ((sender instanceof Player && args.length < commandUserReq) || (sender instanceof ConsoleCommandSender && args.length < commandConsoleReq)) {
                    sender
                            .sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "This command requires " + ((sender instanceof Player) ? commandUserReq : commandConsoleReq) + " arguments, got " + args.length + ".");
                    sender.sendMessage(ConfigProperties.PREFIX + ChatColor.RED + "Usage: " + usage);
                    return;
                }
                if (permission != null && sender instanceof Player && !sender.hasPermission(permission)) {
                    sender
                            .sendMessage(ConfigProperties.PREFIX + "You do not have permission for this command.");
                    return;
                }
                MythPlayer cast = sender instanceof Player ? DataCache.getPlayer(((Player) sender).getUniqueId().toString()).orElseThrow(IllegalStateException::new) : null;
                commandAdapter.setLastPlayer(cast);
                commandAdapter.onCommand(Optional.ofNullable(cast), args, this);
            } catch (NoSuchMethodException ex) {
                getLogger().severe("Could not find runner for command executor class: '" + commandAdapter.getClass()
                        .getName() + "'");
            }
        });
    }
}
