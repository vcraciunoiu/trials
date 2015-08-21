package de.schlund.rtstat.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mic
 */
public class FixedSizeRingBuffer<T> implements RingBuffer<T> {

    private LinkedList<T> _buf;
    private int           _capacity;

    public FixedSizeRingBuffer(int capacity) {
        super();
        _capacity = capacity;
        _buf = new LinkedList<T>();
    }

    /* (non-Javadoc)
     * @see de.schlund.rtstat.model.RingBuffer#add(T)
     */
    public List<T> add(T value) {
        _buf.add(value);
        
        if (_buf.size() > _capacity) {
            final List<T> ret = new LinkedList<T>();
            ret.add(_buf.remove());
            return ret;
        } else {
            return null;
        }
        
        
    }

    /* (non-Javadoc)
     * @see de.schlund.rtstat.model.RingBuffer#size()
     */
    public int size() {
        return _buf.size();
    }

    /* (non-Javadoc)
     * @see de.schlund.rtstat.model.RingBuffer#peek()
     */
    public T peek() {
        return _buf.peek();
    }

    /**
     * does nothing, all necessary updates are made in add
     */
    public List<T> update() {
        return null;
    }
    

}
