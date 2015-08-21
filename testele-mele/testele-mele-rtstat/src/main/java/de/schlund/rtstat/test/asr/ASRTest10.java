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

public class ASRTest10 extends TestCase {
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

    public void testTestdata_notruf() throws Exception {
        System.out.println(getClass().getName());
        String testfile = "testdata/testdata10.txt";
        String calleePrefix = Constants.EMERGENCYPREFIX_QSC_1; // 99911

        ASR asr = setup(testfile, calleePrefix);

        // whole tree for qsc
        ASRCallData tree_cd = asr.getASRTree().getTreeNode(Constants.PROVIDER_QSC).collectASRCallData();
        assertEquals(3, tree_cd.getAllCalls());
        assertEquals(3, tree_cd.getGoodCalls());
        assertEquals(1000, tree_cd.getRatio());

        // node for emergency calls
        ASRCallData cd = asr.getASRTree().getTreeNode(Constants.PROVIDER_QSC).getASRCallData(calleePrefix);
        assertEquals(2, cd.getAllCalls());
        assertEquals(2, cd.getGoodCalls());
        assertEquals(1000, cd.getRatio());
    }

    private ASR setup(String testfile, String calleePrefix) throws IOException, InterruptedException {
        RtstatTestSuite.configureLogging();
        final ASR asr = (ASR) ctx.getBean("asr");

        asr.getASRTree().addPrefix(Constants.PROVIDER_QSC, calleePrefix, true);

        SERLogProcessorFeeder reader = (SERLogProcessorFeeder)ctx.getBean("filefeeder");
        ((SERLogFileReader) reader).setFile(testfile);

        Controller.addPrefixes(asr, Constants.PROVIDER_QSC, "49");
        Controller.addEmergencyPrefixes(asr, Constants.PROVIDER_QSC, calleePrefix);

        myController.startProcessors();

        Thread.sleep(2000);
        return asr;
    }
}
