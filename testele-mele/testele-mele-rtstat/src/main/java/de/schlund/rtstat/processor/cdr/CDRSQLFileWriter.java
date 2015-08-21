package de.schlund.rtstat.processor.cdr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.schlund.rtstat.db.DbConnection;
import de.schlund.rtstat.model.XRTPValue;
import de.schlund.rtstat.model.cdr.CDR;
import de.schlund.rtstat.model.cdr.CDRState;
import de.schlund.rtstat.util.Constants;
import de.schlund.rtstat.util.ErrorReporter;

/**
 * @author user
 * 
 */
public class CDRSQLFileWriter {

	private Logger LOG = Logger.getLogger(CDRSQLFileWriter.class);
	private Logger LOGGER_INSERTS = Logger.getLogger("LOGGER_INSERTS");

	private ErrorReporter errorReporter;

	private static enum CdrTableColumn {
		CALLID("callid"), STARTTIME("startzeit"), ENDTIME("endzeit"), DURATION("dauer"), FROM_NUMBER("fromnummer"), FROM_DOMAIN("fromdomain"), TO_NUMBER("tonummer"), TO_DOMAIN("todomain"), FINAL_USER("final_nummer"), FINAL_DOMAIN("final_domain"), BILL_USER("bill_user"), RESULT("result"), BYE_RESULT(
				"bye_result"), CLIENT("mandant"), PROVIDER("provider"), FROM_PROVIDER("fromprovider"), UA("ua"), UA2("ua2"), XRTP_PS("xrtp_ps"), XRTP_ES("xrtp_es"), XRTP_OS("xrtp_os"), XRTP_SP("xrtp_sp"), XRTP_SO("xrtp_so"), XRTP_PR("xrtp_pr"), XRTP_ER("xrtp_er"), XRTP_OR("xrtp_or"), XRTP_CR(
				"xrtp_cr"), XRTP_SR("xrtp_sr"), XRTP_PLS("xrtp_pl_s"), XRTP_PLR("xrtp_pl_r"), XRTP_LS("xrtp_ls"), XRTP_BL("xrtp_bl"), XRTP_EN("xrtp_en"), XRTP_DE("xrtp_de"), XRTP_JIS("xrtp_ji_s"), XRTP_JIR("xrtp_ji_r"), XRTP_CS("xrtp_cs"), XRTP_RBDU("xrtp_rb_duration"), XRTP_RBDE("xrtp_rb_density"), XRTP_SBDU(
				"xrtp_sb_duration"), XRTP_SBDE("xrtp_sb_density"), XRTP_DLA("xrtp_dl_avg"), XRTP_DLM("xrtp_dl_max"), XRTP2_PS("XRTP2_ps"), XRTP2_ES("xrtp2_es"), XRTP2_OS("XRTP2_os"), XRTP2_SP("XRTP2_sp"), XRTP2_SO("XRTP2_so"), XRTP2_PR("XRTP2_pr"), XRTP2_ER("xrtp2_er"), XRTP2_OR("XRTP2_or"), XRTP2_CR(
				"XRTP2_cr"), XRTP2_SR("XRTP2_sr"), XRTP2_PLS("XRTP2_pl_s"), XRTP2_PLR("XRTP2_pl_r"), XRTP2_LS("xrtp2_ls"), XRTP2_BL("XRTP2_bl"), XRTP2_EN("XRTP2_en"), XRTP2_DE("XRTP2_de"), XRTP2_JIS("XRTP2_ji_s"), XRTP2_JIR("XRTP2_ji_r"), XRTP2_CS("XRTP2_cs"), XRTP2_RBDU("XRTP2_rb_duration"), XRTP2_RBDE(
				"XRTP2_rb_density"), XRTP2_SBDU("XRTP2_sb_duration"), XRTP2_SBDE("XRTP2_sb_density"), XRTP2_DLA("XRTP2_dl_avg"), XRTP2_DLM("XRTP2_dl_max"), TARGET("target"), FLAGS("flags"), CONTACT_DOMAIN("contact_domain");

		private String columnname;

		CdrTableColumn(String name) {
			columnname = name;
		}

