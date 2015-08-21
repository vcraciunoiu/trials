package de.schlund.rtstat.util;

import java.text.SimpleDateFormat;

/**
 * String-Constants used by rtstat
 * 
 * @author <a href="mailto:rapude@schlund.de">Ralf Rapude</a> Date: 09/08/2006
 *         Time: 12:59:16 PM
 */

public interface Constants {

  public static final String LOGHOME = System.getProperty("user.home") + "/rtstatlog";
  public static final SimpleDateFormat SDF = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
  public static final String PROVIDER_TELEFONICA = "telefonica";
  public static final String PROVIDER_QSC = "qsc";
  public static final String PROVIDER_QSC_CAPITALS = "QSC"; // "QSC" is used in
  // field "fp" in
  // OpenSER logs
  public static final String PROVIDER_VODAFONE = "vodafone";
  public static final String PROVIDER_VERIZON = "verizon";
  public static final String PROVIDER_COLT = "colt";
  public static final String TARGET_PSTN = "pstn";
  public static final String[] PROVIDER_LIST = { PROVIDER_TELEFONICA, PROVIDER_QSC, PROVIDER_VODAFONE, PROVIDER_VERIZON, PROVIDER_COLT };
  public static final String EMERGENCYPREFIX_QSC_1 = "49110";
  public static final String EMERGENCYPREFIX_QSC_2 = "49112";
  public static final String EMERGENCYPREFIX_TELEFONICA = "49199111";
  public static final String TAL_RSP = "tal-rsp-";
  public static final String CDR_TABLE = "cdr";
  public static final String USER_AGENT_AVM = "FRITZ!Box";

  public static final String PACKETS_LOST = "pl";
  public static final String RECEIVED_PACKETS_LOST = "rpl";
  public static final String DURATION_MS = "dms";
  public static final String CALLS_COUNT = "cc";
  public static final String JITTER = "ji";
  public static final String JITTER_CALLS_COUNT = "jicc";
  public static final String PACKETS_RECEIVED = "pr";
  public static final String MIN_PER_MIN = "mpm";
  public static final String MIN_PER_MIN_INC = "mpmi";

  public static final String ASR = "asr";
  public static final String ASR_CALLS = "asr_calls";
  public static final String ASR_1XX = "1xx";
  public static final String ASR_3XX = "3xx";
  public static final String ASR_4XX = "4xx";
  public static final String ASR_408 = "408";
  public static final String ASR_480 = "480";
  public static final String ASR_486 = "486";
  public static final String ASR_487 = "487";
  public static final String ASR_5XX = "5xx";
  public static final String ASR_6XX = "6xx";
  public static final String ASR_603 = "603";
  public static final String ASR_7XX = "7xx";

  public static final String ASR_EC = "ec";
  public static final String ASR_EC_486 = "ec_486";
  public static final String ASR_EC_408 = "ec_408";
  public static final String ASR_EC_480 = "ec_480";
  public static final String ASR_EC_487 = "ec_487";
  public static final String ASR_EC_603 = "ec_603";
  public static final String ASR_EC_CALLS = "ec_calls";

  public static final String CALLS_ONCE_ROUTED = "routed1";
  public static final String CALLS_WITHOUT_BYE = "wob";
  public static final String CALLS_WITHOUT_ACK = "woa";
  public static final String CALLS_NEW_SUCCESFULL = "newok";
  public static final String CALLS_ACK_DISCARDED = "ad";
  public static final String CALLS_BYE_DISCARDED = "bd";

  public static final String OTHERS = "others";
  public static final String CALLS_VOICEBOX = "vb";
  public static final String CALLS_ONNET = "onet";
  public static final String CALLS_ANNOUNCEMENTS = "ann";
  public static final String CALLS_ABROAD_GOOD = "abg";
  public static final String CALLS_ABROAD = "ab";
  public static final String CALLS_MOBILE = "mo";
  public static final String CALLS_VOICEBOX_GOOD = "gvb";
  public static final String CALLS_ANNOUNCEMENTS_GOOD = "ag";
  public static final String CALLS_ONNET_GOOD = "gon";
  public static final String CALLS_MOBILE_GOOD = "mg";

  public static final String OTHERS_PREFIX = "33";

}
