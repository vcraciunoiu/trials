package de.schlund.rtstat.model;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * This class parses the new OpenSER log format, with shrinked filed names.
 * 
 * @author VladC
 * 
 */
public class SERLogEventFactoryV3 implements SERLogEventFactory {

    private static final Logger LOG = Logger.getLogger(SERLogEventFactory.class);

    private static final String DATE_PATTERN = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}[\\+-]\\d{2}:\\d{2})";
    private static final String METHOD_PATTERN = "md=(.*)";
    private static final String FROM_TAG_PATTERN = ", fg=(.*)";
    private static final String TO_TAG_PATTERN = ", tg=(.*)";
    private static final String CALLID_PATTERN = ", i=(.*)";
    private static final String CODE_PATTERN = ", sc=(.*)";
    private static final String IURI_PATTERN = ", iu=(.*)";
    private static final String OURI_PATTERN = ", ou=(.*)";
    private static final String FROM_PATTERN = ", f=(.*)";
    private static final String TO_PATTERN = ", t=(.*)";
    private static final String CSEQ_PATTERN = ", cq=(.*)";
    private static final String IDENTITY_PATTERN = ", id=(.*)";
    private static final String FROMPROVIDER_PATTERN = ", fp=(.*)";
    private static final String FLAGS_PATTERN = ", fs=(.*)";
    private static final String LAST_DST_PATTERN = ", ld=(.*)";
    private static final String UA1_PATTERN = ", u1=(.*)";
    private static final String UA2_PATTERN = ", u2=(.*)";
    private static final String XRTP1_PATTERN = ", x1=(.*)";
    private static final String XRTP2_PATTERN = ", x2=(.*)";
    private static final String TARGET_PATTERN = ", tt=(.*)";
    private static final String CONTACT_DOMAIN_PATTERN = ", ct=(.*)";

    private static final String MIGRATED_PATTERN = ", mi=(.*)";

    private static final String pattern = DATE_PATTERN + ".*" + 
                                          METHOD_PATTERN + 
                                          FROM_TAG_PATTERN + 
                                          TO_TAG_PATTERN + 
                                          CALLID_PATTERN + 
                                          CODE_PATTERN + 
                                          IURI_PATTERN + 
                                          OURI_PATTERN + 
                                          FROM_PATTERN + 
                                          TO_PATTERN + 
                                          CSEQ_PATTERN + 
                                          IDENTITY_PATTERN + 
                                          MIGRATED_PATTERN +
                                          FROMPROVIDER_PATTERN + 
                                          FLAGS_PATTERN + 
                                          LAST_DST_PATTERN + 
                                          UA1_PATTERN + 
                                          UA2_PATTERN + 
                                          XRTP1_PATTERN + 
                                          XRTP2_PATTERN + 
                                          TARGET_PATTERN + 
                                          CONTACT_DOMAIN_PATTERN 
                                          ;

    private static Pattern p = Pattern.compile(pattern);

    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static final String TAL_RSP = "tal-rsp-";

    private static XRTPParser xp = new XRTPParser();

    public SERLogEvent parse(CharSequence cs) {
        SERLogEvent ev = new SERLogEvent();
        String log = cs.toString();

        // line in syslog format?
        if (log.startsWith("<")) {
            int index = log.indexOf('>');
            log = log.substring(index + 1);
        }

        Matcher m = p.matcher(log);

        if (m.lookingAt()) {
            String s = m.group(1);
            ev.setTimestamp(guessDate(s));

            s = m.group(2);
            ev.setMethod(Method.valueOf(s));

            s = m.group(3);
            ev.setFromTag(s);

            s = m.group(4);
            ev.setToTag(s);

            s = m.group(5);
            ev.setCallId(s);

            s = m.group(6);
            ev.setCode(Short.parseShort(s));

            s = m.group(7);
            ev.setIURI(URISplitter.splitURI(s));

            s = m.group(8);
            ev.setOURI(URISplitter.splitURI(s));

            s = m.group(9);
            ev.setFrom(URISplitter.splitURI(s));

            s = m.group(10);
            ev.setTo(URISplitter.splitURI(s));

            s = m.group(11);
            // cseq - unused

            s = m.group(12);
            ev.setIdentity(s);

            s = m.group(13);
            ev.setMigrated(s);

            s = m.group(14);
            ev.setFromProvider(s);

            s = m.group(15);
            ev.setFlags(s);

            s = m.group(16);
            ev.setLastDestination(s);

            s = m.group(17);
            ev.setUa1(s);

            s = m.group(18);
            ev.setUa2(s);

            XRTPValue xrtpv = null;
            s = m.group(19);
            if (s.length() != 0) {
                try {
                    xrtpv = xp.parse(s);
                } catch (Exception e) {
                    LOG.error("XRTP1[" + s + "] not parseable. callid=" + ev.getCall_id() + ". log line: \n" + log + "\n", e);
                }
                ev.setXrtp1(xrtpv);
            }

            xrtpv = null;
            s = m.group(20);
            if (s.length() != 0) {
                try {
                    xrtpv = xp.parse(s);
                } catch (Exception e) {
                    LOG.error("XRTP2[" + s + "] not parseable. callid=" + ev.getCall_id() + ". log line: \n" + log + "\n", e);
                }
                ev.setXrtp2(xrtpv);
            }

            s = m.group(21);
            ev.setTarget(s);

            s = m.group(22);
            UserDomain ud = URISplitter.splitURI(s);
            if (ud != null) {
                ev.setContactDomain(ud.getDomain());
            }

            ev.setValid();
        } else {
            LOG.error("Unable to parse: \n" + log + " with \n" + pattern);
        }

        return ev;
    }

    static long guessDate(String datestring) {
        final Date d;
        synchronized (DATEFORMAT) {
            try {
                d = DATEFORMAT.parse(formatDateStringToISO(datestring));
            } catch (ParseException e) {
                LOG.error("Unable to parse date:" + datestring);
                return 0;
            }
        }

        // we have to guess the year! yuck!!
        Calendar c = Calendar.getInstance();
        // get current month and year
        int m = c.get(MONTH);
        int y = c.get(YEAR);
        c.setTime(d);
        // we assume that the logfile is at most one year old!
        // Therefore, if the month of the log entry is bigger than
        // the current month, it is from last year!
        if (c.get(MONTH) > m) {
            c.set(YEAR, y - 1);
        } else {
            c.set(YEAR, y);
        }

        long ts = c.getTimeInMillis();

        if (ts > System.currentTimeMillis() + 10 * 60 * 1000) {
            LOG.error("Guessed date (" + new Date(ts) + ") is more than 10min into the future. Original was: '" + datestring + "'");
        }

        return ts;
    }

    /**
     * This method takes a string like "2008-11-14T11:29:20+01:00" and returns
     * "2008-11-14T11:29:20+0100". Meaning it removes the ":" character from
     * timezone offset, because Java is not ISO compliant !
     * 
     * @param input
     * @return
     */
    private static String formatDateStringToISO(String input) {
        return input.substring(0, 22).concat(input.substring(22 + 1));
    }

}