		public String getColumnname() {
			return columnname;
		}
	}

	private static final SimpleDateFormat GMT_DATE_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
	// static {
	// GMT_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT"));
	// }

	private int fieldsSet = 0;

	private DbConnection dbConnection;
	protected Statement stmt;
	private int insertResult;
	private Map<String, Integer> uasMap = new HashMap<String, Integer>();
	private Map<String, Integer> providersMap = new HashMap<String, Integer>();
	private Map<String, Integer> mandantsMap = new HashMap<String, Integer>();
	private Map<String, Integer> fromprovidersMap = new HashMap<String, Integer>();

	private int bulkInsertLimit;
	private int bulkInsertCounter = 0;
	private StringBuilder bulkInsertString;

	private String patternString = "\\d{2}\\.\\d{2}\\.\\d{2}";
	private Pattern pattern = Pattern.compile(patternString);

	private HashMap<Integer, StringBuilder> bulkStrings = new HashMap<Integer, StringBuilder>();
	private HashMap<Integer, Integer> bulkCounters = new HashMap<Integer, Integer>();
	private int previousDay = 0;

	public void setBulkInsertLimit(int bulkInsertLimit) {
		this.bulkInsertLimit = bulkInsertLimit;
	}

	public void setErrorReporter(ErrorReporter errorReporter) {
		this.errorReporter = errorReporter;
	}

	/**
	 * This is called by Spring
	 * 
	 * @param dbConnection
	 * @throws SQLException
	 */
	public void setDbConnection(DbConnection dbConnection) throws SQLException {
		this.dbConnection = dbConnection;
		// also here set the statement
		stmt = dbConnection.getStatement();
		// also here get some necessary data, meaning the content of tables:
		// uas, providers, mandants
		populateUasMap();
		populateProvidersMap();
		populateMandantsMap();
		populateFromprovidersMap();
	}

	/**
	 * Put the content of the tables in maps for performance reasons, so we
	 * don't have to hit the db for every event.
	 * 
	 * @throws SQLException
	 */
	private void populateUasMap() throws SQLException {
		ResultSet rs = stmt.executeQuery("select id, ua from uas");
		while (rs.next()) {
			// I put the ua as key because it's more simple this way to search
			// in the map
			uasMap.put(rs.getString(2), rs.getInt(1));
		}
		rs.close();
	}

	private void populateProvidersMap() throws SQLException {
		ResultSet rs = stmt.executeQuery("select string, number from providers");
		while (rs.next()) {
			providersMap.put(rs.getString(1), rs.getInt(2));
		}
		rs.close();
	}

	private void populateMandantsMap() throws SQLException {
		ResultSet rs = stmt.executeQuery("select domain, mandant from map_mandants");
		while (rs.next()) {
			mandantsMap.put(rs.getString(1), rs.getInt(2));
		}
		rs.close();
	}

	private void populateFromprovidersMap() throws SQLException {
		ResultSet rs = stmt.executeQuery("select domain, provider from map_fromproviders");
		while (rs.next()) {
			fromprovidersMap.put(rs.getString(1), rs.getInt(2));
		}
		rs.close();
	}

	public void write(CDR cdr) {
		// the write to db is done when limit+1 CDR comes.

		try {
			int day = Integer.valueOf(getDayFromCdr(cdr));

			// here we clean entries older than 2 days, otherwise this map will
			// grow indefinetly.
			if (day != previousDay) {
				bulkStrings.remove(day - 2);
				bulkCounters.remove(day - 2);
			}

			if (!bulkStrings.containsKey(day)) {
				bulkStrings.put(day, null);
				bulkCounters.put(day, 0);
			}

			String tableToWriteIn = "cdr_" + day;
			bulkInsertString = bulkStrings.get(day);
			bulkInsertCounter = bulkCounters.get(day);

			if (bulkInsertCounter == 0) {
				bulkInsertString = new StringBuilder();
				initializeInsertString(bulkInsertString, tableToWriteIn);
				int ret = appendChunk(bulkInsertString, cdr);
				if (ret == 0) {
					appendChunkSeparator(bulkInsertString);
				}
				bulkStrings.put(day, bulkInsertString);
				bulkInsertCounter++;
				bulkCounters.put(day, bulkInsertCounter);
			} else if (bulkInsertCounter == bulkInsertLimit) {
				appendChunk(bulkInsertString, cdr);
				bulkStrings.put(day, bulkInsertString);
				writeToDb(bulkInsertString);
				bulkCounters.put(day, 0);
				bulkInsertString = null;
			} else {
				int ret = appendChunk(bulkInsertString, cdr);
				if (ret == 0) {
					appendChunkSeparator(bulkInsertString);
				}
				bulkStrings.put(day, bulkInsertString);
				bulkInsertCounter++;
				bulkCounters.put(day, bulkInsertCounter);
			}

			previousDay = day;
		} catch (Exception e) {
			LOG.error("Error when writing CDR: " + cdr, e);
		}
	}

