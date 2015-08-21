package de.schlund.rtstat.statistics.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import de.schlund.rtstat.model.RingBuffer;
import de.schlund.rtstat.util.Constants;

public class XRTPCallData {

    private static final Logger LOG = Logger.getLogger(XRTPCallData.class);

    public static final int MIN_CALL_DURATION = 2000;
    public static final int MAX_PLAUSI_JITTER_VALUE = 1000 * 1000;

    private static final float MAX_OK_PACKET_LOSS_PER_MINUTE = 100;

    private static final float MAX_OK_PACKET_LOSS_RATIO = 30;

    /**
     * number of packets sent or sum of ..
     */
    private int _packetsSent;

    /**
     * number of packets received or sum of ..
     */
    private int _packetsReceived;

    /**
     * number of packets lost or sum of ..
     */
    private int _packetsLost;

    /**
     * max number of packets lost during a burst or sum of ...
     */
    private int _receivedMbPacketsLost;

    /**
     * jitter for this call or sum of all jitter
     */
    private long _jitter;

    /**
     * call duration in ms for this call or sum of all call durations
     */
    private long _durationMs;

    private long _durationMsIncoming;

    /**
     * number of calls with a jitter > 0
     */
    private long _jitterCallsCount;

    /**
     * number of calls
     */
    private long _callsCount;

    private long _timestamp;

    public XRTPCallData(int ps, int pr, int pl, int bl, long ji, long durationMs, long durationMsIncoming, long ts) {
        _packetsSent = ps;
        _packetsReceived = pr;
        _packetsLost = pl;
        _receivedMbPacketsLost = bl;
        _durationMs = durationMs;
        _durationMsIncoming = durationMsIncoming;
        if (ji >= 0) {
            _jitter = ji;
            _jitterCallsCount = 1;
        }
        _callsCount = 1;
        _timestamp = ts;
    }

    public XRTPCallData add(XRTPCallData xd) {
        _packetsSent += xd._packetsSent;
        _packetsReceived += xd._packetsReceived;
        _packetsLost += xd._packetsLost;
        _receivedMbPacketsLost += xd._receivedMbPacketsLost;
        if (xd._jitter >= 0) {
            _jitter += xd._jitter;
            _jitterCallsCount++;
        }
        _callsCount += xd._callsCount;
        _durationMs += xd._durationMs;
        _durationMsIncoming += xd._durationMsIncoming;
        return this;
    }

    public XRTPCallData addIncoming(XRTPCallData xd) {
        _durationMsIncoming += xd._durationMsIncoming;
        return this;
    }

    public XRTPCallData sub(XRTPCallData xd) {
        _packetsSent -= xd._packetsSent;
        _packetsReceived -= xd._packetsReceived;
        _packetsLost -= xd._packetsLost;
        _receivedMbPacketsLost -= xd._receivedMbPacketsLost;
        if (xd._jitter >= 0) {
            _jitter -= xd._jitter;
            _jitterCallsCount--;
        }
        _callsCount -= xd._callsCount;
        _durationMs -= xd._durationMs;
        _durationMsIncoming -= xd._durationMsIncoming;
        return this;
    }

    public int getPacketsSent() {
        return _packetsSent;
    }

    public float getPacketLossPerMinute() {
        return (float) _packetsLost * 1000 * 60 / _durationMs;
    }

    /**
     * @return average block loss per call
     */
    public float getAvgBlockLoss() {
        return (float) _receivedMbPacketsLost / _callsCount;
    }

    public float getPacketLossRatio() {
        final long sum = _packetsReceived + _packetsLost;
        return (sum == 0 ? 0 : (float) _packetsLost * 1000 / sum);
    }

    /**
     * @return number of seconds in buffer
     */
    public double getDuration() {
        final double l = _durationMs / 1000;
        return l;
    }

    public double getMinutesPerMinute() {
        // the buffer contains events for 6 minutes and get duration returns the
        // sum in s
        // seconds in buffer / (60 (->min) * 6-minute buffer)
        return getDuration() / (60 * 6);
    }

    public double getMinutesPerMinuteIncoming() {
        return _durationMsIncoming / (1000 * 60 * 6);
    }

    public float getAvgJitter() {
        float ret = 0;

        if (_jitterCallsCount != 0) {
            ret = (float) _jitter / _jitterCallsCount;
            if (LOG.isDebugEnabled()) {
                LOG.debug("AvgJitter::[Jitter complete] / [All Jitter samples]" + "\n::" + _jitter + " / " + _jitterCallsCount + " = " + ret);
            }
        }
        return ret;
    }

    public long getNrSamples() {
        return _jitterCallsCount;
    }

    public boolean plausi() {
        boolean ret = true;
        if (_packetsLost > _packetsSent) {
            LOG.warn("No plausi: " + this + " PL>" + "PS");
            ret = false;
        }
        if (_durationMs < MIN_CALL_DURATION)
            ret = false;
        if (_jitter > MAX_PLAUSI_JITTER_VALUE) {
            LOG.warn("Jitter plausi false: Jitter[" + _jitter + "] > " + MAX_PLAUSI_JITTER_VALUE);
            _jitter = 5000;
            ret = true;
        }
        return ret;
    }

    /**
     * @return List with messages, emtpy List for no warning
     */
    public List<String> needWarning() {

        List<String> ret = new ArrayList<String>();
        float packetLossRatio = getPacketLossRatio();
        if (packetLossRatio > MAX_OK_PACKET_LOSS_RATIO) {
            ret.add("PACKET_LOSS_RATIO::" + packetLossRatio + ">" + MAX_OK_PACKET_LOSS_RATIO);
        }
        return ret;
    }

    public String getFormattedTimestamp() {
        return Constants.SDF.format(new Date(_timestamp));
    }

    public XRTPCallData updateTimestamp(RingBuffer<XRTPCallData> buffer) {
        if (buffer == null || buffer.size() == 0) {
            _timestamp = System.currentTimeMillis();
        } else {
            final XRTPCallData tmp = buffer.peek();
            // to avoid strange NPE, which occured here, perhaps due to concurrend access to buffer?
            if (tmp == null) {
                LOG.error("buffer.peek returned null");
                _timestamp = System.currentTimeMillis();
            } else {
                _timestamp = tmp._timestamp;
            }
        }
        return this;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public int getPacketsLost() {
        return _packetsLost;
    }

    public int getReceivedMbPacketsLost() {
        return _receivedMbPacketsLost;
    }

    public long getCallsCount() {
        return _callsCount;
    }

    public long getDurationMs() {
        return _durationMs;
    }

    public long getJitter() {
        return _jitter/8;
    }

    public long getJitterCallsCount() {
        return _jitterCallsCount;
    }

    public int getPacketsReceived() {
        return _packetsReceived;
    }

}
