package de.schlund.rtstat.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.schlund.rtstat.processor.ProcessorThread;
import de.schlund.rtstat.processor.ser.ProviderUtil;
import de.schlund.rtstat.statistics.model.ASRCallData;
import de.schlund.rtstat.statistics.model.ASRCallDataOthers;
import de.schlund.rtstat.statistics.model.ASRTreeNode;
import de.schlund.rtstat.statistics.model.XRTPCallData;
import de.schlund.rtstat.statistics.processor.ASR;
import de.schlund.rtstat.statistics.processor.XRTP;
import de.schlund.rtstat.util.Constants;
import de.schlund.rtstat.util.UrlConnector;

public class StatsHttpClient extends ProcessorThread {

	static List<XRTP> _xrtp = null;
	static ASR _asr = null;
	String statsalyzerUrl;
	String hostname;
	String hostnameSuffix;
	String statsHeartbeat;
	String profileId = "";

	Collection<String> providers = new ArrayList<String>();
	Map<String, String> providerMap = new HashMap<String, String>();

	final static Logger LOG = Logger.getLogger(StatsHttpClient.class);

	public StatsHttpClient(String name) {
		super(name);
		init();
	}

	public void init() {
		Map<String, String> provMap = ProviderUtil.getProviderMap();
		Iterator<String> it = provMap.values().iterator();
		while (it.hasNext()) {
			String obj = it.next();
			if (!providers.contains(obj)) {
				providers.add(obj);
			}
		}

		for (String s : providers) {
			if (s.equalsIgnoreCase(Constants.PROVIDER_QSC)) {
				providerMap.put(s, Constants.PROVIDER_QSC);
			}
			if (s.equalsIgnoreCase(Constants.PROVIDER_TELEFONICA)) {
				providerMap.put(s, Constants.PROVIDER_TELEFONICA);
			}
			if (s.equalsIgnoreCase(Constants.PROVIDER_VODAFONE)) {
				providerMap.put(s, Constants.PROVIDER_VODAFONE);
			}
			if (s.equalsIgnoreCase(Constants.PROVIDER_VERIZON)) {
				providerMap.put(s, Constants.PROVIDER_VERIZON);
			}
			if (s.equalsIgnoreCase(Constants.PROVIDER_COLT)) {
				providerMap.put(s, Constants.PROVIDER_COLT);
			}
		}
	}

