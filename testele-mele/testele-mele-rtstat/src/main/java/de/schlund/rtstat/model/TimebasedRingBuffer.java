package de.schlund.rtstat.model;


import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.schlund.rtstat.util.Util;

public class TimebasedRingBuffer<T> implements RingBuffer<T> {

    private static final Logger LOG = Logger.getLogger(TimebasedRingBuffer.class);

    protected static class TimestampedElement<T> {
        private T element;

        private long timestamp;

        public TimestampedElement(T e) {
            element = e;
            timestamp = System.currentTimeMillis();
        }

        public TimestampedElement(T e, long t) {
            element = e;
            timestamp = t;
        }

        public T getElement() {
            return element;
        }

        public long getTimestamp() {
            return timestamp;
        }
        
        public String toString() {
            return "Elemtent["+element+"] Timestamp["+Util.readableTimestamp(timestamp)+"]";
        }
    }

    private LinkedList<TimestampedElement<T>> buffer;

    private long maxAge;

    /**
     * @param maxAge max age of elements in ms
     */
    public TimebasedRingBuffer(long maxAge) {
        buffer = new LinkedList<TimestampedElement<T>>();
        this.maxAge = maxAge;
    }

    public long getMaxAgeMs() {
        return maxAge;
    }

    @Deprecated
    public List<T> add(T value) {
        buffer.add(new TimestampedElement<T>(value));
        return update();
    }

    public List<T> add(T value, long timestamp) {
        buffer.add(new TimestampedElement<T>(value, timestamp));
        return update();
    }

    /**
     * <b>call update before peeking</b>, otherwise this call can return an outdated entry 
     */
    public T peek() {
        final TimestampedElement<T> tmp = buffer.peek();
        return (tmp == null ? null : tmp.getElement());
    }

    /**
     * <b>call update before peeking</b>, otherwise this call can return an outdated entry 
     */
    public int size() {
        return buffer.size();
    }

    public List<T> update() {

        if(LOG.isDebugEnabled()) {
            LOG.debug("Buffer before update:\n"+toString());
        }
        
        LOG.info("Ringbuffer["+this.hashCode()+"  Maxage="+maxAge+" before update: size is"+buffer.size());
        
        List<T> result = null;
        if (buffer.size() != 0) {
            final long now = System.currentTimeMillis();
           
           TimestampedElement<T> oldest = null;
           oldest = buffer.peek();
           
           try {
               // try to fix problem below
               if(buffer.contains(null)) {
                   LOG.warn("Buffer contains null value!"+toString());
               }
           } catch(Exception e) {
               LOG.error("Exception when checking the buffer for null values",e);
           }
           
           if(oldest == null ) {
               // I do not know how this list can contain null elements:-(
               // that peek returns null. however if peek returns null
               // set returnvalue stay null and I clear the buffer
               buffer.clear();
           } else {
               if (oldest.getTimestamp() < (now - maxAge)) {
                    final List<T> removed = new LinkedList<T>();
                    while (oldest != null && oldest.getTimestamp() < (now - maxAge)) {
                        try {
                            buffer.remove(oldest);
                        } catch (Exception e) {
                            LOG.error("Exception in buffer.remove", e);
                            LOG.error("Buffer:"+buffer);
                        }
                        removed.add(oldest.getElement());
                        oldest = buffer.peek();
                    }
                    result = removed;
                }
            }
        }
        if(LOG.isDebugEnabled()) {
            LOG.debug("Buffer after update:\n"+toString());
        }
        
        LOG.info("Ringbuffer["+this.hashCode()+"  Maxage="+maxAge+" after update: size is"+buffer.size());
        
        return result;
    }
    
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("BufferSize is:"+buffer.size()+"\n");
        for(int i=0; i<buffer.size(); i++) {
            TimestampedElement<T> e = buffer.get(i);
            ret.append(i + "["+e+"], \n");
        }
        return ret.toString();
    }
}
