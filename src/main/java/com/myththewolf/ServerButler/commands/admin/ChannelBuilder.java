package com.myththewolf.ServerButler.commands.admin;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class ChannelBuilder extends CommandAdapter {
    private String PREFIX; //
    private String SHORTCUT; //
    private String FORMAT;
    private String NAME; //
    private String PERMISSION; //
    @Override
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        ServerButler.conversationBuilder.withEscapeSequence("^c").withTimeout(60).withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return ConfigProperties.PREFIX+"Please specify the channel name";
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                if(DataCache.getOrMakeChannel(s).isPresent()){
                    conversationContext.getForWhom().sendRawMessage(ConfigProperties.PREFIX+ChatColor.RED+"Exiting due to error: That channel name already exists!");
                    return END_OF_CONVERSATION;
                }
                NAME = s;
                return new StringPrompt() {
                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return ConfigProperties.PREFIX+"Please specify the shortcut character";
                    }

                    @Override
                    public Prompt acceptInput(ConversationContext conversationContext, String s) {
                        if(DataCache.getAllChannels().stream().anyMatch(chatChannel -> chatChannel.getShortcut().equals(s))){
                            conversationContext.getForWhom().sendRawMessage(ConfigProperties.PREFIX+ChatColor.RED+"Exiting due to error: That channel shortcut already exists!");
                            return END_OF_CONVERSATION;
                        }
                        SHORTCUT = s;
                        return new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext conversationContext) {
                                return ConfigProperties.PREFIX+"Please specify the channel permission node,or null for none";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                                if(s.toLowerCase().equals("null")){
                                    PERMISSION = null;
                                }else{
                                    PERMISSION = s;
                                }
                                return new StringPrompt() {
                                    @Override
                                    public String getPromptText(ConversationContext conversationContext) {
                                        return ConfigProperties.PREFIX+"Please specify the channel prefix";
                                    }

                                    @Override
                                    public Prompt acceptInput(ConversationContext conversationContext, String s) {
                                        PREFIX = s;
                                        return new StringPrompt() {
                                            @Override
                                            public String getPromptText(ConversationContext conversationContext) {
                                                return ConfigProperties.PREFIX+"Please specify the channel parse pattern \n (see /help pattern for more info)";
                                            }

                                            @Override
                                            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                                                conversationContext.getForWhom().sendRawMessage(ConfigProperties.PREFIX+"Channel created! :)");
                                                FORMAT = s;
                                                commit(sender.get());
                                                return END_OF_CONVERSATION;
                                            }
                                        };
                                    }
                                };
                            }
                        };
                    }
                };
            }
        }).buildConversation(sender.flatMap(MythPlayer::getBukkitPlayer).get()).begin();
    }
    private void commit(MythPlayer send){
        ChatChannel chatChannel = new ChatChannel(NAME,PERMISSION,SHORTCUT,PREFIX,FORMAT);
        chatChannel.update();
    }
}
