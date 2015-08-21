package de.schlund.rtstat.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import de.schlund.rtstat.test.asr.ASRTest1;
import de.schlund.rtstat.test.asr.ASRTest2;
import de.schlund.rtstat.test.asr.ASRTest3;
import de.schlund.rtstat.test.asr.ASRTest5;
import de.schlund.rtstat.test.asr.ASRTest10;
import de.schlund.rtstat.test.model.CDRTest;
import de.schlund.rtstat.test.model.RegExTest;
import de.schlund.rtstat.test.model.SERLogEventTest;
import de.schlund.rtstat.test.model.XRTPParserTest;
import de.schlund.rtstat.test.xrtp.XRTPTest1;
import de.schlund.rtstat.test.xrtp.XRTPTest2;
import de.schlund.rtstat.test.xrtp.XRTPTest3;

/**
 * @author <a href="mailto:rapude@schlund.de">Ralf Rapude</a> Date: 09/08/2006
 *         Time: 1:52:21 PM
 */
public class RtstatTestSuite extends TestSuite {
    static int eins = 1;

    public static void configureLogging() {
        if (eins == 1) {
            final PatternLayout layout = new PatternLayout("%d{ISO8601} [%-5p] %c %m\n");
            Logger.getRootLogger().addAppender(new ConsoleAppender(layout));
            Logger.getRootLogger().setLevel(Level.INFO);
            Logger.getRootLogger().setAdditivity(false);
        }
        eins++;
    }

    public static Test suite() {
        configureLogging();
        TestSuite suite = new TestSuite();

        suite.addTestSuite(ASRTest1.class);
        suite.addTestSuite(CDRTest.class);

        // cannot run these tests;
        // there is a problem with calls != 200, they are kept 3 hours in
        // SER2CDR mating buffer.
        // suite.addTestSuite(ASRTest2.class);
        // suite.addTestSuite(ASRTest3.class);

//        suite.addTestSuite(ASRTest5.class);
//        suite.addTestSuite(ASRTest10.class);
//
//        suite.addTestSuite(XRTPTest1.class);
//        suite.addTestSuite(XRTPTest2.class);
//        suite.addTestSuite(XRTPTest3.class);
//
//        suite.addTestSuite(SERLogEventTest.class);
//        suite.addTestSuite(RegExTest.class);
//        suite.addTestSuite(XRTPParserTest.class);

        return suite;
    }
}
