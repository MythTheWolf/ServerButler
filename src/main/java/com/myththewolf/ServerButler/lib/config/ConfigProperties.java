package com.myththewolf.ServerButler.lib.config;

import com.myththewolf.ServerButler.ServerButler;

public class ConfigProperties {
    public static String DEFAULT_BAN_REASON = ServerButler.configuration.getString("DEFAULT-BAN-REASON");
    public static String DEFAULT_KICK_REASON = ServerButler.configuration.getString("DEFAULT-KICK-REASON");
    public static String FORMAT_KICK = ServerButler.configuration.getString("FORMAT-KICK-PLAYER");
    public static String FORMAT_BAN = ServerButler.configuration.getString("FORMAT-BAN-PLAYER");
    public static String FORMAT_BAN_CHAT = ServerButler.configuration.getString("FORMAT-BAN-CHAT");
    public static String PREFIX = ServerButler.configuration.getString("PLUGIN-PREFIX");
    public static boolean DEBUG = ServerButler.configuration.getBoolean("DEBUG");
    public static String BAN_PERMISSION = ServerButler.configuration.getString("BAN-PERMISSION");
    public static String ADMIN_CHAT_PERMISSION = ServerButler.configuration.getString("ADMIN-CHAT-PERMISSION");
}
