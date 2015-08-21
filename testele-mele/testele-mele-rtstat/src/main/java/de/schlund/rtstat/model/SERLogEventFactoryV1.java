package de.schlund.rtstat.model;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.schlund.rtstat.processor.ser.ProviderUtil;

/**
 * This class parses an "intermediary" OpenSER log format, 4 new fields:
 * timestamp, reason, from_tag, to_tag.
 * 
 * @author user
 * 
 */
public class SERLogEventFactoryV1 implements SERLogEventFactory {

    private static final Logger LOG = Logger
	    .getLogger(SERLogEventFactory.class);

    private static final String DATE_PATTERN = "(\\w*\\s*\\d*\\s*\\d{2}:\\d{2}:\\d{2})";
    private static final String TIMESTAMP_PATTERN = "timestamp=(.*)";
    private static final String METHOD_PATTERN = ", method=(.*)";
    private static final String FROM_TAG_PATTERN = ", from_tag=(.*)";
    private static final String TO_TAG_PATTERN = ", to_tag=(.*)";
    private static final String CALLID_PATTERN = ", call_id=(.*)";
    private static final String CODE_PATTERN = ", code=(.*)";
    private static final String REASON_PATTERN = ", reason=(.*)";
    private static final String IURI_PATTERN = ", i-uri=(.*)";
    private static final String OURI_PATTERN = ", o-uri=(.*)";
    private static final String FROM_PATTERN = ", from=(.*)";
    private static final String TO_PATTERN = ", to=(.*)";
    private static final String CSEQ_PATTERN = ", cseq=(.*)";
    private static final String UID_PATTERN = ", uid=(.*)";
    private static final String IDENTITY_PATTERN = ", identity=(.*)";
    private static final String LAST_DST_PATTERN = ", last-dst=(.*)";
    private static final String RINGING_PATTERN = ", ringing=(.*)";
    private static final String XRTP_PATTERN = ", xrtp=(.*)";
    private static final String UA_PATTERN = ", ua=(.*)";
    private static final String TARGET_PATTERN = ", target=(.*)";
    private static final String CONTACT_DOMAIN_PATTERN = ", ct=(.*)";

    private static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
	    "MMM d HH:mm:ss", Locale.US);

    public static final String TAL_RSP = "tal-rsp-";

    private static XRTPParser xp = new XRTPParser();

    public SERLogEventFactoryV1() {
//	ProviderUtil.setNA("");
    }

    public SERLogEvent parse(CharSequence cs) {
	SERLogEvent ev = new SERLogEvent();
	String log = cs.toString();

	// line in syslog format?
	if (log.startsWith("<")) {
	    // line in syslog format
	    int index = log.indexOf('>');
	    log = log.substring(index + 1);
	}

	String lastDestPattern = "";
	String ringingPattern = "";

	if (log.indexOf("last-dst") > 0) {
	    lastDestPattern = LAST_DST_PATTERN;
	}

	if (log.indexOf("ringing") > 0) {
	    ringingPattern = RINGING_PATTERN;
	}

        final String pattern = DATE_PATTERN + ".*" + TIMESTAMP_PATTERN
                                + METHOD_PATTERN + FROM_TAG_PATTERN + TO_TAG_PATTERN
                                + CALLID_PATTERN + CODE_PATTERN + REASON_PATTERN + IURI_PATTERN
                                + OURI_PATTERN + FROM_PATTERN + TO_PATTERN + CSEQ_PATTERN
                                + UID_PATTERN + IDENTITY_PATTERN + lastDestPattern
                                + ringingPattern + XRTP_PATTERN + UA_PATTERN + TARGET_PATTERN
                                + CONTACT_DOMAIN_PATTERN;
	Pattern p = Pattern.compile(pattern);

	Matcher m = p.matcher(log);

	if (m.lookingAt()) {
	    String s = m.group(1);
	    ev.setTimestamp(guessDate(s));

	    s = m.group(2);
	    // timestamp field is not used

	    s = m.group(3);
	    ev.setMethod(Method.valueOf(s));

	    s = m.group(4);
	    ev.setFromTag(s);

	    s = m.group(5);
	    ev.setToTag(s);

	    s = m.group(6);
	    ev.setCallId(s);

	    s = m.group(7);
	    ev.setCode(Short.parseShort(s));

	    s = m.group(8);
	    // reason - unused

	    s = m.group(9);
	    ev.setIURI(URISplitter.splitURI(s));

	    s = m.group(10);
	    ev.setOURI(URISplitter.splitURI(s));

	    s = m.group(11);
	    ev.setFrom(URISplitter.splitURI(s));

	    s = m.group(12);
	    ev.setTo(URISplitter.splitURI(s));

	    s = m.group(13);
	    // cseq - unused

	    s = m.group(14);
//	    ev.setUid(s);

	    s = m.group(15);
	    ev.setIdentity(s);

	    int c = 16;
	    if (lastDestPattern.length() > 0) {
		s = m.group(c);
		ev.setLastDestination(s);
		c++;
	    }

	    if (ringingPattern.length() > 0) {
		s = m.group(c);
//		ev.setRinging(s);
		c++;
	    }

	    s = m.group(c++);
	    if (s.length() != 0) {
		XRTPValue xrtpv = null;
		try {
		    xrtpv = xp.parse(s);
		} catch (Exception e) {
		    LOG.error("XRTP[" + s + "] not parseable. callid="
			    + ev.getCall_id() + ". log line: \n" + log + "\n",
			    e);
		}
		ev.setXrtp1(xrtpv);
//		ev.setXrtp(xrtpv);
	    }

	    s = m.group(c++);
	    if ("".equals(s)) {
		ev.setUserAgent(null);
	    } else {
		ev.setUserAgent(s);
	    }

	    s = m.group(c++);
	    ev.setTarget(s);
	    
	    s = m.group(c++);
	    UserDomain ud = URISplitter.splitURI(s);
	    if (ud != null) {
	        ev.setContactDomain(ud.getDomain());
	    }

	    ev.setValid();
	} else {
	    LOG.error("Unable to parse: \n" + log + " with \n" + pattern);
	}

	cs = null;
	log = null;

	return ev;
    }

    static long guessDate(String datestring) {
	final Date d;
	synchronized (DATEFORMAT) {
	    try {
		d = DATEFORMAT.parse(datestring);
	    } catch (ParseException e) {
		// TODO why runtime exception?
		throw new RuntimeException("Unable to parse date:" + datestring);
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
	    LOG.error("Guessed date (" + new Date(ts)
		    + ") is more than 10min into the future. Original was: '"
		    + datestring + "'");
	}

	return ts;
    }

}