	protected void initializeInsertString(StringBuilder insertString, String normalOrEmergency) {
		insertString.append("INSERT INTO " + normalOrEmergency + " (").append(CdrTableColumn.CALLID.getColumnname()).append(", ").append(CdrTableColumn.STARTTIME.getColumnname()).append(", ").append(CdrTableColumn.ENDTIME.getColumnname()).append(", ").append(CdrTableColumn.DURATION.getColumnname())
				.append(", ").append(CdrTableColumn.FROM_NUMBER.getColumnname()).append(", ").append(CdrTableColumn.FROM_DOMAIN.getColumnname()).append(", ").append(CdrTableColumn.TO_NUMBER.getColumnname()).append(", ").append(CdrTableColumn.TO_DOMAIN.getColumnname()).append(", ").append(
						CdrTableColumn.FINAL_USER.getColumnname()).append(", ").append(CdrTableColumn.FINAL_DOMAIN.getColumnname()).append(", ").append(CdrTableColumn.BILL_USER.getColumnname()).append(", ").append(CdrTableColumn.RESULT.getColumnname()).append(", ").append(
						CdrTableColumn.BYE_RESULT.getColumnname()).append(", ").append(CdrTableColumn.PROVIDER.getColumnname()).append(", ").append(CdrTableColumn.CLIENT.getColumnname()).append(", ").append(CdrTableColumn.FROM_PROVIDER.getColumnname()).append(", ").append(
						CdrTableColumn.UA.getColumnname()).append(", ").append(CdrTableColumn.UA2.getColumnname()).append(", ").append(CdrTableColumn.XRTP_PS.getColumnname()).append(", ").append(CdrTableColumn.XRTP_ES.getColumnname()).append(", ").append(CdrTableColumn.XRTP_OS.getColumnname())
				.append(", ").append(CdrTableColumn.XRTP_SP.getColumnname()).append(", ").append(CdrTableColumn.XRTP_SO.getColumnname()).append(", ").append(CdrTableColumn.XRTP_PR.getColumnname()).append(", ").append(CdrTableColumn.XRTP_ER.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP_OR.getColumnname()).append(", ").append(CdrTableColumn.XRTP_CR.getColumnname()).append(", ").append(CdrTableColumn.XRTP_SR.getColumnname()).append(", ").append(CdrTableColumn.XRTP_PLS.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP_PLR.getColumnname()).append(", ").append(CdrTableColumn.XRTP_LS.getColumnname()).append(", ").append(CdrTableColumn.XRTP_BL.getColumnname()).append(", ").append(CdrTableColumn.XRTP_EN.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP_DE.getColumnname()).append(", ").append(CdrTableColumn.XRTP_JIS.getColumnname()).append(", ").append(CdrTableColumn.XRTP_JIR.getColumnname()).append(", ").append(CdrTableColumn.XRTP_CS.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP_RBDU.getColumnname()).append(", ").append(CdrTableColumn.XRTP_RBDE.getColumnname()).append(", ").append(CdrTableColumn.XRTP_SBDU.getColumnname()).append(", ").append(CdrTableColumn.XRTP_SBDE.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP_DLA.getColumnname()).append(", ").append(CdrTableColumn.XRTP_DLM.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_PS.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_ES.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP2_OS.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_SP.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_SO.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_PR.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP2_ER.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_OR.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_CR.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_SR.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP2_PLS.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_PLR.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_LS.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_BL.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP2_EN.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_DE.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_JIS.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_JIR.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP2_CS.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_RBDU.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_RBDE.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_SBDU.getColumnname()).append(", ").append(
						CdrTableColumn.XRTP2_SBDE.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_DLA.getColumnname()).append(", ").append(CdrTableColumn.XRTP2_DLM.getColumnname()).append(", ").append(CdrTableColumn.TARGET.getColumnname()).append(", ").append(
						CdrTableColumn.FLAGS.getColumnname()).append(", ").append(CdrTableColumn.CONTACT_DOMAIN.getColumnname()).append(") VALUES ");
	}

