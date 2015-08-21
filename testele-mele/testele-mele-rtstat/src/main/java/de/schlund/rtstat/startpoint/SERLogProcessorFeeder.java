package de.schlund.rtstat.startpoint;

import java.util.List;

import org.apache.log4j.Logger;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.processor.ProcessorThread;
import de.schlund.rtstat.processor.SubscriptionDelegate;

/**
 * Base for classes which read raw SER logs from any input. Parse the SER logs
 * and put them into a <see>SERLogEvent</see> pass this to registered
 * <see>SERLogProcessor</see>
 */
public abstract class SERLogProcessorFeeder extends ProcessorThread {

    public SERLogProcessorFeeder(String name) {
        super(name);
    }

    final SubscriptionDelegate<SERLogEvent> sub = new SubscriptionDelegate<SERLogEvent>();

    public SubscriptionDelegate<SERLogEvent> getSubscriptionDelegate() {
        return sub;
    }

    private final static Logger LOG = Logger.getLogger(SERLogProcessorFeeder.class);

    protected void process(final SERLogEvent event) {
        try {
            if (event.isValid()) {
                sub.publish(event);
            } else {
                LOG.error("unable to process record [" + event.getCall_id().toString() + "]");
            }
        } catch (Exception e) {
            LOG.error("Exception when processing event:" + event.getCall_id(), e);
        }
    }

    public void setConsumer(List<Processor<SERLogEvent>> pthreads) {
        for (Processor<SERLogEvent> p : pthreads) {
            getSubscriptionDelegate().addConsumer(p);
        }
    }
    
    // this is only for unit testing purposes
    public void removeAllconsumers() {
        getSubscriptionDelegate().removeAllconsumers();
    }
    
}
