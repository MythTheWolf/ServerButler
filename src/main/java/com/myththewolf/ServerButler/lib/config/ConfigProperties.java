package com.myththewolf.ServerButler.lib.config;

import com.myththewolf.ServerButler.ServerButler;
import org.bukkit.ChatColor;

/**
 * This class represents config.yml keys and values
 */
public class ConfigProperties {
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
     * The prefix used when displaying messges from this plugin
     */
    public static String PREFIX = ChatColor
            .translateAlternateColorCodes('&', ServerButler.configuration.getString("PLUGIN-PREFIX"));
    /**
     * Boolean value of whether to display debug messages or not
     */
    public static boolean DEBUG = ServerButler.configuration.getBoolean("DEBUG");
    /**
     * The permission node required to use the /ban command
     */
    public static String BAN_PERMISSION = ServerButler.configuration.getString("BAN-PERMISSION");
    /**
     * The permission node required to view/write to the admin chat
     */
    public static String ADMIN_CHAT_PERMISSION = ServerButler.configuration.getString("ADMIN-CHAT-PERMISSION");

    private String PERMISSION_BAN = ServerButler.configuration.getString("BAN-PERMISSION");
}
