package com.myththewolf.ServerButler.lib.MythUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * This class holds various DateTime utils
 */
public class TimeUtils {
    /**
     * The pattern used for parsing Dates
     */
    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Parses a date/time with the {@link TimeUtils#formatter} pattern
     *
     * @param input The date/time String to parse
     * @return The parsed DateTime
     */
    public static DateTime timeFromString(String input) {
        return formatter.parseDateTime(input);
    }

    /**
     * Prints a DateTime object to string using the {@link TimeUtils#formatter} pattern
     *
     * @param input The date to print
     * @return The DateTime string
     */
    public static String dateToString(DateTime input) {
        return formatter.print(input);
    }

    public static PeriodFormatter TIME_INPUT_FORMAT() {
        return new PeriodFormatterBuilder().appendYears().appendSuffix("y").appendMonths().appendSuffix("mo")
                .appendWeeks().appendSuffix("w").appendDays().appendSuffix("d").appendHours().appendSuffix("h")
                .appendMinutes().appendSuffix("m").appendSeconds().appendSuffix("s").toFormatter();
    }
}
