package de.schlund.rtstat.test.model;

import java.util.List;

import de.schlund.rtstat.model.TimebasedRingBuffer;
import junit.framework.TestCase;

public class TimebasedRingBufferTest extends TestCase {
    private TimebasedRingBuffer<Object> trb;

    private Object o1;

    private Object o2;

    private Object o3;

    private Object o4;

    @Override
    public void setUp() {
        // create a buffer and some objects to play around with
        trb = new TimebasedRingBuffer<Object>(1000);

        o1 = new Object();
        o2 = new Object();
        o3 = new Object();
        o4 = new Object();

    }

    public void testCallsToEmptyBuffer() {
        assertNull("Empty buffer should return null", trb.peek());
        assertEquals("empty buffer should have size 0", 0, trb.size());
        assertNull("Empty buffer should return null after update", trb.update());
        assertTrue("Update to empty buffer should return null", trb.update() == null);
    }

    public void testBufferEmptyAfterMaxAge() {
        trb.add(o1, System.currentTimeMillis());
        sleep(1001);
        Object r = trb.update();
        assertTrue("Update should not return null", r != null);
        r = trb.update();
        assertTrue("Update to now empty buffer should return null", r == null);
    }
    
    public void testCallsOneElementWithTimeout() {
        assertNull("add to empty buffer should return null", trb.add(o1,System.currentTimeMillis()));
        sleep(500);

        assertNull("less than 1000ms passed, should return null", trb.update());
        sleep(600);

        List<Object> l = assertListSize(1);

        assertEquals("return object does not match", o1, l.get(0));
    }

    public void testAdd3WaitAdd1() {

        add3ObjectsAndSleep(500);

        // this one is newer and should remain in buffer after the update
        assertNull("add buffer 4 should return null", trb.add(o4,System.currentTimeMillis()));

        assertNull("less than 1000ms passed, should return null", trb.update());
        sleep(600);

        // only the 3 old objects should be returned 
        List<Object> l1 = assertListSize(3);

        // check if the right objects are returned
        assertEquals("return object does not match 1", o1, l1.get(0));
        assertEquals("return object does not match 2", o2, l1.get(1));
        assertEquals("return object does not match 3", o3, l1.get(2));

        sleep(500);

        List<Object> l2 = assertListSize(1);

        assertEquals("return object does not match 4", o4, l2.get(0));
    }

    public void testAdd3WaitLongAdd1() {

        add3ObjectsAndSleep(1100);

        // should return the 3 objects, because they are already timed out
        List<Object> l1 = trb.add(o4,System.currentTimeMillis());

        assertEquals("wrong list size", 3, l1.size());
        
        // check if the right objects are returned
        assertEquals("return object does not match 1", o1, l1.get(0));
        assertEquals("return object does not match 2", o2, l1.get(1));
        assertEquals("return object does not match 3", o3, l1.get(2));

        sleep(1100);

        List<Object> l2 = assertListSize(1);

        assertEquals("return object does not match 4", o4, l2.get(0));
    }
    
    private void add3ObjectsAndSleep(int n) {
        assertNull("add to empty buffer should return null", trb.add(o1,System.currentTimeMillis()));
        assertNull("add buffer 2 should return null", trb.add(o2,System.currentTimeMillis()));
        assertNull("add buffer 3 should return null", trb.add(o3,System.currentTimeMillis()));
        sleep(n);
    }

    private static void sleep(int n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            // ignore
        }
    }
    private List<Object> assertListSize(int size) {
        final List<Object> l = trb.update();
        assertNotNull("should return list of timed out object(s)", l);
        assertEquals("wrong list size", size, l.size());
        return l;
    }

}
