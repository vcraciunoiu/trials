package de.schlund.rtstat.test.model;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.Controller;
import de.schlund.rtstat.processor.cdr.CDRSQLFileWriterProcessor;
import de.schlund.rtstat.processor.ser.AsyncPropertiesProcessor;
import de.schlund.rtstat.processor.ser.ProviderUtil;
import de.schlund.rtstat.processor.ser.SER2CDRProcessor;
import de.schlund.rtstat.startpoint.MultiThreadedBlockingSLogEventListener;
import de.schlund.rtstat.statistics.processor.ASR;
import de.schlund.rtstat.statistics.processor.XRTP;
import de.schlund.rtstat.test.RtstatTestSuite;

public class SQLCdrTest extends TestCase {
    private static final String testfile = "testdata/testdata09.txt";
    private static ApplicationContext ctx = null;
    public void testTest01() throws Exception {
    
        final XRTP xrtp_bt = (XRTP) ctx.getBean("xrtp_bt");
        final XRTP xrtp_telefonica = (XRTP) ctx.getBean("xrtp_telefonica");
        final XRTP xrtp_broadnet = (XRTP) ctx.getBean("xrtp_broadnet");
        final ASR asr = (ASR) ctx.getBean("asr");
        
        
        final CDRSQLFileWriterProcessor cdrsql = (CDRSQLFileWriterProcessor) ctx.getBean("cdrsql");
        final SER2CDRProcessor ser2cdr = (SER2CDRProcessor) ctx.getBean("ser2cdr");
        final AsyncPropertiesProcessor pp = (AsyncPropertiesProcessor) ctx.getBean("asyncpp");
        final MultiThreadedBlockingSLogEventListener feeder = (MultiThreadedBlockingSLogEventListener) ctx.getBean("feeder");
        
        final Controller myController = (Controller) ctx.getBean("controller");
        final ProviderUtil util = (ProviderUtil) ctx.getBean("propertyutil");
        
        
    }
    protected void setUp() throws Exception {
        RtstatTestSuite.configureLogging();
        ctx = new ClassPathXmlApplicationContext("bean.xml");
        ctx.getBean("errorreporter");
        super.setUp();
    }
}
