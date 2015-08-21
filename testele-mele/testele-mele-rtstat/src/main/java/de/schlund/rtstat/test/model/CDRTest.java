package de.schlund.rtstat.test.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.Controller;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.processor.Processor;
import de.schlund.rtstat.processor.ser.AsyncPropertiesProcessor;
import de.schlund.rtstat.startpoint.SERLogFileReader;
import de.schlund.rtstat.startpoint.SERLogProcessorFeeder;
import de.schlund.rtstat.test.RtstatTestSuite;

public class CDRTest extends TestCase {
    ApplicationContext ctx;
    Controller myController;
    Statement statement;
    
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

    public void testTestdataCdr() throws Exception {
        String testfile = "testdata/testdata_cdr.txt";
        setup(testfile);
        int nrRecords = getNrOfInsertedRecords();
        assertEquals(2, nrRecords);
    }

    private int getNrOfInsertedRecords() throws SQLException {
        int nrRecords = 0;        
        String selectString = "select count(*) from cdr_20091229";
        ResultSet rs = statement.executeQuery(selectString);
        while (rs.next()) {
            nrRecords = rs.getInt(1);
        }
        rs.close();
        return nrRecords;
    }
    
    private void setup(String testfile) throws IOException, InterruptedException {
        RtstatTestSuite.configureLogging();
        
        SERLogProcessorFeeder reader = (SERLogProcessorFeeder)ctx.getBean("filefeeder");
        ((SERLogFileReader) reader).setFile(testfile);
        
        // "CurrentTimeStampProcessor" is only necessary for ASR&XRTP tests, so we skip it for db tests; 
        AsyncPropertiesProcessor asyncpp = (AsyncPropertiesProcessor)ctx.getBean("asyncpp");
        java.util.List<Processor<SERLogEvent>> list = new ArrayList<Processor<SERLogEvent>>();
        list.add(asyncpp);
        reader.removeAllconsumers();
        reader.setConsumer(list);

        // open another connection to db in order to check the results
        try {
            String driverName = "com.mysql.jdbc.Driver";
            Class.forName(driverName);
            String url = "jdbc:mysql://localhost:3306/rtstat?autoReconnect=true";
            Connection connection = DriverManager.getConnection(url, "rtstat", "rtstat");
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            statement = connection.createStatement();
            String selectString = "truncate table cdr_20091229";
            statement.executeQuery(selectString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        myController.startProcessors();

        Thread.sleep(25000);
    }
}
