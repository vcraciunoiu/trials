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

public class XRTPTest extends TestCase {
    private XRTP setup(String testfile) throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");
        
        final XRTP xrtp_bt = (XRTP) ctx.getBean("xrtp_bt");
        final XRTP xrtp_telefonica = (XRTP) ctx.getBean("xrtp_telefonica");
        final XRTP xrtp_broadnet = (XRTP) ctx.getBean("xrtp_broadnet");
        final ASR asr = (ASR) ctx.getBean("asr");
       
        
        final CDRSQLFileWriterProcessor cdrsql = (CDRSQLFileWriterProcessor) ctx.getBean("cdr2sql");
        final SER2CDRProcessor ser2cdr = (SER2CDRProcessor) ctx.getBean("ser2cdr");
        final AsyncPropertiesProcessor pp = (AsyncPropertiesProcessor) ctx.getBean("asyncpp");
        final MultiThreadedBlockingSLogEventListener feeder = (MultiThreadedBlockingSLogEventListener) ctx.getBean("feeder");
        
        final Controller myController = (Controller) ctx.getBean("controller");
        final ProviderUtil util = (ProviderUtil) ctx.getBean("propertyutil");
        
        Thread.currentThread().sleep(1000);
        
        return xrtp_telefonica;
    }
    
    public void testTestdata04() throws Exception {
        String testfile = "testdata/testdata04.txt";
        XRTP  x = setup(testfile);
       /* System.out.println("***"+x);
        Thread.sleep(30000);*/
        //System.out.println(ToStringBuilder.reflectionToString(x.getXRTP()));
        assertTrue(x.getXRTP().getNrSamples() == 1);
        assertTrue(x.getXRTP().getAvgJitter() == 360);
    }
    
    /*public void testTestdata04_1() throws Exception {
        String testfile = "testdata/testdata04_1.txt";
        XRTP  x = setup(testfile);
    
        assertTrue(x.getXRTP().getNrSamples() == 1);
        assertTrue(x.getXRTP().getAvgJitter() == 0);
        assertTrue(x.getXRTP().needWarning().isEmpty());
    }*/
    
   /* public void testTestdata04_2() throws Exception {
        String testfile = "testdata/testdata04_2.txt";
        XRTP  x = setup(testfile);
   
        assertTrue(x.getXRTP().getNrSamples() == 1);
        assertTrue(x.getXRTP().needWarning().size() > 0);
    }*/


}
