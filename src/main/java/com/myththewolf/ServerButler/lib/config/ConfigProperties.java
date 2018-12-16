package com.myththewolf.ServerButler.lib.config;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.StringUtils;
import org.bukkit.ChatColor;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

/**
 * This class represents config.yml keys and values
 */
public class ConfigProperties {
    /**
     * The prefix used when displaying messges from this plugin
     */
    public final static String PREFIX = ChatColor
            .translateAlternateColorCodes('&', ServerButler.configuration.getString("PLUGIN-PREFIX"));
    /**
     * The default ban reason if no reason is specified
     */
    public static String DEFAULT_BAN_REASON = ServerButler.configuration.getString("DEFAULT-BAN-REASON");
    /**
     * The default kick reason if no reason is specified
     */
    public static String DEFAULT_KICK_REASON = ServerButler.configuration.getString("DEFAULT-KICK-REASON");
    /**
     * The default mute reason if no reason is specified
     */
    public static String DEFAULT_MUTE_REASON = ServerButler.configuration.getString("DEFAULT-MUTE-REASON");
    /**
     * The default pardon reason if no reason is specified
     */
    public static String DEFAULT_PARDON_REASON = ServerButler.configuration.getString("DEFAULT-PARDON-REASON");
    /**
     * The pattern used to make the message to display when the target player when kicked Such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the reason specified for the kick
     */
    public static String FORMAT_KICK = ServerButler.configuration.getString("FORMAT-KICK-PLAYER");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the target player's name
     * {2} will be replaced with the reason specified for the kick
     */
    public static String FORMAT_KICK_CHAT = ServerButler.configuration.getString("FORMAT-KICK-CHAT");
    /**
     * The pattern used to make the message to display when the target player when banned Such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the reason specified for the ban
     */
    public static String FORMAT_BAN = ServerButler.configuration.getString("FORMAT-BAN-PLAYER");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the target player's name
     * {2} will be replaced with the reason specified for the ban
     */
    public static String FORMAT_BAN_CHAT = ServerButler.configuration.getString("FORMAT-BAN-CHAT");
    /**
     * The pattern used to make the message to display when the target player when banned Such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the reason specified for the ban
     * {2} will be replaced with the expire date
     */
    public static String FORMAT_TEMPBAN = ServerButler.configuration.getString("FORMAT-TEMPBAN-PLAYER");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the target player's name
     * {2} will be replaced with the reason specified for the ban
     * {3} will be replaced with the expire date
     */
    public static String FORMAT_TEMPBAN_CHAT = ServerButler.configuration.getString("FORMAT-TEMPBAN-CHAT");
    /**
     * The pattern used to make the message to display when the target player when muted Such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the reason specified for the mute
     */
    public static String FORMAT_MUTE = ServerButler.configuration.getString("FORMAT-MUTE-PLAYER");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the target player's name
     * {2} will be replaced with the reason specified for the mute
     */
    public static String FORMAT_MUTE_CHAT = ServerButler.configuration.getString("FORMAT-MUTE-CHAT");
    /**
     * The pattern used to make the message to display when the target player when unmuted Such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the reason specified for the unmute
     */
    public static String FORMAT_UNMUTE = ServerButler.configuration.getString("FORMAT-UNMUTE-PLAYER");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the target player's name
     * {2} will be replaced with the reason specified for the unmute
     */
    public static String FORMAT_UNMUTE_CHAT = ServerButler.configuration.getString("FORMAT-UNMUTE-CHAT");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the name of the admin who executed the pardon, or "CONSOLE" if the command was ran from the console.
     * {1} will be replaced with the target player's name
     * {2} will be replaced with the reason specified for the pardon
     */
    public static String FORMAT_PARDON_CHAT = ServerButler.configuration.getString("FORMAT-PARDON-CHAT");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the target IP address String
     * {1} will be replaced with the name of the admin who issued the ban, or "CONSOLE" if command was ran from the console
     * {2} will be replaced with the reason for the ban
     * {3} will be replaced with a list of affected usernames
     */
    public static String FORMAT_IPBAN_CHAT = ServerButler.configuration.getString("FORMAT-BAN-IP-CHAT");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the target IP address String
     * {1} will be replaced with the name of the admin who issued the ban, or "CONSOLE" if command was ran from the console
     * {2} will be replaced with the reason for the ban
     * {3} will be replaced with a list of affected usernames
     * {4} will be replaced with the expire date string
     */
    public static String FORMAT_IP_TEMPBAN_CHAT = ServerButler.configuration.getString("FORMAT-TEMPBAN-IP-CHAT");
    /**
     * The pattern used to make the message to display when the target player when banned Such that: <br />
     * {0} will be replaced with the target IP address String
     * {1} will be replaced with the name of the admin who executed the punishment, or "CONSOLE" if the command was ran from the console.
     * {2} will be replaced with the reason specified for the ban
     */
    public static String FORMAT_IP_BAN = ServerButler.configuration.getString("FORMAT-BAN-IP-PLAYER");
    /**
     * The pattern used to make the message sent to the admin channel such that: <br />
     * {0} will be replaced with the target IP address String
     * {1} will be replaced with the name of the admin who issued the ban, or "CONSOLE" if command was ran from the console
     * {2} will be replaced with the reason for the ban
     * {3} will be replaced with the expire date string
     */
    public static String FORMAT_IP_TEMPBAN = ServerButler.configuration.getString("FORMAT-TEMPBAN-IP-PLAYER");
    public static String FORMAT_IP_PARDON = ServerButler.configuration.getString("FORMAT-PARDON-IP-CHAT");
    /**
     * Boolean value of whether to display debug messages or not
     */
    public static boolean DEBUG = ServerButler.configuration.getBoolean("DEBUG");
    /**
     * The permission node required to use the /ban command
     */
    public static String BAN_PERMISSION = ServerButler.configuration.getString("BAN-PERMISSION");
    /**
     * The permission node required to use the /banip command
     */
    public static String BAN__IP_PERMISSION = ServerButler.configuration.getString("BAN-IP-PERMISSION");
    /**
     * The permission node required to use the /ips command
     */
    public static String VIEW_PLAYER_IPS_PERMISSION = ServerButler.configuration.getString("VIEW-PLAYER-IPS-PERMISSION");
    /**
     * The permission node required to view/write to the admin chat
     */
    public static String ADMIN_CHAT_PERMISSION = ServerButler.configuration.getString("VIEW-ADMIN-CHANNEL-PERMISSION");

