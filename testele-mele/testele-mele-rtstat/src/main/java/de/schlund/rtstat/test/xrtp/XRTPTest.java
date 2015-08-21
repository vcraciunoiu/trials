package de.schlund.rtstat.test.xrtp;

import junit.framework.Test;
import junit.framework.TestSuite;

public class XRTPTest extends TestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(XRTPTest1.class);
        suite.addTestSuite(XRTPTest2.class);
        suite.addTestSuite(XRTPTest3.class);
        return suite;
    }
   
}
