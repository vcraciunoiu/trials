package de.schlund.rtstat.processor.ser;

import java.util.List;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.processor.SubscriptionDelegate;

/**
 * @author mic
 */
public class AsyncPropertiesProcessor extends Processor<SERLogEvent> implements IPropertiesProcessor {

    private SubscriptionDelegate<SERLogEvent> sub = new SubscriptionDelegate<SERLogEvent>();

    private ProviderUtil providerutil;

    public SubscriptionDelegate<SERLogEvent> getSubscriptionDelegate() {
        return sub;
    }

    public AsyncPropertiesProcessor(String name, Integer qCapacity) {
        super(name, qCapacity);
    }

    public AsyncPropertiesProcessor(String name) {
        super(name);
    }

    @Override
    public void processEvent(SERLogEvent event) {
        providerutil.setFromProvider(event);
        providerutil.setToProvider(event);
        sub.publish(event);
    }

    public void setProviderUtil(ProviderUtil p) {
        this.providerutil = p;
    }
    
    public ProviderUtil getProviderUtil() {
        return providerutil;
    }

    public void setConsumer(List<Processor<SERLogEvent>> pthreads) {
        for(Processor<SERLogEvent> p : pthreads) {
            sub.addConsumer(p);
        }
    }
    
}
