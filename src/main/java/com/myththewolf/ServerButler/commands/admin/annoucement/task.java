package com.myththewolf.ServerButler.commands.admin.annoucement;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatAnnoucement;
import com.myththewolf.ServerButler.lib.MythUtils.CustomDyeColor;
import com.myththewolf.ServerButler.lib.MythUtils.ItemUtils;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.command.impl.CommandAdapter;
import com.myththewolf.ServerButler.lib.command.interfaces.CommandPolicy;
import com.myththewolf.ServerButler.lib.config.ConfigProperties;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.logging.Loggable;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.RegexPrompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.util.Optional;

public class task extends CommandAdapter implements Loggable {
    @Override
    @CommandPolicy(userRequiredArgs = 1, consoleRequiredArgs = -1)
    public void onCommand(Optional<MythPlayer> sender, String[] args, JavaPlugin javaPlugin) {
        if (args[0].equals("create")) {
            assert sender.orElse(null) != null;
            create(sender.orElse(null));
            return;
        }
        if (!sender.isPresent() || !sender.get().getBukkitPlayer().isPresent()) {
            return;
        }
        if (!DataCache.getAnnouncement(args[0]).isPresent()) {
            reply(ConfigProperties.PREFIX + ChatColor.RED + "ID not found");
            return;
        }
        reply(ConfigProperties.PREFIX + "Reading database...");
        ChatAnnoucement target = DataCache.getAnnouncement(args[0]).get();
        JSONObject packetAddChannel = new JSONObject();
        packetAddChannel.put("packetType", PacketType.CHANNEL_SELECTION_CONTINUE);
        packetAddChannel.put("targetPacketType", PacketType.ADD_CHANNEL);
        packetAddChannel.put("ID", target.getId());
        ItemStack itemAddChannel = ItemUtils.nameItem("Add channels", ItemUtils
                .applyJSON(packetAddChannel, ItemUtils.woolForColor(DyeColor.LIME)));
        JSONObject packetRemoveChannel = new JSONObject();
        packetRemoveChannel.put("packetType", PacketType.CHANNEL_SELECTION_CONTINUE);
        packetRemoveChannel.put("targetPacketType", PacketType.REMOVE_CHANNEL);
        packetRemoveChannel.put("ID", target.getId());
        ItemStack itemRemoveChannel = ItemUtils.nameItem("Remove Channels", ItemUtils
                .applyJSON(packetRemoveChannel, ItemUtils.woolForColor(DyeColor.RED)));
        JSONObject packetUpdateContent = new JSONObject();
        packetUpdateContent.put("packetType", PacketType.UPDATE_CONTENT);
        packetUpdateContent.put("ID", target.getId());
        ItemStack itemUpdateContent = ItemUtils.nameItem("Update Content", ItemUtils
                .applyJSON(packetUpdateContent, new ItemStack(Material.BOOK_AND_QUILL, 1)));
        JSONObject packetUpdateInterval = new JSONObject();
        packetUpdateInterval.put("packetType", PacketType.UPDATE_INTERVAL);
        packetUpdateInterval.put("ID", target.getId());
        ItemStack itemUpdateInterval = ItemUtils.nameItem("Update Interval", ItemUtils
                .applyJSON(packetUpdateInterval, new ItemStack(Material.WATCH, 1)));
        JSONObject packetUpdatePermission = new JSONObject();
        packetUpdatePermission.put("packetType", PacketType.UPDATE_PERMISSION);
        packetUpdatePermission.put("ID", target.getId());
        ItemStack itemUpdatePermission = ItemUtils.nameItem("Update Permission", ItemUtils
                .applyJSON(packetUpdatePermission, new ItemStack(Material.SHIELD, 1)));
        JSONObject packetStartTask = new JSONObject();
        packetStartTask.put("packetType", PacketType.START_ANNOUNCEMENT);
        packetStartTask.put("ID", target.getId());
        ItemStack itemStartTask = ItemUtils.nameItem("Start task", ItemUtils
                .applyJSON(packetStartTask, new ItemStack(Material.INK_SACK, 1, CustomDyeColor.LIME.getData())));
        JSONObject packetStopTask = new JSONObject();
        packetStopTask.put("packetType", PacketType.STOP_ANNOUNCEMENT);
        packetStopTask.put("ID", target.getId());
        ItemStack itemStopTask = ItemUtils.nameItem("Stop task", ItemUtils
                .applyJSON(packetStopTask, new ItemStack(Material.INK_SACK, 1, CustomDyeColor.RED.getData())));
        JSONObject packetDeleteItem = new JSONObject();
        packetDeleteItem.put("packetType", PacketType.DELETE_ANNOUNCEMENT);
        packetDeleteItem.put("ID", target.getId());
        ItemStack itemDelete = ItemUtils.nameItem("Delete announcement", ItemUtils
                .applyJSON(packetDeleteItem, new ItemStack(Material.BARRIER, 1)));
        Inventory I = Bukkit.createInventory(null, 9, "Options for Announcement #" + target.getId());
        I.setItem(0, itemAddChannel);
        I.setItem(1, itemRemoveChannel);
        I.setItem(2, itemUpdateContent);
        I.setItem(3, itemUpdateInterval);
        I.setItem(4, itemUpdatePermission);
        I.setItem(5, itemStartTask);
        I.setItem(6, itemStopTask);
        I.setItem(7, itemDelete);
        sender.get().getBukkitPlayer().ifPresent(player -> player.openInventory(I));
    }