    public static String MUTE_PERMISSION = ServerButler.configuration.getString("MUTE-PERMISSION");

    public static String UNMUTE_PERMISSION = ServerButler.configuration.getString("UNMUTE-PERMISSION");

    public static String IMPORT_JSON_DATA = ServerButler.configuration.getString("IMPORT-JSON-DATA");

    public static String PARDON_PERMISSION = ServerButler.configuration.getString("PARDON-PERMISSION");

    public static String PARDON_IP_PERMISSION = ServerButler.configuration.getString("PARDON-IP-PERMISSION");

    public static String TEMPBAN_IP_PERMISSION = ServerButler.configuration.getString("TEMPBAN-IP-PERMISSION");

    public static String KICK_PERMISSION = ServerButler.configuration.getString("KICK-PERMISSION");

    public static String TEMPBAN_PERMISSION = ServerButler.configuration.getString("TEMPBAN-PERMISSION");

    public static String DEFAULT_CHAT_PATTERN = "{isProbated}{prefix} {player_name} : {text}";

    public static String COLOR_CHAT_PERMISSION = ServerButler.configuration.getString("CHAT-COLOR-PERMISSION");

    public static boolean ENABLE_DISCORD_BOT = ServerButler.configuration.getBoolean("ENABLE-DISCORD-BOT");

    public static String DISCORD_BOT_TOKEN = ServerButler.configuration.getString("BOT-TOKEN");

    public static String SQL_HOST = ServerButler.configuration.getString("SQL-HOST");

    public static String SQL_PORT = ServerButler.configuration.getString("SQL-PORT");

    public static String SQL_USER = ServerButler.configuration.getString("SQL-USERNAME");

    public static String SQL_PASS = ServerButler.configuration.getString("SQL-PASSWORD");

    public static String SQL_DATABASE = ServerButler.configuration.getString("SQL-DATABASE");

    public static String VIEW_ANNOUNCEMENT_GUI = ServerButler.configuration.getString("VIEW-ANNOUNCEMENT-GUI");

    public static String PUNISHMENT_INFO_CHANNEL = ServerButler.configuration.getString("PUNISHMENT-INFO-CHANNEL");

    public static String PROBATE_PERMISSION = ServerButler.configuration.getString("PROBATE-PERMISSION");

    public static Optional<String> EULA = StringUtils
            .readFile(ServerButler.plugin.getDataFolder() + File.separator + "eula.txt", Charset
                    .defaultCharset());

    public static List<String> postEulaCommands = (List<String>) ServerButler.configuration
            .getList("POST-EULA-COMMANDS");

    public static boolean ENABLE_BUNGEE_SUPPORT = ServerButler.configuration.getBoolean("BUNGEE");

    public static String SERVER_NAME = ENABLE_BUNGEE_SUPPORT ? "[" + ServerButler.configuration.getString("THIS-SERVER-NAME") + "]" : "";
}
