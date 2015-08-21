package de.schlund.rtstat.test.xrtp;

import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.Controller;
import de.schlund.rtstat.statistics.processor.XRTP;
import de.schlund.rtstat.test.RtstatTestSuite;

public class XRTPTest3 extends TestCase {
    ApplicationContext ctx;
    Controller myController;

    @Override
    protected void setUp() throws Exception {
        ctx = new ClassPathXmlApplicationContext("bean.xml");
        myController = (Controller) ctx.getBean("controller");
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        myController.stopProcessors();
    }

    public void testTestdata04_2() throws Exception {
        System.out.println(getClass().getName());
        String testfile = "testdata/testdata04_2.txt";
        XRTP x = setup(testfile);

        assertTrue(x.getXRTP().getNrSamples() == 1);
        assertTrue(x.getXRTP().needWarning().size() > 0);
    }

    private XRTP setup(String testfile) throws IOException, InterruptedException {
        RtstatTestSuite.configureLogging();

        final XRTP xrtp_telefonica = (XRTP) ctx.getBean("xrtp_telefonica");
        myController = (Controller) ctx.getBean("controller");

        // SERLogProcessorFeeder reader = (SERLogProcessorFeeder)
        // ctx.getBean("filefeeder1");
        // ((SERLogFileReader) reader).setFile(testfile);

        myController.startProcessors();

        Thread.sleep(2000);
        return xrtp_telefonica;
    }

}
