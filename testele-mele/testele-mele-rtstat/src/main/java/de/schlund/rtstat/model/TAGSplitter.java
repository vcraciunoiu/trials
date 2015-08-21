package de.schlund.rtstat.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TAGSplitter {
    private final static Pattern TAG_PA = Pattern.compile("tag=([-._+!%*`'~\\p{Alnum}]+)", Pattern.CASE_INSENSITIVE);
 
    static public String getTag(String string) {
        Matcher m2 = TAG_PA.matcher(string);
        if (m2.find()) {
            return m2.group(1);
        } else {
            return null;
        }

    }
}
