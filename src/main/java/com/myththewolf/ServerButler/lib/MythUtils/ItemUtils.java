package com.myththewolf.ServerButler.lib.MythUtils;

import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Wool;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


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

    /**
     * Creates a item with a BAN_PLAYER packet
     *
     * @param player The player to ban
     * @param mod    The moderator
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeBanUserItem(MythPlayer player, MythPlayer mod) {
        ItemStack stack = woolForColor(DyeColor.RED);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Ban User");
        stack.setItemMeta(meta);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.BAN_PLAYER.toString());
        packet.put("PLAYER-NAME", player.getName());
        packet.put("MOD-UUID", mod.getUUID());
        return applyJSON(packet, stack);
    }

    /**
     * Creates a item with a PARDON_PLAYER packet
     *
     * @param player The player to pardon
     * @param mod    The moderator
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makePardonUserItem(MythPlayer player, MythPlayer mod) {
        ItemStack stack = woolForColor(DyeColor.LIME);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Pardon User");
        stack.setItemMeta(meta);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.PARDON_PLAYER.toString());
        packet.put("PLAYER-NAME", player.getName());
        packet.put("MOD-UUID", mod.getUUID());
        return applyJSON(packet, stack);
    }

    /**
     * Creates a item with a MUTE_PLAYER packet
     *
     * @param player The player to mute
     * @param mod    The moderator
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeMuteUserItem(MythPlayer player, MythPlayer mod) {
        ItemStack stack = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Mute User");
        stack.setItemMeta(meta);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.MUTE_PLAYER.toString());
        packet.put("PLAYER-NAME", player.getName());
        packet.put("MOD-UUID", mod.getUUID());
        return applyJSON(packet, stack);
    }

    /**
     * Creates a item with a SOFTMUTE_PLAYER packet
     *
     * @param player The player to softmute
     * @param mod    The moderator
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeSoftmuteUserItem(MythPlayer player, MythPlayer mod) {
        ItemStack stack = new ItemStack(Material.EYE_OF_ENDER);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Softmute User");
        stack.setItemMeta(meta);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.SOFTMUTE_PLAYER.toString());
        packet.put("PLAYER-NAME", player.getName());
        packet.put("MOD-UUID", mod.getUUID());
        return applyJSON(packet, stack);
    }

    /**
     * Creates a item with a UNMUTE_PLAYER packet
     *
     * @param player The player to unmute
     * @param mod    The moderator
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makUnmuteUserItem(MythPlayer player, MythPlayer mod) {
        ItemStack stack = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Unmute Player");
        stack.setItemMeta(meta);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.UNMUTE_PLAYER.toString());
        packet.put("PLAYER-NAME", player.getName());
        packet.put("MOD-UUID", mod.getUUID());
        return applyJSON(packet, stack);
    }

    /**
     * Creates a wool itemstack for the given color
     *
     * @param color The color to dye the wool
     * @return The colored wool
     */
    public static ItemStack woolForColor(DyeColor color) {
        Wool w = new Wool();
        w.setColor(color);
        return w.toItemStack(1);
    }

    public static ItemStack makeKickItem(MythPlayer target) {
        ItemStack stack = new ItemStack(Material.JUNGLE_DOOR);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Kick player");
        stack.setItemMeta(meta);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.KICK_PLAYER);
        packet.put("PLAYER-UUID", target.getUUID());
        return applyJSON(packet, stack);
    }

    public static ItemStack nameItem(String x, ItemStack source) {
        ItemMeta meta = source.getItemMeta();
        meta.setDisplayName(x);
        source.setItemMeta(meta);
        return source;
    }

    public static ItemStack getSkullofPlayer(String playerUUID) {
        ItemStack raw = new ItemStack(Material.SKULL_ITEM, (short) 3);
        SkullMeta skullMeta = (SkullMeta) raw.getItemMeta();
        skullMeta.setOwner(Bukkit.getOfflinePlayer(UUID.fromString(playerUUID)).getName());
        raw.setItemMeta(skullMeta);
        return raw;
    }
}
