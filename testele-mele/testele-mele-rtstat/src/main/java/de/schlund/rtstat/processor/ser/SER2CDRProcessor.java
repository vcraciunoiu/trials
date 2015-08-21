package de.schlund.rtstat.processor.ser;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.schlund.rtstat.model.Method;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.model.cdr.CDR;
import de.schlund.rtstat.model.cdr.CDRState;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.processor.SubscriptionDelegate;

/**
 * Build CDRs from SER logfile input.
 * 
 * @author mic
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class SER2CDRProcessor extends Processor<SERLogEvent> {

    private SubscriptionDelegate<CDR> sub = new SubscriptionDelegate<CDR>();

    public SubscriptionDelegate<CDR> getSubscriptionDelegate() {
        return sub;
    }

    private CDRState _cdrMap;
    private CDRState _cdrMapNon200;

    /**
     * Creates a SER2CDRProcessor
     * 
     * @param cruncher
     * @param qCapacity
     * @param expireAfter
     * @param recoveryFile
     * @throws IOException
     */
    public SER2CDRProcessor(String name, int qCapacity, int expireAfter) throws IOException {
        super(name, qCapacity);
        init(expireAfter);
    }

    public SER2CDRProcessor(String name, Integer expireAfter) throws IOException {
        super(name);
        init(expireAfter);
    }

    private void init(int expireAfter) {
        _cdrMap = new CDRState(expireAfter);
        _cdrMapNon200 = new CDRState(expireAfter);
        startTimer();
    }
    
    public void setConsumer(List<Processor<CDR>> pthreads) {
        for (Processor<CDR> p : pthreads) {
            sub.addConsumer(p);
        }
    }

    private void startTimer() {
        TimerTask task = new TimerTask() {
            public void run() {
                synchronized (_cdrMapNon200) {
                    _cdrMapNon200.removeOldestEntryNon200(sub);
                }
            }
        };
        int hb = 10;
        new Timer(false).schedule(task, 0, hb * 1000L);
    }

    @Override
    protected void processEvent(SERLogEvent event) {
        if (event.getMethod() == Method.INVITE) {
            processINVITE(event);
        } else if (event.getMethod() == Method.ACK) {
            processACK(event);
        } else if (event.getMethod() == Method.BYE) {
            processBYE(event);
        } else {
            LOG.info("skipped method '" + event.getMethod() + "' in " + event.toString());
        }
    }

    private void processINVITE(SERLogEvent event) {
        CDR cdr = buildCDR(event);

            final String call_id = cdr.getCall_id();
            if (LOG.isDebugEnabled()) {
                LOG.debug("_cdrmap#BEFORE put:" + _cdrMap);
            }
            final CDR old = _cdrMap.get(call_id);
            if (old != null) {
                LOG.info("update event with newer INVITE");
                if (LOG.isDebugEnabled()) {
                    LOG.debug(old);
                }
                old.updateINVITE(event);
                LOG.info(old);
            } else {
                if (event.getCode() != 200) {
                    final String call_id_non200 = cdr.getCall_id();
                    final CDR old_non200 = _cdrMapNon200.get(call_id_non200);
                    if (old_non200 != null) {
                        old_non200.updateINVITE(event);
                    } else {
                        synchronized (_cdrMapNon200) {
                            _cdrMapNon200.put(call_id_non200, cdr);
                        }
                    }
                } else {
                    _cdrMap.put(call_id, cdr);
                    _cdrMap.removeOldestEntry(sub);
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("_cdrmap#AFTER put:" + _cdrMap);
            }
    }

    private void processACK(SERLogEvent event) {
        // we're not interested in non-200 ACKs!
        if (event.getCode() == 200) {
            CDR cdr = _cdrMap.get(event.getCall_id());
            if (cdr == null) {
                LOG.info("ACK without INVITE [Timestamp:" + event.getTimestamp() + " Call-ID:" + event.getCall_id() + "]");
                CDR cdrDiscarded = buildCDR(event);
                cdrDiscarded.setStateACKdiscarded();
                sub.publish(cdrDiscarded);
            } else if (cdr.getState() != CDR.STATE.INVITE) {
                LOG.info("unexpected State [Timestamp:" + event.getTimestamp() + " Call-ID:" + event.getCall_id() + " State:" + cdr.getState() + "]");
            } else {
                cdr.setStateACK(event);
            }
        }
    }

    private void processBYE(SERLogEvent event) {
        final String call_id = event.getCall_id();
        CDR cdr = _cdrMap.remove(call_id);

        if (cdr == null) {
            LOG.info("BYE without INVITE [Timestamp:" + event.getTimestamp() + " Call-ID:" + call_id + "]");
            CDR cdrDiscarded = buildCDR(event);
            cdrDiscarded.setStateBYEdiscarded();
            sub.publish(cdrDiscarded);
        } else {
            if (cdr.getState() != CDR.STATE.ACK) {
                LOG.warn("Unexpected State [Timestamp:" + event.getTimestamp() + " Call-ID:" + call_id + " State:" + cdr.getState() + "]");
                if (cdr.getCode() == 200) {
                    cdr.getFirstINVITE().setCode((short) 701);
                }
            }
            cdr.setStateBYE(event);
            sub.publish(cdr);
        }
    }

    private CDR buildCDR(SERLogEvent event) {
        CDR cdr = new CDR(event.getProperty("A-User"), event.getProperty("A-Provider"), event.getProperty("B-User"), event.getProperty("B-Provider"), event);
        return cdr;
    }

    public static String join(String delimiter, String... strings) {
        StringBuilder sb;

        if (strings == null)
            return null;
        if (strings.length == 0)
            return null;

        sb = new StringBuilder(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            sb.append(delimiter).append(strings[i]);
        }
        return sb.toString();
    }

}
