package de.schlund.rtstat.statistics.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.TimebasedRingBuffer;
import de.schlund.rtstat.model.XRTPValue;
import de.schlund.rtstat.model.cdr.CDR;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.statistics.model.XRTPCallData;
import de.schlund.rtstat.util.Constants;

/**
 * buffers
 * 
 * @author mic
 */
public class XRTP extends Processor<CDR> {

    private static final Logger LOG = Logger.getLogger(XRTP.class);

    /**
     * @see http://www.avm.de/de/Presse/Informationen/2005/pdf/Iptelorg_QoS-Spec.pdf
     *      1: PS=<audio packets sent> 
     *      2: OS=<audio octets sent> 
     *      3: SP=<comfort noise packets sent>,<silence packets sent> 
     *      4: SO=<silence octets sent> 
     *      5: PR=<audio packets received> 
     *      6: OR=<audio octets received>
     *      7: CR=<comfort noise packets received> 
     *      8: SR=<comfort noise octets received> 
     *      9: PL=<receive packets lost> 
     *      10:BL=<receive maximum burst packets lost> 
     *      11:EN=<encoder1 used>,<encoder2 used>...
     *      12:DE=<decoder1 used>,<decoder2 used>... 
     *         - possible coder values: "G723","G729","PCMA","PCMU","iLBC-20",
     *                                  "iLBC-30","G726-16","G726-24",
     *                                  "G726-32","G726-40","AMR-WB","Linear" 
     *      13:JI=<jitter in ms>
     * 
     */

    private static final Logger LOG_WARN = Logger.getLogger("LOGGER_WARN");

    private String _provider;

    private TimebasedRingBuffer<XRTPCallData> _buffer;

    private XRTPCallData _sum;

//    private SubscriptionDelegate<CDR> sub = new SubscriptionDelegate<CDR>();
//
//    public SubscriptionDelegate<CDR> getSubscriptionDelegate() {
//        return sub;
//    }

    public TimebasedRingBuffer<XRTPCallData> getBuffer() {
        return _buffer;
    }

    public XRTP(int capacity, String provider, int ringbufferduration, int port) {
        super(provider, capacity);
        init(provider, ringbufferduration, port);
    }

    public XRTP(String provider, Integer ringbufferduration, Integer port) {
        super(provider);
        init(provider, ringbufferduration, port);
    }

    private void init(String provider, int ringbufferduration, int port) {
        _provider = provider;
        _sum = new XRTPCallData(0, 0, 0, 0, -1, 1, 1, Long.MAX_VALUE);
        _buffer = new TimebasedRingBuffer<XRTPCallData>(ringbufferduration);
        /*
         * Thread t = new DebugSocketPrinter(this, port); t.start();
         */
    }

