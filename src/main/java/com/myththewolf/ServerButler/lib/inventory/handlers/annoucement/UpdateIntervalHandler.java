package com.myththewolf.ServerButler.lib.inventory.handlers.annoucement;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.ItemPacketHandler;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.json.JSONObject;

public class UpdateIntervalHandler implements ItemPacketHandler {
    @Override
    public void onPacketReceived(MythPlayer player, JSONObject data) {
        ServerButler.conversationBuilder.withEscapeSequence("^c")
                .withFirstPrompt(new RegexPrompt("((\\d{1,2}y\\s?)?(\\d{1,2}mo\\s?)?(\\d{1,2}w\\s?)?(\\d{1,2}d\\s?)?(\\d{1,2}h\\s?)?(\\d{1,2}m\\s?)?(\\d{1,2}s\\s?)?)|\\d{1,2}") {
                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                        conversationContext.setSessionData("packetType", PacketType.UPDATE_INTERVAL);
                        conversationContext.setSessionData("interval", s);
                        conversationContext.setSessionData("ID", data.getString("ID"));
                        return END_OF_CONVERSATION;
                    }

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return ConfigProperties.PREFIX + "Please specify the time interval (1d2h...)";
                    }
                }).buildConversation(player.getBukkitPlayer().get()).begin();
    }
}