	protected int appendChunk(StringBuilder insertString, CDR cdr) {
		int ret = 0;
		StringBuilder localInsertString = new StringBuilder("");
		try {
			fieldsSet = 0;
			localInsertString.append("(");

			// call id
			String callid = cdr.getCDRCall_id();
			if (callid.length() > 128) {
				callid = callid.substring(0, 127).concat("*");
			}
			appendValue(localInsertString, callid);

			// startzeit & endzeit & duration
			appendValue(localInsertString, GMT_DATE_FORMATTER.format(new Date(cdr.getStarttime())));
			if (cdr.getState() == CDR.STATE.BYE) {
				appendValue(localInsertString, GMT_DATE_FORMATTER.format(new Date(cdr.getEndtime())));
				appendValue(localInsertString, "" + (cdr.getDuration() / 1000));
			} else {
				if (isCallNotEstablished(cdr.getCode())) {
					appendValue(localInsertString, GMT_DATE_FORMATTER.format(new Date(cdr.getStarttime())));
					appendValue(localInsertString, "0");
				} else {
					appendValue(localInsertString, GMT_DATE_FORMATTER.format(new Date(cdr.getStarttime() + CDRState.getExpireAfter())));
					appendValue(localInsertString, "" + (CDRState.getExpireAfter() / 1000));
				}
			}

			// fromnummer
			String fromNumber = cdr.getFromNumber();
			if (fromNumber.length() > 40) {
				fromNumber = fromNumber.substring(0, 39).concat("*");
			}
			appendValue(localInsertString, fromNumber);
			// fromdomain
			appendValue(localInsertString, cdr.getFromDomain());

			// tonummer & todomain
			String tonummer = cdr.getToNumber();
			if (tonummer != null) {
				if (tonummer.length() > 40) {
					tonummer = tonummer.substring(0, 39).concat("*");
				}
				appendValue(localInsertString, tonummer);
			} else {
				appendValue(localInsertString, null);
			}
			appendValue(localInsertString, cdr.getToDomain());

			// final number & final domain
			String finalNumber = cdr.getFinalNumber();
			if (finalNumber.length() > 32) {
				finalNumber = finalNumber.substring(0, 31).concat("*");
			}
			appendValue(localInsertString, finalNumber);
			appendValue(localInsertString, cdr.getFinalDomain());

			// bill user
			String billUser = cdr.getBillUser();
			if (billUser != null && billUser.length() > 32) {
				billUser = billUser.substring(0, 31).concat("*");
			}
			appendValue(localInsertString, billUser);

			// result
			appendValue(localInsertString, "" + cdr.getCode());

			// bye_result
			String bye_result = null;
			if (cdr.getBYE() != null) {
				bye_result = "" + cdr.getBYE().getCode();
			}
			appendValue(localInsertString, bye_result);

			// provider
			final Integer provider = providersMap.get(cdr.getB().getDomain());
			appendValue(localInsertString, provider == null ? null : provider.toString());

			// mandant
			final Integer mandant = mandantsMap.get(cdr.getFromDomain());
			appendValue(localInsertString, mandant == null ? null : mandant.toString());

			// fromprovider
			final Integer fromprovider = fromprovidersMap.get(cdr.getFirstINVITE().getFromProvider());
			appendValue(localInsertString, fromprovider == null ? null : fromprovider.toString());

			// fill "ua1" column
			Integer uaId1 = null;
			String ua1 = null;
			ua1 = cdr.getLastINVITE().getUa1();
			if (ua1 != null) {
				uaId1 = handleUa(ua1);
				appendValue(localInsertString, String.valueOf(uaId1));
			} else {
				appendValue(localInsertString, null);
			}

			// fill "ua2" column
			Integer uaId2 = null;
			String ua2 = null;
			ua2 = cdr.getLastINVITE().getUa2();
			if (ua2 != null) {
				uaId2 = handleUa(ua2);
				appendValue(localInsertString, String.valueOf(uaId2));
			} else {
				appendValue(localInsertString, null);
			}

			// xrtp values
			XRTPValue xrtp1 = null, xrtp2 = null;
			if (cdr.getBYE() != null) {
				xrtp1 = cdr.getBYE().getXrtp1();
				xrtp2 = cdr.getBYE().getXrtp2();

				if (xrtp1 == null) {
					xrtp1 = new XRTPValue();
				}

				if (xrtp2 == null) {
					xrtp2 = new XRTPValue();
				}
			} else {
				xrtp1 = new XRTPValue();
				xrtp2 = new XRTPValue();
			}

			// If the caller sends the BYE, x1 holds the XRTP values from the
			// caller, x2 the XRTP values from the callee.
			// If the callee sends the BYE, x1 holds the XRTP values from the
			// callee, x2 the XRTP values from the caller.
			// No matter who has sent the BYE, the XRTP values from the caller
			// should be stored in the xrtp_* columns
			// and the values from the callee in the xrtp2_* columns.
			if (cdr.getByeSentByCaller() == true) {
				appendXRTPValue(localInsertString, xrtp1);
				appendXRTPValue(localInsertString, xrtp2);
			} else {
				appendXRTPValue(localInsertString, xrtp2);
				appendXRTPValue(localInsertString, xrtp1);
			}

			// "target" field
			String target = cdr.getTarget();
			if (target.length() > 300) {
				target = target.substring(0, 299).concat("*");
			}
			appendValue(localInsertString, target);

			// "flags" field;
			appendValue(localInsertString, cdr.getFlagsBinaryValue().toString());

			// contact domain field, for lawful interception
			appendValue(localInsertString, cdr.getInviteOrAck().getContactDomain(), true);

			// just a sanity check
			if (fieldsSet != CdrTableColumn.values().length) {
				LOG.error("fields set: " + fieldsSet + " should be " + CdrTableColumn.values().length);
			}

			if (LOG.isDebugEnabled())
				LOG.debug("\n" + "\n" + "callid " + cdr.getCDRCall_id() + "\n" + "tonummer " + cdr.getToNumber() + "\n" + "todomain " + cdr.getToDomain() + "\n" + "fromnummer " + cdr.getFromNumber() + "\n" + "fromdomain " + cdr.getFromDomain() + "\n" + "startzeit "
						+ GMT_DATE_FORMATTER.format(new Date(cdr.getStarttime())) + "\n" + "endzeit " + GMT_DATE_FORMATTER.format(new Date(cdr.getEndtime())) + "\n" + "dauer " + cdr.getDuration() / 1000 + "\n" + "final_nummer " + cdr.getToNumber() + "\n" + "final_domain " + cdr.getToDomain() + "\n"
						+ "target " + cdr.getTarget() + "\n" + "bill_user " + cdr.getBillUser() + "\n" + "result " + cdr.getCode() + "\n" + "bye_result " + bye_result + "\n" + "mandant " + mandant + "\n" + "provider " + cdr.getB().getDomain() + "\n" + "fromprovider " + "ip" + "\n" + "ua " + ua1
						+ "\n" + "ua2 " + ua2 + "\n" + "xrtp_ps " + xrtp1.getPS_audio_packets_sent() + "\n" + "xrtp_os " + xrtp1.getOS_audio_octets_sent() + "\n" + "xrtp_sp " + xrtp1.getSP_silence_packets_sent() + "\n" + "xrtp_so " + xrtp1.getSO_silence_octets_sent() + "\n" + "xrtp_pr "
						+ xrtp1.getPR_audio_packets_received() + "\n" + "xrtp_or " + xrtp1.getOR_audio_octets_received() + "\n" + "xrtp_cr " + xrtp1.getCR_comfort_noise_packets_received() + "\n" + "xrtp_sr " + xrtp1.getSR_comfort_noise_octets_received() + "\n" + "xrtp_pl "
						+ xrtp1.getPL_receive_packets_lost_s() + "\n" + "xrtp_bl " + xrtp1.getBL_receive_maximum_burst_packets_lost() + "\n" + "xrtp_en " + xrtp1.getEN_encoder() + "\n" + "xrtp_de " + xrtp1.getDE_decoder() + "\n" + "xrtp_ji " + xrtp1.getJI_jitter_s() + "\n" + "xrtp_cs "
						+ xrtp1.getCS_setup_time() + "\n" + "xrtp_rb " + xrtp1.getRB_receive_burst_duration() + "\n" + "xrtp_sb " + xrtp1.getSB_send_burst_duration() + "\n" + "xrtp_dl " + xrtp1.getDL_delay_avg() + "\n" + "flags " + "*" + "\n");

			insertString.append(localInsertString);

		} catch (Exception e) {
			ret = 1;
			LOG.error("Error appending chunk. localInsertString: " + localInsertString, e);
		} finally {
			return ret;
		}

	}

