package de.schlund.rtstat.util;

import java.text.DateFormat;
import java.util.Locale;


public class Util {
    
    public static String readableTimestamp(long timestamp) {
        return DateFormat.getDateTimeInstance(2, 2, Locale.GERMAN).format(timestamp);
    }
    
}