	protected void process() {

		TimerTask task = new TimerTask() {
			String content;

			public void run() {
				String url = statsalyzerUrl;

				try {
					content = getUrl();
				} catch (Exception e) {
					LOG.error("Error building URL string.", e);
				} // createUrl with params

				try {
					new UrlConnector(url).openConnection(content);
				} catch (Exception e) {
					LOG.error("Error sending data to Statsalyzer.", e);
				}
			}
		};
		int hb = 360; // default value;

		try {
			hb = Integer.valueOf(statsHeartbeat);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		new Timer(false).schedule(task, 0, hb * 1000L);

	}

	private String getUrl() throws Exception {
		Map<String, StringBuffer> pMap = new HashMap<String, StringBuffer>();

		StringBuffer completeUrl = new StringBuffer();
		if (_xrtp != null) {
			for (Iterator<XRTP> it = _xrtp.iterator(); it.hasNext();) {
				final XRTP curXRTP = it.next();
				final String curProvider = curXRTP.getProvider();
				XRTPCallData d = curXRTP.getXRTP();

				StringBuffer data = new StringBuffer();
				data.append(Constants.PACKETS_LOST).append(":").append(d.getPacketsLost()).append(",");
				data.append(Constants.DURATION_MS).append(":").append(d.getDurationMs()).append(",");
				data.append(Constants.RECEIVED_PACKETS_LOST).append(":").append(d.getReceivedMbPacketsLost()).append(",");
				data.append(Constants.CALLS_COUNT).append(":").append(d.getCallsCount()).append(",");
				data.append(Constants.JITTER).append(":").append(d.getJitter()).append(",");
				data.append(Constants.JITTER_CALLS_COUNT).append(":").append(d.getJitterCallsCount()).append(",");
				data.append(Constants.PACKETS_RECEIVED).append(":").append(d.getPacketsReceived()).append(",");
				data.append(Constants.MIN_PER_MIN).append(":").append(d.getMinutesPerMinute()).append(",");
				data.append(Constants.MIN_PER_MIN_INC).append(":").append(d.getMinutesPerMinuteIncoming()).append(",");
				pMap.put(curProvider, data);

				insertProviderASR(pMap, curProvider);
			}

			if (profileId.contains("_test")) {
				completeUrl.append("hostName=" + hostname + "_test");
			} else {
				completeUrl.append("hostName=" + hostname + hostnameSuffix);
			}

			for (Map.Entry<String, StringBuffer> entry : pMap.entrySet()) {
				if (entry.getValue() != null) {
					completeUrl.append("&profileData=").append(hostname).append("_").append(entry.getKey()).append(profileId).append("|");
					completeUrl.append(entry.getValue());
				}
			}
		}

		insertOtherASRCallData(completeUrl);

		long now = System.currentTimeMillis();
		completeUrl.append("&timestamp=").append(now);

		LOG.info(new Date(now) + " -- " + completeUrl.toString());

		return completeUrl.toString();
	}

	private void insertProviderASR(Map<String, StringBuffer> pMap, String p) {
		final ASRTreeNode asr = _asr.getASRTree().getTreeNode(p);
		StringBuffer buffer = pMap.get(p);

		final ASRCallData data = asr.collectASRCallData();

		insertASRCodes(buffer, data);
		insertCON1251ASRCodes(buffer, asr.getASRCallData(_asr.getCon1251Filter()));
		insertSipCodes(buffer, data);
		insertEmergencyCodes(p, asr, buffer);
		insertSpecialCounters(buffer, data);

		pMap.put(p, buffer);
	}

	private void insertSpecialCounters(StringBuffer newData, final ASRCallData data) {
		// special counters
		newData.append(Constants.CALLS_ONCE_ROUTED).append(":").append(data.getCallsOnceRouted()).append(",");
		newData.append(Constants.CALLS_WITHOUT_ACK).append(":").append(data.getCallsWithoutAck()).append(",");
		newData.append(Constants.CALLS_WITHOUT_BYE).append(":").append(data.getCallsWithoutBye()).append(",");
		newData.append(Constants.CALLS_NEW_SUCCESFULL).append(":").append(data.getCallsNewSuccesfull()).append(",");
		newData.append(Constants.CALLS_ACK_DISCARDED).append(":").append(data.getCallsAckDiscarded()).append(",");
		newData.append(Constants.CALLS_BYE_DISCARDED).append(":").append(data.getCallsByeDiscarded()).append(",");
		// ASR_CALLS_PREFIX
		newData.append(Constants.ASR_CALLS).append(":").append(data.getAllCalls());
	}

	private void insertASRCodes(StringBuffer newData, final ASRCallData data) {
		newData.append(Constants.ASR + ":" + data.getRatio(false)).append(",");
		newData.append(Constants.ASR_1XX).append(":").append(data.getBlockRatio(1, false)).append(",");
		newData.append(Constants.ASR_3XX).append(":").append(data.getBlockRatio(3, false)).append(",");
		newData.append(Constants.ASR_4XX).append(":").append(data.getBlockRatio(4, false)).append(",");
		newData.append(Constants.ASR_408).append(":").append(data.get408Ratio()).append(",");
		newData.append(Constants.ASR_480).append(":").append(data.get480Ratio()).append(",");
		newData.append(Constants.ASR_486).append(":").append(data.get486Ratio()).append(",");
		newData.append(Constants.ASR_487).append(":").append(data.get487Ratio()).append(",");
		newData.append(Constants.ASR_5XX).append(":").append(data.getBlockRatio(5, false)).append(",");
		newData.append(Constants.ASR_6XX).append(":").append(data.getBlockRatio(6, false)).append(",");
		newData.append(Constants.ASR_603).append(":").append(data.get603Ratio()).append(",");
		newData.append(Constants.ASR_7XX).append(":").append(data.getBlockRatio(7, false)).append(",");
	}

	private void insertCON1251ASRCodes(StringBuffer newData, final ASRCallData data) {
		newData.append(Constants.ASR + "_con1251" + ":" + data.getRatio(false)).append(",");
		newData.append(Constants.ASR_408 + "_con1251").append(":").append(data.get408Ratio()).append(",");
		newData.append(Constants.ASR_480 + "_con1251").append(":").append(data.get480Ratio()).append(",");
		newData.append(Constants.ASR_486 + "_con1251").append(":").append(data.get486Ratio()).append(",");
		newData.append(Constants.ASR_487 + "_con1251").append(":").append(data.get487Ratio()).append(",");
		newData.append(Constants.ASR_603 + "_con1251").append(":").append(data.get603Ratio()).append(",");
		newData.append(Constants.ASR_CALLS + "_con1251").append(":").append(data.getAllCalls()).append(",");
	}

	private void insertSipCodes(StringBuffer newData, final ASRCallData data) {
		for (short sipcode : ASRCallData.SIP_CODES) {
			if ((sipcode == 408) || (sipcode == 486) || (sipcode == 487)) {
				newData.append(sipcode).append("_c:").append(data.getCount(sipcode)).append(",");
			} else {
				newData.append(sipcode).append(":").append(data.getCount(sipcode)).append(",");
			}
		}
	}

	private void insertEmergencyCodes(String p, final ASRTreeNode asr, StringBuffer newData) {
		// emergency calls
		String emergencyPrefix = "";
		long asrEc200 = 0;
		long asrEc408 = 0;
		long asrEc480 = 0;
		long asrEc486 = 0;
		long asrEc487 = 0;
		long asrEc603 = 0;
		long asrEcAll = 0;
		ASRCallData em_data;

		if (p.equalsIgnoreCase(Constants.PROVIDER_QSC)) {
			emergencyPrefix = Constants.EMERGENCYPREFIX_QSC_1;
			em_data = asr.getASRCallData(emergencyPrefix);
			asrEc200 = em_data.getRatio(false);
			asrEc408 = em_data.get408Ratio();
			asrEc480 = em_data.get480Ratio();
			asrEc486 = em_data.get486Ratio();
			asrEc487 = em_data.get487Ratio();
			asrEc603 = em_data.get603Ratio();
			asrEcAll = em_data.getAllCalls() == 0 ? 1 : em_data.getAllCalls();

			emergencyPrefix = Constants.EMERGENCYPREFIX_QSC_2;
		} else if (p.equalsIgnoreCase(Constants.PROVIDER_TELEFONICA)) {
			emergencyPrefix = Constants.EMERGENCYPREFIX_TELEFONICA;
		}

		em_data = asr.getASRCallData(emergencyPrefix);
		asrEc200 += em_data.getRatio(false);
		asrEc408 += em_data.get408Ratio();
		asrEc480 += em_data.get480Ratio();
		asrEc486 += em_data.get486Ratio();
		asrEc487 += em_data.get487Ratio();
		asrEc603 += em_data.get603Ratio();
		asrEcAll += em_data.getAllCalls() == 0 ? 1 : em_data.getAllCalls();

		newData.append(Constants.ASR_EC).append(":").append(asrEc200).append(",");
		newData.append(Constants.ASR_EC_408).append(":").append(asrEc408).append(",");
		newData.append(Constants.ASR_EC_480).append(":").append(asrEc480).append(",");
		newData.append(Constants.ASR_EC_486).append(":").append(asrEc486).append(",");
		newData.append(Constants.ASR_EC_487).append(":").append(asrEc487).append(",");
		newData.append(Constants.ASR_EC_603).append(":").append(asrEc603).append(",");
		newData.append(Constants.ASR_EC_CALLS).append(":").append(asrEcAll).append(",");
	}

	private void insertOtherASRCallData(StringBuffer completeUrl) {
		// now insert the ASR data, which does not belong to a particular
		// provider
		completeUrl.append("&profileData=").append(hostname).append("_").append(Constants.OTHERS).append(profileId).append("|");
		final ASRTreeNode asr = _asr.getASRTree().getTreeNode(Constants.OTHERS);
		final ASRCallDataOthers asrCallDataOthers = asr.collectASRCallDataOthers();
		completeUrl.append(Constants.CALLS_VOICEBOX).append(":").append(asrCallDataOthers.getVoiceboxCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_ANNOUNCEMENTS).append(":").append(asrCallDataOthers.getAnnouncementCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_ONNET).append(":").append(asrCallDataOthers.getOnNetCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_ABROAD).append(":").append(asrCallDataOthers.getAbroadCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_MOBILE).append(":").append(asrCallDataOthers.getMobileCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_VOICEBOX_GOOD).append(":").append(asrCallDataOthers.getVoiceboxGoodCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_ANNOUNCEMENTS_GOOD).append(":").append(asrCallDataOthers.getAnnouncementGoodCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_ONNET_GOOD).append(":").append(asrCallDataOthers.getOnNetGoodCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_ABROAD_GOOD).append(":").append(asrCallDataOthers.getAbroadGoodCalls());
		completeUrl.append(",");
		completeUrl.append(Constants.CALLS_MOBILE_GOOD).append(":").append(asrCallDataOthers.getMobileGoodCalls());
	}

	public void run() {
		process();
	}

	public static void set_xrtp(List<XRTP> _xrtp) {
		StatsHttpClient._xrtp = _xrtp;
	}

	public void setStatsalyzerUrl(String statsalyzerUrl) {
		this.statsalyzerUrl = statsalyzerUrl;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * additional rtstat instance discriminator; needed because hostname is part
	 * of profilename, but we need to reuse profiles across rtstat instances,
	 * hence the hostname has to stay the same...
	 * 
	 * @param hostnameSuffix
	 */
	public void setHostnameSuffix(String hostnameSuffix) {
		this.hostnameSuffix = hostnameSuffix;
	}

	public void setStatsHeartbeat(String statsHeartbeat) {
		this.statsHeartbeat = statsHeartbeat;
	}

	static class SimpleStatsdata {
		private Map<String, List<Long>> values;
		private String provider;

		SimpleStatsdata(String provider) {
			this.provider = provider;
			values = new HashMap<String, List<Long>>();
		}

		public double[] getRatioAsDoubleArr() {
			double[] d = new double[values.keySet().size()];
			int i = 0;
			for (String s : values.keySet()) {
				List<Long> l = values.get(s);
				Long ratio = l.get(0);
				d[i++] = ratio.doubleValue();
			}
			return d;
		}

		public String getProvider() {
			return provider;
		}

		public Map<String, List<Long>> getValues() {
			return values;
		}
	}

	public static void set_asr(ASR _asr) {
		StatsHttpClient._asr = _asr;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

}
