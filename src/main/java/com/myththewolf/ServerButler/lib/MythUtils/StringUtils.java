package com.myththewolf.ServerButler.lib.MythUtils;

import com.myththewolf.ServerButler.ServerButler;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class contains various String utils
 */
public class StringUtils {
    /**
     * The start sequence of a hidden string in a item's lore
     */
    private static final String SEQUENCE_HEADER = "" + ChatColor.RESET + ChatColor.UNDERLINE + ChatColor.RESET;
    /**
     * The start sequence of a hidden string in a item's lore
     */
    private static final String SEQUENCE_FOOTER = "" + ChatColor.RESET + ChatColor.ITALIC + ChatColor.RESET;

    private static final Random random = new Random();
    private static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890!@#$";

    public static void writeFile(String fileName, String text) {
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(text);

            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }

    public static String readFile(String fileName) {
        // This will reference one line at a time
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Replaces parameters, denoted by the pattern "{x}" where x represents parameter indexes
     *
     * @param raw    The raw String
     * @param values The values to replace
     * @return The replaced String
     */
    public static String replaceParameters(String raw, String... values) {
        String rep = raw;
        int pos = 0;
        for (String val : values) {
            rep = rep.replace("{" + pos + "}", val);
            pos++;
        }
        return rep;
    }

    public static <E extends Enum<E>> boolean enumContains(Class<E> _enumClass,
                                                           String value) {
        try {
            return EnumSet.allOf(_enumClass)
                    .contains(Enum.valueOf(_enumClass, value));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Turns a array (with the specified start index) into a string, putting a space between values
     *
     * @param index The start index
     * @param arr   The array
     * @return The value String
     */
    public static String arrayToString(int index, String[] arr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = index; i < arr.length; i++) {
            stringBuilder.append(arr[i] + " ");
        }
        return stringBuilder.toString();
    }


    private static String stringToColors(String normal) {
        if (normal == null) return null;

        byte[] bytes = normal.getBytes(StandardCharsets.UTF_8);
        char[] chars = new char[bytes.length * 4];

        for (int i = 0; i < bytes.length; i++) {
            char[] hex = byteToHex(bytes[i]);
            chars[i * 4] = ChatColor.COLOR_CHAR;
            chars[i * 4 + 1] = hex[0];
            chars[i * 4 + 2] = ChatColor.COLOR_CHAR;
            chars[i * 4 + 3] = hex[1];
        }

        return new String(chars);
    }

    private static String colorsToString(String colors) {
        if (colors == null) return null;

        colors = colors.toLowerCase().replace("" + ChatColor.COLOR_CHAR, "");

        if (colors.length() % 2 != 0) {
            colors = colors.substring(0, (colors.length() / 2) * 2);
        }

        char[] chars = colors.toCharArray();
        byte[] bytes = new byte[chars.length / 2];

        for (int i = 0; i < chars.length; i += 2) {
            bytes[i / 2] = hexToByte(chars[i], chars[i + 1]);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    private static int hexToUnsignedInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'a' && c <= 'f') {
            return c - 87;
        } else {
            throw new IllegalArgumentException("Invalid hex char: out of range");
        }
    }

    private static char unsignedIntToHex(int i) {
        if (i >= 0 && i <= 9) {
            return (char) (i + 48);
        } else if (i >= 10 && i <= 15) {
            return (char) (i + 87);
        } else {
            throw new IllegalArgumentException("Invalid hex int: out of range");
        }
    }

    private static byte hexToByte(char hex1, char hex0) {
        return (byte) (((hexToUnsignedInt(hex1) << 4) | hexToUnsignedInt(hex0)) + Byte.MIN_VALUE);
    }

    private static char[] byteToHex(byte b) {
        int unsignedByte = (int) b - Byte.MIN_VALUE;
        return new char[]{unsignedIntToHex((unsignedByte >> 4) & 0xf), unsignedIntToHex(unsignedByte & 0xf)};
    }

    /**
     * Converts a ArrayList into a String, seperated by ","
     *
     * @param array The ArrayList to serialize
     * @return The serialized ArrayList String
     */
    public static String serializeArray(List<String> array) {
        String it = "";
        for (Object o : array) {
            it += o.toString() + ",";
        }
        return it.endsWith(",") ? it.substring(0, it.length() - 1) : it;
    }

    /**
     * Converts a ArrayList serial string to an array
     *
     * @param in The serialized ArrayList string
     * @return The new ArrayList
     */
    public static List<String> deserializeArray(String in) {
        return Arrays.asList(in.split(","));
    }

    /**
     * Extracts a hidden string from a ItemStack
     *
     * @param source The item to extract the String from
     * @return A optional, empty if no hidden String is present
     */
    public static Optional<String> getEmeddedString(ItemStack source) {
        if(!source.hasItemMeta()){
            return Optional.empty();
        }
        ItemMeta meta = source.getItemMeta();
        NamespacedKey key = new NamespacedKey(ServerButler.plugin, "MythPacketContainer");
        if (!(meta != null && meta.getPersistentDataContainer().has(key, PersistentDataType.STRING))) {
            return Optional.empty();
        }
        String extracted = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return Optional.ofNullable(extracted);
    }

    /**
     * Parses a JSONObject string
     *
     * @param in The JSON String to parse
     * @return A optional, empty if the JSON String is invalid.
     */
    public static Optional<JSONObject> parseJSONObject(String in) {
        org.json.JSONObject jsonObject = null;
        try {
            jsonObject = new org.json.JSONObject(in);
        } catch (JSONException ex) {
            jsonObject = null;
        }
        return Optional.ofNullable(jsonObject);
    }

    public static boolean isInt(String in) {
        try {
            Integer.parseInt(in);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return token.toString();
    }

    public static Optional<String> readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return Optional.of(new String(encoded, encoding));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    public static String getStackTrace(Exception E) {
        StringWriter sw = new StringWriter();
        E.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        return exceptionAsString;
    }

}
