package de.schlund.rtstat.processor.ser;

import java.util.List;

import de.schlund.rtstat.model.Method;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.processor.SubscriptionDelegate;
import de.schlund.rtstat.util.Util;

/**
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class CurrentTimestampProcessor extends Processor<SERLogEvent> {

    private SubscriptionDelegate<SERLogEvent> sub = new SubscriptionDelegate<SERLogEvent>();

    public SubscriptionDelegate<SERLogEvent> getSubscriptionDelegate() {
        return sub;
    }

    public CurrentTimestampProcessor(String name, int qCapacity) {
        super(name, qCapacity);
    }

    public CurrentTimestampProcessor(String name) {
        super(name);
    }

    @Override
    protected void processEvent(SERLogEvent event) {
        // @TODO: hack
        long timestamp;
        if (event.getMethod() == Method.BYE) {
            timestamp = System.currentTimeMillis() - 1000;
        } else if (event.getMethod() == Method.INVITE) {
            timestamp = System.currentTimeMillis() - 10000;
        } else if (event.getMethod() == Method.ACK) {
            timestamp = System.currentTimeMillis() - 7000;
        } else {
            throw new IllegalStateException("Unkown event:"+event.getMethod().toString());
        }
        
        if(LOG.isDebugEnabled()) {
            String d1 = Util.readableTimestamp(timestamp); 
            String d2 = Util.readableTimestamp(event.getTimestamp());
            LOG.debug("\nReplacing timestamp at event["+event.hashCode()+"] ["+d2+"]->["+d1+"]");
        }
        event.setTimestamp(timestamp);
        sub.publish(event);
    }
    
    public void setConsumer(List<Processor<SERLogEvent>> pthreads) {
        for(Processor<SERLogEvent> p : pthreads) {
            getSubscriptionDelegate().addConsumer(p);
        }
    }

}
