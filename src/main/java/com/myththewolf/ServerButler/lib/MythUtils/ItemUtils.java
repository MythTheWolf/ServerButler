package com.myththewolf.ServerButler.lib.MythUtils;

import com.myththewolf.ServerButler.ServerButler;
import com.myththewolf.ServerButler.lib.Chat.ChatChannel;
import com.myththewolf.ServerButler.lib.cache.DataCache;
import com.myththewolf.ServerButler.lib.inventory.interfaces.PacketType;
import com.myththewolf.ServerButler.lib.player.interfaces.MythPlayer;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONObject;

import java.util.Arrays;


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
        ItemStack stack = new ItemStack(Material.LIME_WOOL, 1);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.TOGGLE_CHANNEL_ON);
        packet.put("channelID", channel.getID());
        String[] lore = {"Toggle this channel on",};
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("View this channel");
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return applyJSON(packet, stack);
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
        String[] lore = {"Toggle this channel off"};
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Stop viewing this channel");
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return applyJSON(packet, stack);
    }

    /**
     * Creates a item with a SET_WRITE_CHANNEL packet
     *
     * @param channel The channel to package in the packet
     * @return The new ItemStack, with JSON applied
     */
    public static ItemStack makeWriteToChannelItemStack(ChatChannel channel) {
        ItemStack stack = new ItemStack(Material.WRITABLE_BOOK);
        JSONObject packet = new JSONObject();
        packet.put("packetType", PacketType.SET_WRITE_CHANNEL);
        packet.put("channelID", channel.getID());
        String[] lore = {"Set this as your writing channel"};
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName("Set this as your writing channel");
        meta.setLore(Arrays.asList(lore));
        stack.setItemMeta(meta);
        return applyJSON(packet, stack);
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
        NamespacedKey key = new NamespacedKey(ServerButler.plugin, "mythPacketContainer");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, json.toString());
        copy.setItemMeta(meta);
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
        ItemStack stack = new ItemStack(Material.ENDER_EYE);
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
    public static ItemStack makeUnmuteUserItem(MythPlayer player, MythPlayer mod) {
        ItemStack stack = new ItemStack(Material.ENDER_EYE);
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
        Material material = Material.valueOf(color.toString().toUpperCase() + "_WOOL");
        return new ItemStack(material, 1);
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

    public static ItemStack nameItem(String x, ItemStack source, String... lore) {
        ItemMeta meta = source.getItemMeta();
        meta.setDisplayName(x);
        meta.setLore(Arrays.asList(lore));
        source.setItemMeta(meta);
        return source;
    }

    public static ItemStack getSkullofPlayer(String playerUUID) {
        ItemStack raw = new ItemStack(Material.PLAYER_HEAD, (short) 3);
        SkullMeta skullMeta = (SkullMeta) raw.getItemMeta();
        skullMeta.setOwningPlayer(DataCache.getPlayer(playerUUID).orElseThrow(IllegalStateException::new).getOfflinePlayer());
        raw.setItemMeta(skullMeta);
        return raw;
    }

    public static int findInventorySize(int listSize) {
        if (listSize <= 9) {
            return 9;
        } else {
            int sizeNow = 9;
            while (listSize > sizeNow) {
                sizeNow += 9;
            }
            return sizeNow;
        }
    }
}
