package de.schlund.rtstat.test.asr;

import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.Controller;
import de.schlund.rtstat.startpoint.SERLogFileReader;
import de.schlund.rtstat.startpoint.SERLogProcessorFeeder;
import de.schlund.rtstat.statistics.model.ASRCallData;
import de.schlund.rtstat.statistics.processor.ASR;
import de.schlund.rtstat.test.RtstatTestSuite;
import de.schlund.rtstat.util.Constants;

public class ASRTest3 extends TestCase {
    ApplicationContext ctx;
    Controller myController;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = new ClassPathXmlApplicationContext("bean.xml");
        myController = (Controller) ctx.getBean("controller");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        myController.stopProcessors();
    }

    public void testTestdata03() throws Exception {
        System.out.println(getClass().getName());
        String testfile = "testdata/testdata03.txt";

        String cPref = "4926514226";
        String calleePrefix = "49265142267";
        String calleePrefix1 = "49265142266";

        ASR asr = setup(testfile, new String[] { calleePrefix, calleePrefix1 });

        ASRCallData cd = asr.getASRTree().getTreeNode(Constants.PROVIDER_QSC).getASRCallData(calleePrefix);
        assertEquals(3, cd.getAllCalls());
        assertEquals(1, cd.getGoodCalls());
        assertEquals(333, cd.getRatio());
        assertEquals(666, cd.get486Ratio());

        ASRCallData cd1 = asr.getASRTree().getTreeNode(Constants.PROVIDER_QSC).getASRCallData(calleePrefix1);
        assertEquals(2, cd1.getAllCalls());
        assertEquals(1, cd1.getGoodCalls());
        assertEquals(500, cd1.getRatio());
        assertEquals(500, cd1.get486Ratio());

        ASRCallData cd3 = asr.getASRTree().getTreeNode(Constants.PROVIDER_QSC).collectASRCallData(cPref);
        assertEquals(5, cd3.getAllCalls());
        assertEquals(2, cd3.getGoodCalls());
        assertEquals(400, cd3.getRatio());
        assertEquals(600, cd3.get486Ratio());
    }

    private ASR setup(String testfile, String calleePrefix[]) throws IOException, InterruptedException {
        RtstatTestSuite.configureLogging();
        
        final ASR asr = (ASR) ctx.getBean("asr");
        for (int i = 0; i < calleePrefix.length; i++) {
            asr.getASRTree().addPrefix(Constants.PROVIDER_QSC, calleePrefix[i], true);
            asr.getASRTree().addPrefix(Constants.PROVIDER_TELEFONICA, calleePrefix[i], true);
        }

        SERLogProcessorFeeder reader = (SERLogProcessorFeeder)ctx.getBean("filefeeder");
        ((SERLogFileReader) reader).setFile(testfile);

        for (String provider : Constants.PROVIDER_LIST) {
            Controller.addPrefixes(asr, provider, "49");
        }

        myController.startProcessors();

        Thread.sleep(2000);
        return asr;
    }
}
