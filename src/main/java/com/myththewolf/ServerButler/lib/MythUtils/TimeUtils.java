package com.myththewolf.ServerButler.lib.MythUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeUtils {
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

    public static DateTime timeFromString(String input) {
        return formatter.parseDateTime(input);
    }

    public static String dateToString(DateTime input){
        return formatter.print(input);
    }
}
