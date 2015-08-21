package de.schlund.rtstat.processor;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.schlund.rtstat.util.ErrorReporter;

public class SyncSubscribtionDelegate<T> implements ISubscriptionDelegate<T> {
    
        final Logger LOG = Logger.getLogger(AsyncSubscriptionDelegate.class);

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
                    processor.processEvent(event);
                } catch (Exception e) {
                    ErrorReporter r = new ErrorReporter();
                    r.sendException(e);
                }
            }
        }
    }
  


