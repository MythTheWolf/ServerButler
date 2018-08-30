package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.json.JSONObject;

public class UpdateContentHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        ServerButler.conversationBuilder.withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return ConfigProperties.PREFIX + "Please enter the new content to display";
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                conversationContext.setSessionData("packetType", PacketType.UPDATE_CONTENT);
                conversationContext.setSessionData("content", s);
                conversationContext.setSessionData("ID", data.getString("ID"));
                return END_OF_CONVERSATION;
            }
        }).withEscapeSequence("^c").buildConversation(player.getBukkitPlayer().get()).begin();
    }
}
