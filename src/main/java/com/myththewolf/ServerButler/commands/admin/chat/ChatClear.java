package com.myththewolf.ServerButler.commands.admin.chat;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class ChatClear extends CommandAdapter {
    @Override
    @CommandPolicy(userRequiredArgs = 0, consoleRequiredArgs = 1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {

        if (args.length == 0) {
            for (int i = 0; i < 45; i++) {
                sender.get().getWritingChannel().get().pushRaw("");
            }
            sender.get().getWritingChannel().get().push(ChatColor.AQUA + "[This chat has been cleared]");
            if (ConfigProperties.ENABLE_DISCORD_BOT) {
                sender.get().getWritingChannel().orElseThrow(IllegalStateException::new).getDiscordChannel().getMessages(45).join().deleteAll().whenComplete((aVoid, throwable) -> {
                    sender.get().getWritingChannel().orElseThrow(IllegalStateException::new).getDiscordChannel().sendMessage("[This chat has been cleared]");
                });

            }
        } else {
            if (args[0].toLowerCase().equals("all-channels")) {
                DataCache.getAllChannels().forEach(chatChannel -> {
                    for (int i = 0; i < 45; i++) {
                        chatChannel.pushRaw("");
                    }
                    chatChannel.push(ChatColor.AQUA + "[This chat has been cleared]");
                    if (ConfigProperties.ENABLE_DISCORD_BOT) {
                        chatChannel.getDiscordChannel().getMessages(45).join().deleteAll().whenComplete((aVoid, throwable) -> {
                            chatChannel.getDiscordChannel().sendMessage("[This chat has been cleared]");
                        });

                    }
                });
                return;
            }
            Optional<ChatChannel> optionalChatChannel = DataCache.getOrMakeChannel(args[0]);
            if (!optionalChatChannel.isPresent()) {
                reply(ConfigProperties.PREFIX + ChatColor.RED + "No such channel");
                return;
            }
            for (int i = 0; i < 45; i++) {
                optionalChatChannel.get().pushRaw("");
            }
            optionalChatChannel.get().push(ChatColor.AQUA + "[This chat has been cleared]");
            if (ConfigProperties.ENABLE_DISCORD_BOT) {
                optionalChatChannel.map(ChatChannel::getDiscordChannel).orElseThrow(IllegalStateException::new).getMessages(45).join().deleteAll().whenComplete((aVoid, throwable) -> {
                    optionalChatChannel.map(ChatChannel::getDiscordChannel).orElseThrow(IllegalStateException::new).sendMessage("[This chat has been cleared]");
                });
            }
        }

    }

    @Override
    public String getRequiredPermission() {
        return ConfigProperties.ADMIN_CHAT_PERMISSION;
    }
}