    private void create(MythPlayer src) {
        Player player = src.getBukkitPlayer().get();
        ServerButler.conversationBuilder.withEscapeSequence("^c").withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext conversationContext) {
                return ConfigProperties.PREFIX + "Please specify the announcement content:";
            }

            @Override
            public Prompt acceptInput(ConversationContext conversationContext, String s) {
                conversationContext.setSessionData("content", s);
                return new RegexPrompt("((\\d{1,2}y\\s?)?(\\d{1,2}mo\\s?)?(\\d{1,2}w\\s?)?(\\d{1,2}d\\s?)?(\\d{1,2}h\\s?)?(\\d{1,2}m\\s?)?(\\d{1,2}s\\s?)?)|\\d{1,2}") {
                    @Override
                    protected Prompt acceptValidatedInput(ConversationContext conversationContext, String s) {
                        conversationContext.setSessionData("interval", s);
                        return new StringPrompt() {
                            @Override
                            public String getPromptText(ConversationContext conversationContext) {
                                return ConfigProperties.PREFIX + "Please specify the permission node, or null for none";
                            }

                            @Override
                            public Prompt acceptInput(ConversationContext con, String s) {
                                JSONObject packet = new JSONObject();
                                packet.put("targetPacketType", PacketType.INSERT_ANNOUNCEMENT.toString());
                                packet.put("permission", s.equals("none") ? null : s);
                                packet.put("interval", con.getSessionData("interval"));
                                packet.put("content", con.getSessionData("content"));
                                packet.put("packetType", PacketType.CHANNEL_SELECTION_CONTINUE);
                                con.setSessionData("packet", packet);
                                con.setSessionData("player", src);
                                con.setSessionData("packetType", PacketType.CHANNEL_SELECTION_CONTINUE);
                                getLogger().info(packet.toString());
                                conversationContext.getForWhom()
                                        .sendRawMessage(ConfigProperties.PREFIX + "Opening channel menu, please select all channels that this announcement applies to.");
                                try {
                                    Thread.sleep(2000);
                                    return END_OF_CONVERSATION;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return END_OF_CONVERSATION;
                            }
                        };
                    }

                    @Override
                    public String getPromptText(ConversationContext conversationContext) {
                        return ConfigProperties.PREFIX + "Please specify the interval (1d2h...):";
                    }
                };
            }
        }).buildConversation(player).begin();
    }
}
