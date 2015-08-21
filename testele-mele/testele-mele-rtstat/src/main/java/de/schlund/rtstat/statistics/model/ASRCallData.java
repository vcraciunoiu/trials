package de.schlund.rtstat.statistics.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import de.schlund.rtstat.util.Constants;
import de.schlund.rtstat.util.Util;

/**
 * <p>
 * counter for a number of SIP status codes, currently for codes 200, 408, 486,
 * 487, overall number of calls and timestamp of the oldest call.
 * </p>
 * 
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 * @version 1.0
 */
public class ASRCallData {

  private static final Logger LOG = Logger.getLogger(ASRCallData.class);

  private final static SimpleDateFormat SDF = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

  //
  // 1xx
  //
  private static final short SIP_CODE_100_TRYING = 100;
  private static final short SIP_CODE_180_RINGING = 180;
  private static final short SIP_CODE_181_CALL_BEING_FORWARDED = 181;
  private static final short SIP_CODE_182_QUEUED = 182;
  private static final short SIP_CODE_183_SESSION_PROGRESS = 183;

  //
  // 200
  //
  private static final short SIP_CODE_200_OK = 200;

  //
  // 3xx
  //
  private static final short SIP_CODE_300_MULTIPLE_CHOICES = 300;
  private static final short SIP_CODE_301_MOVED_PERMANENTLY = 301;
  private static final short SIP_CODE_302_MOVED_TEMPORARILY = 302;
  private static final short SIP_CODE_305_USE_PROXY = 305;
  private static final short SIP_CODE_380_ALTERNATIVE_SERVICE = 380;

  //
  // 4xx
  //
  private static final short SIP_CODE_400_BAD_REQUEST = 400;
  private static final short sIP_CODE_401_UNAUTHORIZED = 401;
  private static final short SIP_CODE_402_PAYMENT_REQUIRED = 402;
  private static final short SIP_CODE_403_FORBIDDEN = 403;
  private static final short SIP_CODE_404_NOT_FOUND = 404;
  private static final short SIP_CODE_405_METHOD_NOT_ALLOWED = 405;
  private static final short SIP_CODE_406_NOT_ACCEPTABLE = 406;
  private static final short SIP_CODE_407_PROXY_AUTHENTICATION_REQUIRED = 407;
  private static final short SIP_CODE_408_REQUEST_TIMEOUT = 408;
  private static final short SIP_CODE_410_GONE = 410;
  private static final short SIP_CODE_413_REQUEST_ENTITY_TOO_LARGE = 413;
  private static final short SIP_CODE_414_REQUEST_URI_TOO_LONG = 414;
  private static final short SIP_CODE_415_UNSUPPORTED_MEDIA_TYPE = 415;
  private static final short SIP_CODE_416_UNSUPPORTED_URI_SCHEME = 416;
  private static final short SIP_CODE_420_BAD_EXTENSION = 420;
  private static final short SIP_CODE_421_EXTENSION_REQUIRED = 421;
  private static final short SIP_CODE_423_INTERVAL_TOO_BRIEF = 423;
  private static final short SIP_CODE_480_TEMORARILY_UNAVAILABLE = 480;
  private static final short SIP_CODE_481_CALL_TRANSACTION_DOES_NOT_EXIST = 481;
  private static final short SIP_CODE_482_LOOP_DETECTED = 482;
  private static final short SIP_CODE_483_TOO_MANY_HOPS = 483;
  private static final short SIP_CODE_484_ADDRESS_INCOMPLETE = 484;
  private static final short SIP_CODE_485_AMBIGUOUS = 485;
  private static final short SIP_CODE_486_BUSY_HERE = 486;
  private static final short SIP_CODE_487_REQUEST_TERMINATED = 487;
  private static final short SIP_CODE_488_NOT_ACCEPTABLE_HERE = 488;
  private static final short SIP_CODE_491_REQUEST_PENDING = 491;
  private static final short SIP_CODE_493_UNDECIPHERABLE = 493;

  //
  // 5xx
  //
  private static final short SIP_CODE_500_SERVER_INTERNAL_ERROR = 500;
  private static final short SIP_CODE_501_NOT_IMPLEMENTED = 501;
  private static final short SIP_CODE_502_BAD_GATEWAY = 502;
  private static final short SIP_CODE_503_SEVICE_UNAVAILABLE = 503;
  private static final short SIP_CODE_504_SERVER_TIMEOUT = 504;
  private static final short SIP_CODE_505_VERSION_NOT_SUPPORTED = 505;
  private static final short SIP_CODE_513_MESSAGE_TOO_LARGE = 513;

