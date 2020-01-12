package com.myththewolf.ServerButler.lib.MythUtils;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
                sb.append(line);
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

    /**
     * Converts a String to to a encoded/hidden String for a ItemStack
     *
     * @param hiddenString The String to hide
     * @return The new hidden String
     */
    public static String encodeStringForItemStack(String hiddenString) {
        return quote(stringToColors(hiddenString));
    }

    /**
     * Checks if a String is hidden
     *
     * @param input The String to check
     * @return true if the String is hidden
     */
    public static boolean hasHiddenString(String input) {
        if (input == null) return false;

        return input.indexOf(SEQUENCE_HEADER) > -1 && input.indexOf(SEQUENCE_FOOTER) > -1;
    }

    /**
     * Decodes a hidden String
     *
     * @param input The Sting to decode
     * @return The decoded String
     */
    public static String extractHiddenString(String input) {
        return colorsToString(extract(input));
    }


    /**
     * Attaches the SEQUENCE_HEADER and the SEQUENCE_FOOTER to a given string
     *
     * @param input The string to quote
     * @return The String, with the headers
     */
    private static String quote(String input) {
        if (input == null) return null;
        return SEQUENCE_HEADER + input + SEQUENCE_FOOTER;
    }

    /**
     * Extracts the String between the two headers
     *
     * @param input The String to extract from
     * @return The extracted String
     */
    private static String extract(String input) {
        if (input == null) return null;

        int start = input.indexOf(SEQUENCE_HEADER);
        int end = input.indexOf(SEQUENCE_FOOTER);

        if (start < 0 || end < 0) {
            return null;
        }

        return input.substring(start + SEQUENCE_HEADER.length(), end);
    }

    private static String stringToColors(String normal) {
        if (normal == null) return null;

        byte[] bytes = normal.getBytes(Charset.forName("UTF-8"));
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

        return new String(bytes, Charset.forName("UTF-8"));
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
     * @param item The item to extract the String from
     * @return A optional, empty if no hidden String is present
     */
    public static Optional<String> getEmeddedString(ItemStack item) {
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getLore() == null) {
            return Optional.empty();
        }
        return item.getItemMeta().getLore().stream().filter(StringUtils::hasHiddenString)
                .map(StringUtils::extractHiddenString).findFirst();
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
