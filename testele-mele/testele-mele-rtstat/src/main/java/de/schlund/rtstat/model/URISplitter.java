package de.schlund.rtstat.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class URISplitter {
    private static final Logger LOG = Logger.getLogger(URISplitter.class);
    
    private final static String USER_DOMAIN_RE = "sips?:(?:\\+?(\\S+)@)?" + "([-.\\p{Alnum}]+)";
    private final static Pattern USER_DOMAIN_PA = Pattern.compile(USER_DOMAIN_RE, Pattern.CASE_INSENSITIVE);
    
    public static UserDomain splitURI(String string) {
        Matcher m = USER_DOMAIN_PA.matcher(string);
        if (m.find()) {
            return new UserDomain(m.group(1), m.group(2));
        } else {
//            throw new IllegalArgumentException("unable to parse [" + string + "]");
            LOG.debug("Unable to parse [" + string + "], setting to null");
            return null;
        }
    }
}