  //
  // 6xx
  //
  private static final short SIP_CODE_600_BUSY_EVERYWHERE = 600;
  private static final short SIP_CODE_603_DECLINE = 603;
  private static final short SIP_CODE_604_DOES_NOT_EXIST_ANYWHERE = 604;
  private static final short SIP_CODE_606_NOT_ACCEPTABLE = 606;

  //
  // 7xx Mic Special
  //
  private static final short SIP_CODE_700_CALL_TOO_SHORT = 700;
  private static final short SIP_CODE_701_CALL_OK_BUT_NOT_ACK = 701;

  public static final short[] SIP_CODES = {
      // 1xx
      SIP_CODE_100_TRYING,
      SIP_CODE_180_RINGING,
      SIP_CODE_181_CALL_BEING_FORWARDED,
      SIP_CODE_182_QUEUED,
      SIP_CODE_183_SESSION_PROGRESS,
      // 200
      SIP_CODE_200_OK,
      // 3xx
      SIP_CODE_300_MULTIPLE_CHOICES, SIP_CODE_301_MOVED_PERMANENTLY, SIP_CODE_302_MOVED_TEMPORARILY,
      SIP_CODE_305_USE_PROXY,
      SIP_CODE_380_ALTERNATIVE_SERVICE,
      // 4xx
      SIP_CODE_400_BAD_REQUEST, sIP_CODE_401_UNAUTHORIZED, SIP_CODE_402_PAYMENT_REQUIRED, SIP_CODE_403_FORBIDDEN, SIP_CODE_404_NOT_FOUND, SIP_CODE_405_METHOD_NOT_ALLOWED, SIP_CODE_406_NOT_ACCEPTABLE, SIP_CODE_407_PROXY_AUTHENTICATION_REQUIRED, SIP_CODE_408_REQUEST_TIMEOUT, SIP_CODE_410_GONE,
      SIP_CODE_413_REQUEST_ENTITY_TOO_LARGE, SIP_CODE_414_REQUEST_URI_TOO_LONG, SIP_CODE_415_UNSUPPORTED_MEDIA_TYPE, SIP_CODE_416_UNSUPPORTED_URI_SCHEME, SIP_CODE_420_BAD_EXTENSION, SIP_CODE_421_EXTENSION_REQUIRED, SIP_CODE_423_INTERVAL_TOO_BRIEF, SIP_CODE_480_TEMORARILY_UNAVAILABLE,
      SIP_CODE_481_CALL_TRANSACTION_DOES_NOT_EXIST, SIP_CODE_482_LOOP_DETECTED, SIP_CODE_483_TOO_MANY_HOPS, SIP_CODE_484_ADDRESS_INCOMPLETE, SIP_CODE_485_AMBIGUOUS, SIP_CODE_486_BUSY_HERE, SIP_CODE_487_REQUEST_TERMINATED, SIP_CODE_488_NOT_ACCEPTABLE_HERE, SIP_CODE_491_REQUEST_PENDING,
      SIP_CODE_493_UNDECIPHERABLE,
      // 5xx
      SIP_CODE_500_SERVER_INTERNAL_ERROR, SIP_CODE_501_NOT_IMPLEMENTED, SIP_CODE_502_BAD_GATEWAY, SIP_CODE_503_SEVICE_UNAVAILABLE, SIP_CODE_504_SERVER_TIMEOUT, SIP_CODE_505_VERSION_NOT_SUPPORTED, SIP_CODE_513_MESSAGE_TOO_LARGE,
      // 6xx
      SIP_CODE_600_BUSY_EVERYWHERE, SIP_CODE_603_DECLINE, SIP_CODE_604_DOES_NOT_EXIST_ANYWHERE, SIP_CODE_606_NOT_ACCEPTABLE,
      // 7xx
      SIP_CODE_700_CALL_TOO_SHORT, SIP_CODE_701_CALL_OK_BUT_NOT_ACK };

  private static final int NR_CODES = SIP_CODES.length;

