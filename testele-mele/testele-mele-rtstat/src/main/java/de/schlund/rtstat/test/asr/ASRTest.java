package de.schlund.rtstat.test.asr;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ASRTest extends TestSuite {
    
    public static Test suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(ASRTest1.class);
        suite.addTestSuite(ASRTest2.class);
        suite.addTestSuite(ASRTest3.class);
        suite.addTestSuite(ASRTest5.class);
        suite.addTestSuite(ASRTest10.class);
        return suite;
    }
   
}