	private void appendXRTPValue(StringBuilder localInsertString, XRTPValue xrtp) {
		appendValue(localInsertString, xrtp.getPS_audio_packets_sent());
		appendValue(localInsertString, xrtp.getEsExpectedPacketsSend());
		appendValue(localInsertString, xrtp.getOS_audio_octets_sent());
		appendValue(localInsertString, xrtp.getSP_silence_packets_sent());
		appendValue(localInsertString, xrtp.getSO_silence_octets_sent());
		appendValue(localInsertString, xrtp.getPR_audio_packets_received());
		appendValue(localInsertString, xrtp.getErExpectedPacketsReceived());
		appendValue(localInsertString, xrtp.getOR_audio_octets_received());
		appendValue(localInsertString, xrtp.getCR_comfort_noise_packets_received());
		appendValue(localInsertString, xrtp.getSR_comfort_noise_octets_received());
		appendValue(localInsertString, xrtp.getPL_receive_packets_lost_s());
		appendValue(localInsertString, xrtp.getPL_receive_packets_lost_r());
		appendValue(localInsertString, xrtp.getLsSequenceLoss());
		appendValue(localInsertString, xrtp.getBL_receive_maximum_burst_packets_lost());
		appendValue(localInsertString, xrtp.getEN_encoder());
		appendValue(localInsertString, xrtp.getDE_decoder());
		appendValue(localInsertString, xrtp.getJI_jitter_s());
		appendValue(localInsertString, xrtp.getJI_jitter_r());
		appendValue(localInsertString, xrtp.getCS_setup_time());
		appendValue(localInsertString, xrtp.getRB_receive_burst_duration());
		appendValue(localInsertString, xrtp.getRB_receive_burst_density());
		appendValue(localInsertString, xrtp.getSB_send_burst_duration());
		appendValue(localInsertString, xrtp.getSB_send_burst_density());
		appendValue(localInsertString, xrtp.getDL_delay_avg());
		appendValue(localInsertString, xrtp.getDL_delay_max());
	}