  private static final short MAX_SIP_CODE = SIP_CODES[NR_CODES - 1];

  private static final int[] REVERSE_LOOKUP_TABLE = new int[MAX_SIP_CODE + 1];

  private static final int IDX_CODE_200_OK;

  private static final int IDX_CODE_408_REQUEST_TIMEOUT;

  private static final int IDX_CODE_480_TEMORARILY_UNAVAILABLE;

  private static final int IDX_CODE_486_BUSY_HERE;

  private static final int IDX_CODE_487_REQUEST_TERMINATED;

  private static final int IDX_CODE_603_DECLINE;

  private static final int findIndex(short code) {

    int pos = REVERSE_LOOKUP_TABLE[code];
    if (pos == -1) {
      throw new RuntimeException("No index was found for code " + code + ", have a look at the sources...");
    } else {
      return pos;
    }
  }

  static {
    for (int i = 0; i < REVERSE_LOOKUP_TABLE.length; i++) {
      REVERSE_LOOKUP_TABLE[i] = -1;
    }
    for (int i = 0; i < SIP_CODES.length; i++) {
      REVERSE_LOOKUP_TABLE[SIP_CODES[i]] = i;
    }

    // lookup indices to avoid errors after adding new codes and changing
    // indices...
    IDX_CODE_200_OK = findIndex(SIP_CODE_200_OK);
    IDX_CODE_408_REQUEST_TIMEOUT = findIndex(SIP_CODE_408_REQUEST_TIMEOUT);
    IDX_CODE_480_TEMORARILY_UNAVAILABLE = findIndex(SIP_CODE_480_TEMORARILY_UNAVAILABLE);
    IDX_CODE_486_BUSY_HERE = findIndex(SIP_CODE_486_BUSY_HERE);
    IDX_CODE_487_REQUEST_TERMINATED = findIndex(SIP_CODE_487_REQUEST_TERMINATED);
    IDX_CODE_603_DECLINE = findIndex(SIP_CODE_603_DECLINE);

    int last = 0;
    for (int i = 0; i < NR_CODES; i++) {
      if (SIP_CODES[i] < last) {
        throw new IllegalStateException("CODES should be in ascending order. CODES[" + i + "]=" + SIP_CODES[i] + " last=" + last);
      }
      last = SIP_CODES[i];
    }
  }

  private long[] codeCounter = new long[NR_CODES];

  private long allCalls;

  private long otherCalls;

  private long timestampOldest;

  private String id;

  private long callsOnceRouted;
  private long callsWithoutBye;
  private long callsWithoutAck;
  private long callsNewSuccesfull;
  private long callsAckDiscarded;
  private long callsByeDiscarded;

  /**
   * set good and all calls to 0 and timestampOldest to now
   */
  public ASRCallData(String id) {
    for (int i = 0; i < NR_CODES; i++) {
      codeCounter[i] = 0;
    }
    this.id = id;
    otherCalls = 0;
    allCalls = 0;
    callsOnceRouted = 0;
    callsWithoutBye = 0;
    callsWithoutAck = 0;
    callsNewSuccesfull = 0;
    callsAckDiscarded = 0;
    callsByeDiscarded = 0;
    timestampOldest = System.currentTimeMillis();
    assertConsistency();
  }

  /**
   * additional contructor just for unittesting
   */
  public ASRCallData(long c200, long c408, long c486, long c487, long other, long all, long timestamp) {
    codeCounter[IDX_CODE_200_OK] = c200;
    codeCounter[IDX_CODE_408_REQUEST_TIMEOUT] = c408;
    codeCounter[IDX_CODE_486_BUSY_HERE] = c486;
    codeCounter[IDX_CODE_487_REQUEST_TERMINATED] = c487;
    allCalls = all;
    otherCalls = other;
    timestampOldest = timestamp;
    assertConsistency();
  }

  public void insert(ASRData data) {
    LOG.debug(">>> " + id + " " + data.getCode());
    boolean found = false;
    for (int i = 0; i < NR_CODES; i++) {
      if (data.getCode() == SIP_CODES[i]) {
        found = true;
        codeCounter[i]++;
      }
    }
    if (!found) {
      LOG.warn("unknown code: " + data.getCode() + " for " + id);
      otherCalls++;
    }
    allCalls++;
    assertConsistency();
  }

