package com.myththewolf.ServerButler.lib.Chat;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.mySQL.SQLAble;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.util.logging.ExceptionLogger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents a Chat channnel
 */
public class ChatChannel implements SQLAble {
    /**
     * The name of this channel
     */
    private String name;
    /**
     * The permission node to read/write to this channel
     */
    private String permission;
    /**
     * The ID in the database of this channel
     */
    private String ID;
    /**
     * The shortcut that can be used for this channel
     */
    private String shortcut;
    /**
     * The chat prefix of this channel
     */
    private String prefix;

    private String pattern;
    private TextChannel channel;

    /**
     * Constructs a new Chat Channel, pulling data from the database
     *
     * @param ID The ID of the channel to pull data from
     */
    public ChatChannel(String ID) {
        this.ID = ID;
        try {
            ResultSet RS = prepareAndExecuteSelectThrow("SELECT * FROM `SB_Channels` WHERE ID = ?", 1, ID);
            while (RS.next()) {
                name = RS.getString("name");
                permission = RS.getString("permission");
                shortcut = RS.getString("shortcut");
                prefix = RS.getString("prefix");
                pattern = RS.getString("format");
                if (ConfigProperties.ENABLE_DISCORD_BOT) {
                    channel = RS.getString("discord_id") == null ? null : ServerButler.API
                            .getTextChannelById(RS.getString("discord_id")).orElse(null);
                }
            }
        } catch (SQLException e) {
            handleExceptionPST(e);
        }
    }

    /**
     * Constructs a new Chat Channel, assuming there is no channel with these paramaters in the database
     *
     * @param name     The channel name
     * @param perm     The channel permission node, null if none
     * @param shortcut The shortcut for this channel, null if none
     * @param prefix   The chat prefix for this channel
     */
    public ChatChannel(String name, String perm, String shortcut, String prefix, String pattern) {
        this.ID = null;
        this.name = name;
        this.permission = perm;
        this.shortcut = shortcut;
        this.prefix = prefix;
        this.pattern = pattern;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
        update();
    }

    public TextChannel getDiscordChannel() {
        return channel;
    }

    /**
     * Gets a list of all cached players who are viewing this channel
     *
     * @return The list of players
     */
    public List<MythPlayer> getAllCachedPlayers() {
        return DataCache.playerHashMap.entrySet().stream().map(Map.Entry::getValue)
                .filter(mp -> mp.getChannelList().contains(this)).collect(Collectors.toList());
    }

    /**
     * Gets a list of all players who are writing to this channel
     *
     * @return The list of authors
     */
    public List<MythPlayer> getAuthors() {
        return getAllCachedPlayers().stream().filter(player -> player.getWritingChannel().equals(this))
                .collect(Collectors.toList());
    }

    /**
     * Gets the database ID of this channel
     *
     * @return The ID
     */
    public String getID() {
        return ID;
    }

    /**
     * Gets the chat prefix of this channel
     *
     * @return The chat prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the name of this channel
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the permission node of this channel
     *
     * @return A optional, empty if no permission node is specified
     */
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    /**
     * Gets the shortcut for this channel
     *
     * @return A optional, empty if no shortcut is specified
     */
    public Optional<String> getShortcut() {
        return Optional.ofNullable(shortcut);
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * Sends a message to all players viewing this channel
     *
     * @param content The message to send
     * @param player  The player who is sending the message
     * @apiNote If player is null, the message being sent will be treated as a raw message, where the player name will not be included.
     * @deprecated We now just remove players from the recipent list via AsyncPlayerChatEvent
     */
    public void push(String content, MythPlayer player) {
        if (player == null) {
            String con = ChatColor.translateAlternateColorCodes('&', content);
            getAllCachedPlayers().stream().filter(MythPlayer::isOnline).map(p2 -> p2.getBukkitPlayer().get())
                    .forEach(bukkitPlayer -> bukkitPlayer
                            .sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix()) + con));
            String whom = "[Server Message]";
            if (ConfigProperties.ENABLE_DISCORD_BOT) {
                getDiscordChannel().sendMessage(ChatColor.stripColor(whom) + " » " + ChatColor.stripColor(con))
                        .exceptionally(ExceptionLogger.get());
            }
            return;
        }
        String parsed;
        if (player.hasPermission(ConfigProperties.COLOR_CHAT_PERMISSION)) {
            parsed = ChatColor.translateAlternateColorCodes('&', content);
        } else {
            parsed = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', content));
        }

