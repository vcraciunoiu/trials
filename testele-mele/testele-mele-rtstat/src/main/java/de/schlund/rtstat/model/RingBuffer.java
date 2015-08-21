package de.schlund.rtstat.model;

import java.util.List;

public interface RingBuffer<T> {

    /**
     * @param value
     * @return
     */
    public List<T> add(T value);

    /* (non-Javadoc)
     * @see java.util.LinkedList#size()
     */
    public int size();

    /* (non-Javadoc)
     * @see java.util.LinkedList#peek()
     */
    public T peek();

    /**
     * check if elements must be removed from buffer
     * @return null or a List of elements which have been removed
     */
    public List<T> update();
}