  public void remove(ASRData data) {
    LOG.debug(">>> " + id + " " + data.getCode());
    boolean found = false, codeOk = true;

    for (int i = 0; i < NR_CODES; i++) {
      if (data.getCode() == SIP_CODES[i]) {
        found = true;
        if (codeCounter[i] == 0) {
          LOG.error("Called remove for code " + data.getCode() + " on " + id + " but counter was 0 [" + toString() + "]");
          codeOk = false;
        } else {
          codeCounter[i]--;
        }
      }
    }
    if (!found) {
      LOG.warn("unknown code: " + data.getCode() + " for " + id);
      if (otherCalls == 0) {
        LOG.error("Called remove on " + id + " for code " + data.getCode() + " but otherCalls == 0 [" + toString() + "]");
      } else {
        otherCalls--;
      }
    }
    if (codeOk) {
      if (allCalls == 0) {
        LOG.error("Called remove on " + id + " but allCalls == 0 [" + toString() + "]");
      } else {
        allCalls--;
      }
    }
    assertConsistency();
  }

  private void assertConsistency() {
    int sum = 0;
    for (int i = 0; i < NR_CODES; i++) {
      sum += codeCounter[i];
    }
    sum += otherCalls;
    if (sum != allCalls) {
      // Exception needed to get a stacktrace
      LOG.error("sum of codeCounter and otherCalls for " + id + " is != allCalls - " + toString());
    }
  }

  public long getAllCalls() {
    return allCalls;
  }

  public long getGoodCalls() {
    return codeCounter[IDX_CODE_200_OK];
  }

  public long getTimestampOldest() {
    return timestampOldest;
  }

  public String getTimestampOldestFormatted() {
    return SDF.format(new Date(getTimestampOldest()));
  }

  public void setTimestampOldest(long timestampOldest) {
    this.timestampOldest = timestampOldest;
  }

  public void add(ASRCallData asrcd) {
    for (int i = 0; i < NR_CODES; i++) {
      codeCounter[i] += asrcd.codeCounter[i];
    }
    allCalls += asrcd.allCalls;
    timestampOldest = (timestampOldest < asrcd.timestampOldest ? timestampOldest : asrcd.timestampOldest);
  }

  public long getRatio() {
    if (getAllCalls() == 0) {
      return 1000;
    } else {
      return 1000 * getGoodCalls() / getAllCalls();
    }
  }

  private long getErrorRation(int idx) {
    long ret = 0;
    if (getAllCalls() != 0) {
      ret = 1000 * codeCounter[idx] / getAllCalls();
    }
    LOG.info("ErrorRatio for " + idx + ": " + ret);
    if (LOG.isDebugEnabled()) {
      LOG.debug(this.toString());
    }
    return ret;
  }

  public long get408Ratio() {
    return getErrorRation(IDX_CODE_408_REQUEST_TIMEOUT, false);
  }

  public long get480Ratio() {
    return getErrorRation(IDX_CODE_480_TEMORARILY_UNAVAILABLE, false);
  }

  public long get486Ratio() {
    return getErrorRation(IDX_CODE_486_BUSY_HERE, false);
  }

  public long get487Ratio() {
    return getErrorRation(IDX_CODE_487_REQUEST_TERMINATED, false);
  }

  public long get603Ratio() {
    return getErrorRation(IDX_CODE_603_DECLINE, false);
  }

  public long getBlockRatio(int b) {
    if (b < 1 || b > 7) {
      throw new IllegalArgumentException("only blocks 1-7 allowed.");
    }

    if (getAllCalls() == 0) {
      return (b == 2 ? 1000 : 0);
    }

    long sum = 0;
    int firstCode = b * 100;
    int lastCode = firstCode + 100;
    for (int i = 0; i < NR_CODES; i++) {
      if (SIP_CODES[i] >= firstCode && SIP_CODES[i] < lastCode) {
        sum += codeCounter[i];
      }
    }
    return 1000 * sum / getAllCalls();
  }

  public long getCount(short sipcode) {
    int pos = REVERSE_LOOKUP_TABLE[sipcode];
    if (pos == -1) {
      throw new IllegalArgumentException("'" + sipcode + "' is no known sipcode");
    }
    return codeCounter[pos];
  }