    @Override
    protected void processEvent(CDR cdr) {
        try {
            
            // "discarded" CDR (ACKs and BYEs wihtout INVITE) do not count
            if ( !(cdr.getState().equals(CDR.STATE.ACK_DISCARDED) || cdr.getState().equals(CDR.STATE.BYE_DISCARDED)) ) {
                
                // CDRs older than 120 minutes will not take part in statistic calculation
                final long timestamp = cdr.getEndtime();
                final long now = System.currentTimeMillis();
                final long tooold = now - (120 * 60 * 1000);

                if (timestamp < tooold) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("ancient (" + ((now - timestamp) / 1000) + "s) event found: " + cdr.getCall_id());
                    }
                } else {
                    if (cdr.getBYE() != null && cdr.getCode() == 200) {
                        
                        // first we handle the outgoing traffic, taking in account the provider
                        if (_provider.equalsIgnoreCase(cdr.getB().getDomain())) {
                            XRTPValue xrtp = cdr.getBYE().getXrtp1();

                            if (xrtp != null) {
                                final XRTPCallData calldata = new XRTPCallData(
                                        xrtp.getPS_audio_packets_sent(), 
                                        xrtp.getPR_audio_packets_received(), 
                                        xrtp.getPL_receive_packets_lost_r(),
                                        xrtp.getBL_receive_maximum_burst_packets_lost(), 
                                        xrtp.getJI_jitter_s(), 
                                        cdr.getDuration(), 
                                        0, 
                                        cdr.getEndtime());

                                if (calldata.plausi()) {
                                    final List<String> needWarning = calldata.needWarning();
                                    if (needWarning.size() > 0) {
                                        LOG_WARN.warn("Event with warning:[" + needWarning.get(0) + "]|" + cdr.getBYE().getCall_id());
                                    }

                                    final List<XRTPCallData> data = _buffer.add(calldata, calldata.getTimestamp());
                                    updateState(data);
                                    _sum.add(calldata);
                                    cdr.setXRTPCallData(calldata);
                                    if (_sum.needWarning().size() > 0) {
                                        LOG_WARN.warn("Event triggered warn:|" + cdr.getBYE().getCall_id() + "\n" + toString());
                                    }
                                }
                            } else {
                                if (LOG.isDebugEnabled())
                                    LOG.debug("XRTP not available, is null");
                            }

                        }

                        // and now we handle the incoming traffic, taking in account the from provider
                        if ( _provider.equalsIgnoreCase(cdr.getFirstINVITE().getFromProvider()) ||
                             (_provider.equals(Constants.PROVIDER_QSC) && 
                              cdr.getFirstINVITE().getFromProvider().equals(Constants.PROVIDER_QSC_CAPITALS)
                             )   
                           ) {
                            final XRTPCallData calldata = new XRTPCallData(0, 0, 0, 0, 0, 0, cdr.getDuration(), cdr.getEndtime());
                            final List<XRTPCallData> data = _buffer.add(calldata, calldata.getTimestamp());
                            updateState(data);
                            _sum.add(calldata);
                        }
                    }
                }
            }
        } catch (Exception e) {
//            ErrorReporter r = new ErrorReporter();
//            r.sendException(e);
            LOG.error("Error: ", e);
        }
    }

    private void updateState(List<XRTPCallData> data) {
        if (data != null) {
            for (XRTPCallData xrtp : data) {
                _sum.sub(xrtp);
            }
        }
    }

    public XRTPCallData getXRTP() {
        final List<XRTPCallData> data = _buffer.update();
        updateState(data);
        return _sum.updateTimestamp(_buffer);
    }

    /**
     * needed for monitoring output
     */
    public int getBuffersize() {
        return _buffer.size();
    }

    public String getProvider() {
        return _provider;
    }

    // @Override
    /*
     * public String toString() { XRTPCallData d = getXRTP(); StringBuilder sb =
     * new StringBuilder();
     * 
     * sb.append("pl/min: ").append(d.getPacketLossPerMinute()).append("\n");
     * sb.append("avg bl: ").append(d.getAvgBlockLoss()).append("\n");
     * sb.append("ji: ").append(d.getAvgJitter()).append("\n");
     * sb.append("pl/promille: ").append(d.getPacketLossRatio()).append("\n");
     * sb
     * .append("aggregated duration of calls/sec: ").append(d.getDuration()).append
     * ("\n");
     * sb.append("jitter samples: ").append(d.getNrSamples()).append("\n");
     * sb.append
     * ("date of oldest sample: ").append(d.getFormattedTimestamp()).append
     * ("\n"); return sb.toString(); }
     */

    /**
     * Sonja Pieper needs the current XRTP Data for displaying it by the
     * Statsylyzer Servlet
     * (http://statsalyzer.pumama.schlund.de:8080/xml/stats/xrtp) Therefore we
     * have to collect the current XRTP stuff as key-value pairs that can be
     * processed by the servlet.
     */
    public Map<String, String> getXRTPToStatsalyzerData() {
        Map<String, String> data = new HashMap<String, String>();
        XRTPCallData d = getXRTP();

        data.put("plMin", d.getPacketLossPerMinute() + "");
        data.put("blAvg", d.getAvgBlockLoss() + "");
        data.put("jitter", d.getAvgJitter() + "");
        data.put("plPromille", d.getPacketLossRatio() + "");
        data.put("minPerMin", d.getMinutesPerMinute() + "");
        data.put("jitterSamples", d.getNrSamples() + "");
        data.put("oldestSample", d.getFormattedTimestamp());

        return data;
    }

}
