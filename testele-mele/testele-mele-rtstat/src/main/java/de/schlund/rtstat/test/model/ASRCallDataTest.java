package de.schlund.rtstat.test.model;

import de.schlund.rtstat.statistics.model.ASRCallData;
import de.schlund.rtstat.statistics.model.ASRData;
import junit.framework.TestCase;

public class ASRCallDataTest extends TestCase {

    private ASRCallData data;

    private static final ASRData CODE_200 = new ASRData((short) 200, 123);

    private static final ASRData CODE_401 = new ASRData((short) 401, 123);
    private static final ASRData CODE_402 = new ASRData((short) 402, 123);
    private static final ASRData CODE_408 = new ASRData((short) 408, 123);

    private static final ASRData CODE_486 = new ASRData((short) 486, 123);

    private static final ASRData CODE_487 = new ASRData((short) 487, 123);

    private static final ASRCallData DATA_EMPTY = new ASRCallData("unittest");

    @Override
    public void setUp() {
        data = new ASRCallData("unittest");
    }

    public void testRemoveFromEmpty() {
        data.remove(CODE_200);
        assertEqualsNoTS(DATA_EMPTY, data);
    }

    public void testAddAndRemove() {
        for (int i = 1; i < 100; i++) {
            data.insert(CODE_200);
        }
        for (int i = 1; i < 50; i++) {
            data.remove(CODE_200);
        }
        ASRCallData expected = new ASRCallData(50, 0, 0, 0,0, 50, 0);

        assertEqualsNoTS(expected, data);
    }

    private void assertEqualsNoTS(ASRCallData expected, ASRCallData actual) {
        assertTrue("expected " + expected.toString() + " but was " + actual.toString(), expected
                .equalsNoTimestamp(actual));
    }

    public void testManyAddRemoves() {
        for (int i = 0; i < 100; i++) {
            data.insert(CODE_200);
            data.insert(CODE_408);
            data.insert(CODE_486);
            data.insert(CODE_487);
        }
        data.insert(new ASRData((short) 123, 2222));

        for (int i = 0; i < 100; i++) {
            data.remove(CODE_200);
            data.remove(CODE_408);
            data.remove(CODE_486);
            data.remove(CODE_487);
        }
    }
    
    public void testGetBlockRatio() {
        data.insert(CODE_401);
        data.insert(CODE_402);
        data.insert(CODE_486);
        data.insert(CODE_487);
        
        assertEquals(250,data.get486Ratio());
        assertEquals(1000, data.getBlockRatio(4));
        
       for (int i = 0; i < 36; i++) {
           data.insert(CODE_200);
       }
       assertEquals(25,data.get486Ratio());
       assertEquals(100, data.getBlockRatio(4));
    }
}