  @Override
  public Object clone() {
    final ASRCallData clone = new ASRCallData(this.id);
    for (int i = 0; i < NR_CODES; i++) {
      clone.codeCounter[i] = this.codeCounter[i];
    }

    clone.allCalls = this.allCalls;
    clone.timestampOldest = this.timestampOldest;
    clone.callsOnceRouted = this.callsOnceRouted;
    clone.callsWithoutAck = this.callsWithoutAck;
    clone.callsWithoutBye = this.callsWithoutBye;
    clone.callsNewSuccesfull = this.callsNewSuccesfull;
    clone.callsAckDiscarded = this.callsAckDiscarded;
    clone.callsByeDiscarded = this.callsByeDiscarded;

    // after cloning, reset the "other" data
    this.callsOnceRouted = 0;
    this.callsWithoutAck = 0;
    this.callsWithoutBye = 0;
    this.callsNewSuccesfull = 0;
    this.callsAckDiscarded = 0;
    this.callsByeDiscarded = 0;

    return clone;
  }

  @Override
  public int hashCode() {
    int hash = new Long(allCalls).hashCode() ^ new Long(timestampOldest).hashCode();
    for (int i = 0; i < NR_CODES; i++) {
      hash ^= this.codeCounter[i];
    }

    return hash;
  }

  /**
   * @param data
   * @return true if all the counters are equal, but ignores the timestamp
   */
  public boolean equalsNoTimestamp(ASRCallData data) {
    if (data.allCalls == this.allCalls) {
      for (int i = 0; i < NR_CODES; i++) {
        if (this.codeCounter[i] != data.codeCounter[i]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    if (o instanceof ASRCallData) {
      ASRCallData acd = (ASRCallData) o;
      if (acd.timestampOldest == this.timestampOldest) {
        return equalsNoTimestamp(acd);
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("AllCalls=" + allCalls + ", KnownCodesCalls=" + codeCounter.length + ", ");
    sb.append("UnkownCodesCalls=" + otherCalls + ", ");
    sb.append("Oldest=" + Util.readableTimestamp(timestampOldest) + "\n");
    for (int i = 0; i < codeCounter.length; i++) {
      sb.append("CodeCounter[" + i + "]=" + codeCounter[i] + "/");
    }
    return sb.toString();
  }

  public long getRatio(boolean flag) {
    return 1000 * getGoodCalls();
  }

  public long getBlockRatio(int b, boolean flag) {
    if (b < 1 || b > 7) {
      throw new IllegalArgumentException("only blocks 1-7 allowed.");
    }

    if (getAllCalls() == 0) {
      return (b == 2 ? 1000 : 0);
    }

    long sum = 0;
    int firstCode = b * 100;
    int lastCode = firstCode + 100;
    for (int i = 0; i < NR_CODES; i++) {
      if (SIP_CODES[i] >= firstCode && SIP_CODES[i] < lastCode) {
        sum += codeCounter[i];
      }
    }
    return 1000 * sum;
  }

  private long getErrorRation(int idx, boolean flag) {
    return 1000 * codeCounter[idx];
  }

  public void insertOtherData(String graphType) {
    if (graphType.equals(Constants.CALLS_WITHOUT_BYE)) {
      callsWithoutBye++;
    } else if (graphType.equals(Constants.CALLS_WITHOUT_ACK)) {
      callsWithoutAck++;
    } else if (graphType.equals(Constants.CALLS_ONCE_ROUTED)) {
      callsOnceRouted++;
    } else if (graphType.equals(Constants.CALLS_NEW_SUCCESFULL)) {
      callsNewSuccesfull++;
    } else if (graphType.equals(Constants.CALLS_ACK_DISCARDED)) {
      callsAckDiscarded++;
    } else if (graphType.equals(Constants.CALLS_BYE_DISCARDED)) {
      callsByeDiscarded++;
    }
  }

  public long getCallsWithoutAck() {
    return callsWithoutAck;
  }

  public long getCallsWithoutBye() {
    return callsWithoutBye;
  }

  public long getCallsOnceRouted() {
    return callsOnceRouted;
  }

  public double getCallsNewSuccesfull() {
    return getGoodCalls();
  }

  public long getCallsAckDiscarded() {
    return callsAckDiscarded;
  }

  public long getCallsByeDiscarded() {
    return callsByeDiscarded;
  }

}
