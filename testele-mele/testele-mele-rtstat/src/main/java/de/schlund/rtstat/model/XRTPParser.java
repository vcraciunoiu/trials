package de.schlund.rtstat.model;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class XRTPParser {

	private static Logger LOG = Logger.getLogger(XRTPParser.class);

	private static final String XRTP_NOTAVAILABLE = "";

	public XRTPParser() {
	}

	public XRTPValue parse(String xrtp_str) {

		XRTPValue ret = null;
		if (!xrtp_str.toLowerCase().equals(XRTP_NOTAVAILABLE.toLowerCase())) {
			String[] xrtp = xrtp_str.split(";");

			HashMap<String, String> xrtpMap = new HashMap<String, String>();
			String[] stringArray;
			for (int i = 0; i < xrtp.length; i++) {
				stringArray = new String[2];
				splitEqual(xrtp[i], stringArray);
				xrtpMap.put(stringArray[0], stringArray[1]);
			}

			ret = new XRTPValue();
			ret.setPS_audio_packets_sent(nullCheck("PS", xrtpMap));
			ret.setEsExpectedPacketsSend(nullCheck("ES", xrtpMap));
			ret.setOS_audio_octets_sent(nullCheck("OS", xrtpMap));
			ret.setSP_silence_packets_sent(xrtpMap.get("SP"));
			ret.setSO_silence_octets_sent(nullCheck("SO", xrtpMap));
			ret.setPR_audio_packets_received(nullCheck("PR", xrtpMap));
			ret.setErExpectedPacketsReceived(nullCheck("ER", xrtpMap));
			ret.setLsSequenceLoss(nullCheck("LS", xrtpMap));
			ret.setOR_audio_octets_received(nullCheck("OR", xrtpMap));
			ret.setCR_comfort_noise_packets_received(nullCheck("CR", xrtpMap));
			ret.setSR_comfort_noise_octets_received(nullCheck("SR", xrtpMap));

			Integer[] pl = splitOnCharacter(xrtpMap.get("PL"), ",", false);
			ret.setPL_receive_packets_lost_s(pl[1]);
			ret.setPL_receive_packets_lost_r(pl[0]);

			ret.setBL_receive_maximum_burst_packets_lost(nullCheck("BL", xrtpMap));
			ret.setEN_encoder(xrtpMap.get("EN"));
			ret.setDE_decoder(xrtpMap.get("DE"));

			Integer[] ji = splitOnCharacter(xrtpMap.get("JI"), ",", false);
			if (ji[1] == null) {
				ret.setJI_jitter_s(ji[0]);
				ret.setJI_jitter_r(ji[1]);
			} else {
				ret.setJI_jitter_s(ji[1]);
				ret.setJI_jitter_r(ji[0]);
			}

			ret.setCS_setup_time(nullCheckLong("CS", xrtpMap));

			Integer[] rb = splitOnCharacter(xrtpMap.get("RB"), "/", true);
			ret.setRB_receive_burst_duration(rb[0]);
			ret.setRB_receive_burst_density(rb[1]);

			Integer[] sb = splitOnCharacter(xrtpMap.get("SB"), "/", true);
			ret.setSB_send_burst_duration(sb[0]);
			ret.setSB_send_burst_density(sb[1]);

			Integer[] dl = splitOnCharacter(xrtpMap.get("DL"), ",", false);
			ret.setDL_delay_avg(dl[0]);
			ret.setDL_delay_max(dl[2]);
		}
		return ret;
	}
	private Integer nullCheck(String s, HashMap<String, String> m) {
		Integer ret = 0;
		try {
			ret = m.get(s) == null ? null : Integer.parseInt(m.get(s));
		} catch (NumberFormatException nfe) {
			throw new NumberFormatException("key=" + s + ", value=" + m.get(s));
		}
		return ret;
	}

	/*
	 * I put long only for CS because I observed from testing with live data
	 * that it only happens for CS.
	 */
	private Long nullCheckLong(String s, HashMap<String, String> xrtpMap) {
		return xrtpMap.get(s) == null ? null : Long.parseLong(xrtpMap.get(s));
	}

	private void splitEqual(String s, String[] stringArray) {
		int i = s.indexOf('=');
		stringArray[0] = s.substring(0, i);
		stringArray[1] = s.substring(i + 1);
	}

	private Integer[] splitOnCharacter(String s, String splitChar, boolean isTinyint) {
		Integer[] splittedInt = new Integer[3];
		if (s != null) {
			String[] splitted = s.split(splitChar);
			for (short j = 0; j < splitted.length; j++) {
				try {
					int parsed = Integer.parseInt(splitted[j]);
					if (isTinyint) {
						if (parsed > 255) {
							parsed = 255;
						}
					}
					splittedInt[j] = parsed;
				} catch (Exception e) {
					LOG.info("Error splitting on character.", e);
				}
			}
		}
		return splittedInt;
	}

}
