package de.schlund.rtstat.test.model;

import junit.framework.TestCase;
import de.schlund.rtstat.statistics.model.ASRCallData;
import de.schlund.rtstat.statistics.model.ASRTreeNode;

public class ASRTreeNodeTest extends TestCase {
    ASRTreeNode atn1;

    long now;

    @Override
    public void setUp() {
        atn1 = new ASRTreeNode(1000, '0', "unittest");
        atn1.build("1", false);
        now = System.currentTimeMillis();
    }

    public void testErrorhandling() {

        try {
            atn1.getASRCallData(null);
            fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // this should happen!
        }
    }

    public void testInsertEvents() {
        atn1.insert("", (short) 200, now);
        assertEquals(1, atn1.getBuffersize());
        ASRCallData expected = new ASRCallData(1, 0, 0, 0, 0, 1, now);
        ASRCallData returned = atn1.collectASRCallData();
        assertEquals(expected, returned);

        // insert another 50 successful events
        for (int i = 0; i < 50; i++) {
            atn1.insert("", (short) 200, now + i);
        }
        // and 100 unsuccessful events
        for (int i = 0; i < 100; i++) {
            atn1.insert("", (short) 404, now + 50 + i);
        }

        assertEquals(151, atn1.getBuffersize());
        ASRCallData expected2 = new ASRCallData(51, 0, 0, 0, 0, 151, now);
        ASRCallData returned2 = atn1.collectASRCallData();
        assertEquals(expected2, returned2);

        for (int i = 0; i < 50; i++) {
            atn1.insert("1", (short) 200, now + i);
        }

        assertEquals(201, atn1.getBuffersize());

        ASRCallData expected3 = new ASRCallData(51, 0, 0, 0, 0, 151, now);
        ASRCallData returned3 = atn1.getASRCallData();
        assertEquals(expected3, returned3);

        ASRCallData expected4 = new ASRCallData(101, 0, 0, 0, 0, 201, now);
        ASRCallData returned4 = atn1.collectASRCallData();
        assertEquals(expected4, returned4);

    }

    public void testInsertEventsWithChildren() {
        for (int i = 0; i < 50; i++) {
            atn1.insert("1", (short) 200, now + i);
        }

        assertEquals(50, atn1.getBuffersize());

        for (int i = 0; i < 20; i++) {
            atn1.insert("", (short) 404, now + i);
        }
        assertEquals(70, atn1.getBuffersize());
    }

    public void testToViewableString() {

        String[] root = atn1.toViewableString(23).get(0);
        String out = root[1];
        assertEquals("Should end with ' 0' but was '" + out + "'", out.substring(out.length() - 2), " 0");
        String[] child = atn1.toViewableString(23).get(1);
        String outchild = child[1];
        assertEquals("Should end with ' 01' but was '" + outchild + "'", outchild.substring(outchild.length() - 3),
                " 01");
    }
}
