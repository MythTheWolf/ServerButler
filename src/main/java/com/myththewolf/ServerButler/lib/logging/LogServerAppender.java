package com.myththewolf.ServerButler.lib.logging;


import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;


import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public abstract class LogServerAppender implements Filter {

    public Result check(Logger logger, Level level, String message, Throwable throwable) {
        // only listen for JDA logs
        if (!logger.getName().startsWith("github.scarsz.discordsrv.dependencies.jda")) return Result.NEUTRAL;

        switch (level.name()) {
            case "INFO":
                ServerButler.plugin.getLogger().info("[JC] " + message);
                break;
            case "WARN":
                ServerButler.plugin.getLogger().warning("[JC] " + message);
                break;
            case "ERROR":
                if (throwable != null) {
                    ServerButler.plugin.getLogger().severe("[JC] " + message + "\n" + ExceptionUtils.getStackTrace(throwable));
                } else {
                    ServerButler.plugin.getLogger().severe("[JC] " + message);
                }
                break;
            default:
                if (ConfigProperties.DEBUG) ServerButler.plugin.getLogger().info("[JDA] " + message);
        }

        // all JDA messages should be denied because we handle them ourselves
        return Result.DENY;
    }

    @Override
    public Result filter(LogEvent logEvent) {
        return check((Logger) LogManager.getLogger(logEvent.getLoggerName()), logEvent.getLevel(), logEvent.getMessage().getFormattedMessage(), logEvent.getThrown());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String message, Object... parameters) {
        return check(logger, level, message, null);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object message, Throwable throwable) {
        return check(logger, level, message.toString(), throwable);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message message, Throwable throwable) {
        return check(logger, level, message.getFormattedMessage(), throwable);
    }

    public void start() {
    }

    public void stop() {
    }

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    @Override
    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    @Override
    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

}