        String message2Send = ChatColor.translateAlternateColorCodes('&', getPattern().replace("{prefix}", getPrefix())
                .replace("{channelName}", getName())
                .replace("{worldName}", player.getBukkitPlayer().get().getLocation().getWorld().getName()));
        getAllCachedPlayers().forEach(p21 -> p21.getBukkitPlayer()
                .ifPresent(p2 -> p2.sendMessage(String.format(message2Send, player.getDisplayName(), content))));
        if (ConfigProperties.ENABLE_DISCORD_BOT && getDiscordChannel() != null) {
            String con = ChatColor.translateAlternateColorCodes('&', content);
            String whom = ChatColor.translateAlternateColorCodes('&', player.getBukkitPlayer().get().getDisplayName());
            getDiscordChannel().sendMessage(ChatColor.stripColor(whom) + " » " + ChatColor.stripColor(con))
                    .exceptionally(ExceptionLogger.get());
        }
    }

    public void sendToDiscord(MythPlayer player, String content) {
        if (ConfigProperties.ENABLE_DISCORD_BOT && getDiscordChannel() != null) {
            String con = ChatColor.translateAlternateColorCodes('&', content);
            String whom = ChatColor.translateAlternateColorCodes('&', player.getBukkitPlayer().get().getDisplayName());
            getDiscordChannel().sendMessage(ChatColor.stripColor(whom) + " » " + ChatColor.stripColor(con))
                    .exceptionally(ExceptionLogger.get());
        }
    }

    public String getMessageFromContext(MythPlayer player) {
        return ChatColor.translateAlternateColorCodes('&', getPattern().replace("{prefix}", getPrefix())
                .replace("{channelName}", getName())
                .replace("{worldName}", player.getBukkitPlayer().get().getLocation().getWorld().getName())
                .replace("{isProbated}", player.isProbated() ? ChatColor.RED + "*" + "" : ""));
    }

    public void push(String content) {
        String con = ChatColor.translateAlternateColorCodes('&', content);
        getAllCachedPlayers().stream().filter(MythPlayer::isOnline).map(p2 -> p2.getBukkitPlayer().get())
                .forEach(bukkitPlayer -> bukkitPlayer
                        .sendMessage(ChatColor.translateAlternateColorCodes('&', getPrefix()) + con));
        String whom = "[Server Message]";
        if (ConfigProperties.ENABLE_DISCORD_BOT) {
            getDiscordChannel().sendMessage(ChatColor.stripColor(whom) + " » " + ChatColor.stripColor(con))
                    .exceptionally(ExceptionLogger.get());
        }
        return;
    }

    public void messagePlayer(MythPlayer player, String content) {
        String con = ChatColor.translateAlternateColorCodes('&', getPrefix() + content);
        player.getBukkitPlayer().ifPresent(player1 -> player1.sendMessage(con));
    }

    public void pushRaw(String raw) {
        getAllCachedPlayers().stream().filter(MythPlayer::isOnline).map(player -> player.getBukkitPlayer().get())
                .forEach(player -> {
                    player.sendMessage(raw);
                });
    }

    public void pushViaDiscord(String content, MythPlayer player) {
        String message2Send = ChatColor.translateAlternateColorCodes('&', getPattern()
                .replace("{prefix}", getPrefix())
                .replace("{channelName}", getName())
                .replace("{worldName}", ""));
        getAllCachedPlayers().forEach(p21 -> p21.getBukkitPlayer()
                .ifPresent(p2 -> p2.sendMessage(String
                        .format(message2Send, player
                                .getDisplayName() + ChatColor.ITALIC + " (discord)" + ChatColor.RESET, content))));
        String con = ChatColor.translateAlternateColorCodes('&', content);
        String whom = ChatColor.translateAlternateColorCodes('&', player.getDisplayName());
        getDiscordChannel().sendMessage(ChatColor.stripColor(whom) + " » " + ChatColor.stripColor(con))
                .exceptionally(ExceptionLogger.get());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof ChatChannel) && (((ChatChannel) o).getID().equals(getID()));
    }

    /**
     * Updates channel entry with the data from this class <br />
     *
     * @apiNote This method will INSERT into the database if {@link ChatChannel#getID()} is null <br /> Additionally, if INSERTing, this method will auto re-build the channel cache
     */
    public void update() {
        if (getID() == null) {
            String SQL = "INSERT INTO `SB_Channels` (`name`,`permission`,`shortcut`,`prefix`,`format`) VALUES (?,?,?,?,?)";
            prepareAndExecuteUpdateExceptionally(SQL, 5, getName(), getPermission().orElse(null), getShortcut()
                    .orElse(null), getPrefix(), getPattern());
        } else {
            String SQL = "UPDATE `SB_Channels` SET `name` = ?,`permission` = ?,`shortcut` = ?, `prefix` = ?, `format` = ?, `discord_id` = ?  WHERE `ID` = ?";
            prepareAndExecuteUpdateExceptionally(SQL, 7, getName(), getPermission().orElse(null), getShortcut()
                    .orElse(null), getPrefix(), getPattern(), getDiscordChannel()
                    .getIdAsString(), getID());
        }
        DataCache.rebuildChannelList();
    }

    @Override
    public String toString() {
        return getID();
    }
}
