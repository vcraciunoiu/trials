package de.schlund.rtstat.processor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.schlund.rtstat.util.ErrorReporter;

public class SubscriptionDelegate<T> {

    final Logger LOG = Logger.getLogger(SubscriptionDelegate.class);

    private List<Processor<T>> _subscriber = new LinkedList<Processor<T>>();

    public void addConsumer(Processor<T> consumer) {
        _subscriber.add(consumer);
    }

    public void publish(T event) {
        for (Processor<T> processor : _subscriber) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Feeding " + processor.getName() + " with event:" + event);
                }
                processor.put(event);
            } catch (InterruptedException e) {
                ErrorReporter r = new ErrorReporter();
                r.sendException(e);
                LOG.error("Couldn't put event in queue, logging it.\n" + event, e);
            }
        }
    }

    // this is only for unit testing purposes
    public void removeAllconsumers() {
        _subscriber.clear();
    }
}
