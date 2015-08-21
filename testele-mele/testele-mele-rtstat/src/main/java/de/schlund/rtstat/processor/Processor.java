package de.schlund.rtstat.processor;

import java.util.concurrent.ArrayBlockingQueue;

import de.schlund.rtstat.util.ErrorReporter;

/**
 * <p>
 * base class for all processors, more fun and less code with generics
 * </p>
 * 
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 * @version 1.0
 */
public abstract class Processor<T> extends ProcessorThread {

    private ArrayBlockingQueue<T> _queue;

    public final static int DEFAULT_QUEUE_SIZE = 5000;
    public final static int LIMIT_FOR_LOG = 900;
    protected ErrorReporter reporter;

    public Processor(String name, int capacity) {
        super(name);
        _queue = new ArrayBlockingQueue<T>(capacity);
    }

    public Processor(String name) {
        this(name, DEFAULT_QUEUE_SIZE);
    }

    public void setErrorReporter(ErrorReporter rep) {
        reporter = rep;
    }

    public void put(T o) throws InterruptedException {
        if (o == null) {
            // create exception to get a stacktrace
            final Exception e = new Exception();
            LOG.error("received 'null'", e);
        } else {
            _queue.put(o);
            // logQueuesize("after take", LIMIT_FOR_LOG);
        }
    }

    private void logQueuesize(String where, int limit) { 
        if (LOG.isDebugEnabled()) {
            int s = _queue.size();
            if (s > limit) {
                LOG.debug("Queuesize for " + getName() + " " + where + " is " + s);
            }
        }
    }

    public T take() throws InterruptedException {
        // logQueuesize("before take", LIMIT_FOR_LOG);
        return _queue.take();
    }

    protected abstract void processEvent(T event);

    public void run() {
        Thread t = Thread.currentThread();
        while (_thread == t) {
            try {
                processEvent(take());
            } catch (Exception e) {
                LOG.fatal("", e);
            }
        }
    }

    @Override
    public String toString() {
        return "[" + this.getClass() + "]";
    }
}