	protected void appendChunkSeparator(StringBuilder insertString) {
		insertString.append(",");
	}

	private void appendValue(StringBuilder insertString, Object value) {
		appendValue(insertString, value, false);
	}

	private void appendValue(StringBuilder insertString, Object value, boolean last) {
		fieldsSet++;
		if (value == null) {
			insertString.append(value);
		} else {
			insertString.append("\'").append(value).append("\'");
		}
		if (last) {
			insertString.append(")");
		} else {
			insertString.append(", ");
		}
	}

	/**
	 * Check if ua is already in database, in table uas (through the uasMap); if
	 * yes, returns ua's id which is put into insert string; if no, insert it
	 * into table uas, get the new id and put it into insert string.
	 * 
	 * @param ua
	 *            The user agent string got from SER log.
	 * @return int The id from the db.
	 */
	private int handleUa(String ua) {
		int uaId;
		if (uasMap.containsKey(ua)) {
			uaId = uasMap.get(ua);
		} else {
			// if ua is not in table/map then we have to insert it in table and
			// in map
			uaId = getUaIdNewInserted(ua);
		}
		return uaId;
	}

	/**
	 * Inserts a new user agent into table uas and into uasMap.
	 * 
	 * @param ua
	 * @return int
	 */
	private int getUaIdNewInserted(String ua) {
		int uaId = 0;
		ResultSet rs = null;
		try {
			rs = stmt.executeQuery("select max(id) from uas");
			if (rs.last()) {
				uaId = rs.getInt(1) + 1;
				String firmware = getFirmware(ua);
				if (ua.length() > 180) {
					ua = ua.substring(0, 179).concat("*");
				}
				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO uas (ua, firmware) VALUES (")
				// .append(uaId).append(", ")
						.append("'").append(ua).append("', ").append(firmware == null ? null : "'" + firmware + "'").append(")");
				try {
					insertResult = stmt.executeUpdate(query.toString());
				} catch (SQLException sqlex) {
					if (sqlex.getSQLState().equals("23000")) {
						rs = stmt.executeQuery("select id from uas where ua = '" + ua + "'");
						if (rs.last()) {
							uaId = rs.getInt(1);
						}
					} else {
						LOG.error("Error inserting into uas table: ", sqlex);
					}
				}
				uasMap.put(ua, uaId);
			} else {
				LOG.error("Error getting max id from uas table.");
			}
		} catch (Exception e) {
			LOG.error("Error inserting new UA: ua=" + ua, e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				LOG.error("Couldn't close result set.", e);
			}
		}
		return uaId;
	}

	private String getFirmware(String ua) {
		String firmware = null;
		if (ua.indexOf(Constants.USER_AGENT_AVM) != -1) {
			Matcher m = pattern.matcher(ua);
			if (m.find()) {
				firmware = m.group();
			}
		}
		return firmware;
	}

	/**
	 * Insert into database. Implements a retry mechanism.
	 */
	protected void writeToDb(StringBuilder insertString) {
		int retryCount = 3;
		boolean transactionCompleted = false;

		PreparedStatement preparedStatement = null;

		do {
			try {
				preparedStatement = dbConnection.getConnection().prepareStatement(insertString.toString());
				insertResult = preparedStatement.executeUpdate();
				transactionCompleted = true;
			} catch (SQLException sqlEx) {
				String sqlState = sqlEx.getSQLState();
				LOG.error("Error inserting to db. \n" + "insertString=\n" + insertString + "\n" + "sqlState=" + sqlState + ", cause=", sqlEx);
				if ("40001".equals(sqlState)) {
					retryCount--;
					LOG.error("Retrying...");
				} else {
					transactionCompleted = true;
					retryCount = 0;
				}
			} finally {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					LOG.error("Couldn't close the PreparedStatement.");
				}
			}
		} while (!transactionCompleted && (retryCount > 0));

		if (transactionCompleted == false) {
			LOGGER_INSERTS.error(insertString + ";");
			LOG.error("Couldn't write to database, tried 3 times. I give up. Logging INSERT statement to inserts.log");
			errorReporter.sendException(new Exception("Couldn't write to database, tried 3 times. I give up."));
		}
	}

	private boolean isCallNotEstablished(short code) {
		boolean ret = false;
		String temp = String.valueOf(code);
		if (temp.startsWith("3") || temp.startsWith("4") || temp.startsWith("5") || temp.startsWith("6")) {
			ret = true;
		}
		return ret;
	}

	private String getDayFromCdr(CDR cdr) {
		String dayFromCdr = null;
		dayFromCdr = GMT_DATE_FORMATTER.format(new Date(cdr.getStarttime())).substring(0, 8);
		return dayFromCdr;
	}

}
