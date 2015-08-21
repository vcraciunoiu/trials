package de.schlund.rtstat.model.cdr;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.schlund.rtstat.processor.SubscriptionDelegate;

/**
 * A specialized LinkedHashMap which automatically expires old CDRs. Maps SER
 * CallIds to CDR Objects.
 */

public class CDRState extends LinkedHashMap<String, CDR> {

    private static final long serialVersionUID = 8491100150744972622L;

    private long _maxTime;

    private static long _cdrExpireAfter;

    private static Logger LOG = Logger.getLogger(CDRState.class.getName());

    public CDRState(long expire) {
        _cdrExpireAfter = expire;
    }

    public static long getExpireAfter() {
        return _cdrExpireAfter;
    }

    @Override
    public CDR put(String key, CDR value) {
        _maxTime = value.getStarttime();
        return super.put(key, value);
    }

    /**
     * Walk thru the collection as long as there are elements which are older
     * than _cdrExpireAfter. Remove them!
     * 
     * @param eldest
     *            This parameter is not used in this implementation
     * @return returns always false because it manipulates the collection
     *         itself.
     */
    public boolean removeOldestEntry(SubscriptionDelegate<CDR> sub) {
        long age;
        // get an iterator for all entries in this map
        Iterator<Map.Entry<String, CDR>> i = entrySet().iterator();

        do {
            Map.Entry<String, CDR> e = i.next();
            // calculate age of this entry (which is the diffence between the
            // timestamp of the newest entry (_maxTime) and the timestamp of this entry)
            age = _maxTime - e.getValue().getStarttime();
            // remove this entry, if its age is greater than _cdrExpireAfter (defined in SER2CDRProcessor).
            if (age > _cdrExpireAfter) {
                LOG.debug("removing stale CDR [" + e.getValue().getCall_id() + "], age=" + age + ", _maxTime=" + _maxTime + ", startime=" + e.getValue().getStarttime());

                // we have to handle the situation when there was no ACK, besides no BYE
                if (e.getValue().getState() != CDR.STATE.ACK && e.getValue().getCode() == 200) {
                    e.getValue().getFirstINVITE().setCode((short) 701);
                    if (LOG.isDebugEnabled())
                        LOG.debug("Changed code to 701 for CDR " + e.getValue().getCDRCall_id() + "");
                }

                sub.publish(e.getValue());
                i.remove();
            }
            // stop iteration at the first element whose age is not greater than
            // _cdrExpireAfter
        } while (i.hasNext() && age > _cdrExpireAfter);
        // always return false!
        return false;
    }

    public boolean removeOldestEntryNon200(SubscriptionDelegate<CDR> sub) {
        long age;
        Iterator<Map.Entry<String, CDR>> i = entrySet().iterator();

        while (i.hasNext()) {
            Map.Entry<String, CDR> e = i.next();
            age = _maxTime - e.getValue().getStarttime();
            if (age > 60000) {
                sub.publish(e.getValue());
                i.remove();
            }
        };
        return false;
    }

}
