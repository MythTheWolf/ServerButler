package com.myththewolf.ServerButler.lib.logging;


import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.MythUtils.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.ChatColor;
import org.joda.time.DateTime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Plugin(name = "DiscordSRV-ConsoleChannel", category = "Core", elementType = "appender", printObject = true)
public class ConsoleAppender extends AbstractAppender {

    private static final PatternLayout PATTERN_LAYOUT;

    static {
        Method createLayoutMethod = null;
        for (Method method : PatternLayout.class.getMethods()) {
            if (method.getName().equals("createLayout")) {
                createLayoutMethod = method;
            }
        }
        if (createLayoutMethod == null) {
            ServerButler.plugin.getLogger().severe("Failed to reflectively find the Log4j createLayout method. The console appender is not going to function.");
            PATTERN_LAYOUT = null;
        } else {
            Object[] args = new Object[createLayoutMethod.getParameterCount()];
            args[0] = "[%d{HH:mm:ss} %level]: %msg";
            if (args.length == 9) {
                // log4j 2.1
                args[5] = true;
                args[6] = true;
            }

            PatternLayout createdLayout = null;
            try {
                createdLayout = (PatternLayout) createLayoutMethod.invoke(null, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                ServerButler.plugin.getLogger().severe("Failed to reflectively invoke the Log4j createLayout method. The console appender is not going to function.");
                e.printStackTrace();
            }
            PATTERN_LAYOUT = createdLayout;
        }
    }

    public ConsoleAppender() {
        super("DiscordSRV-ConsoleChannel", null, PATTERN_LAYOUT, false);

        Logger rootLogger = (Logger) LogManager.getRootLogger();
        rootLogger.addAppender(this);
    }

    @Override
    public boolean isStarted() {
        return PATTERN_LAYOUT != null;
    }

    @Override
    public void append(LogEvent e) {
        // return if console channel isn't available
        // if (DiscordSRV.getPlugin().getConsoleChannel() == null) return;

        // return if this is not an okay level to send
        //  boolean isAnOkayLevel = false;
        //  for (String consoleLevel : DiscordSRV.config().getStringList("DiscordConsoleChannelLevels")) if (consoleLevel.toLowerCase().equals(e.getLevel().name().toLowerCase())) isAnOkayLevel = true;
        //  if (!isAnOkayLevel) return;

        String line = e.getMessage().getFormattedMessage();

        // remove coloring
        line = ChatColor.stripColor(line);

        // do nothing if line is blank before parsing
        if (StringUtils.isBlank(line)) return;


        // do nothing if line is blank after parsing
        if (StringUtils.isBlank(line)) return;

        // escape markdown
        //line = .escapeMarkdown(line);

        // apply formatting
        line = "[%date% %level%] %line%"
                .replace("%date%", TimeUtils.dateToString(new DateTime()))
                .replace("%level%", e.getLevel().name().toUpperCase())
                .replace("%line%", line)
        ;

        // if line contains a blocked phrase don't send it


        // queue final message
        ServerButler.getInstance().getConsoleMessageQueue().add(line);
    }

}