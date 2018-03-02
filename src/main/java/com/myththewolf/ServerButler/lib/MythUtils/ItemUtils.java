package com.myththewolf.ServerButler.lib.MythUtils;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This class contains various Item utils
 */
public class ItemUtils {
    /**
     * Creates a item attached with a OPEN_CHANNEL packet
     *
     * @param channel The channel to package in the packet
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeOpenChannelItemStack(ChatChannel channel) {
        Wool W = new Wool();
        W.setColor(DyeColor.LIME);
        ItemStack stack = W.toItemStack(1);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.TOGGLE_CHANNEL_ON);
        packet.put("channelID", channel.getID());
        String[] lore = {"Toggle this channel on", StringUtils.encodeStringForItemStack(packet.toString())};
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("View this channel");
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Creates a item with a CLOSE_CHANNEL packet
     *
     * @param channel The channel to package in the packet
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeCloseChannelItemStack(ChatChannel channel) {
        ItemStack stack = new ItemStack(Material.BARRIER);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.TOGGLE_CHANNEL_OFF);
        packet.put("channelID", channel.getID());
        String[] lore = {"Toggle this channel off", StringUtils.encodeStringForItemStack(packet.toString())};
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Stop viewing this channel");
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Creates a item with a SET_WRITE_CHANNEL packet
     *
     * @param channel The channel to package in the packet
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeWriteToChannelItemStack(ChatChannel channel) {
        ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.SET_WRITE_CHANNEL);
        packet.put("channelID", channel.getID());
        String[] lore = {"Set this as your writing channel", StringUtils.encodeStringForItemStack(packet.toString())};
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Set this as your writing channel");
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Applies JSON to a item via hiding it in the lore
     *
     * @param json   The JSON to apply
     * @param source The item to apply it to
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack applyJSON(JSONObject json, ItemStack source) {
        ItemStack copy = source;
        ItemMeta meta = source.getItemMeta();
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        lore.add(StringUtils.encodeStringForItemStack(json.toString()));
        meta.setLore(lore);
        source.setItemMeta(meta);
        return copy;
    }
}
