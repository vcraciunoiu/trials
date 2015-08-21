package de.schlund.rtstat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.db.DbConnection;
import de.schlund.rtstat.monitoring.StatsalyzerServlet;
import de.schlund.rtstat.processor.ProcessorThread;
import de.schlund.rtstat.statistics.StatsHttpClient;
import de.schlund.rtstat.statistics.processor.ASR;
import de.schlund.rtstat.statistics.processor.XRTP;
import de.schlund.rtstat.util.Constants;
import de.schlund.rtstat.util.ErrorReporter;
import de.schlund.rtstat.util.MonitoringThread;
import de.schlund.rtstat.util.TablesCreatorThread;

/**
 * @author mic
 * @author Frank Spychalski (<a
 *         href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class Controller {

  private final static Logger LOG = Logger.getLogger(Controller.class);

  private Map<String, ProcessorThread> _processorList = new HashMap<String, ProcessorThread>();

  private ASR _asr = null;

  private List<XRTP> _xrtp = new ArrayList<XRTP>();

  private ErrorReporter reporter;

  private String aliveFile;

  private int httpInterfacePort;

  private int oldTableExpiration;

  public Controller() {
  }

  public void setProcessors(List<ProcessorThread> t) {
    System.out.println("Processors:" + t);
    for (ProcessorThread p : t) {
      registerProcessor(p);
    }
  }

  public void registerProcessor(ProcessorThread processor) {
    if (_processorList.containsKey(processor.getName())) {
      System.out.println("Processor already exists:" + processor.getName());
      return;
    }
    LOG.info("Register processor:" + processor.getClass().getName());
    _processorList.put(processor.getName(), processor);

    // there should be only one ASR
    if (processor instanceof ASR) {
      _asr = (ASR) processor;
    }

    if (processor instanceof XRTP) {
      final XRTP xrtp2 = ((XRTP) processor);
      _xrtp.add(xrtp2);
    }
  }

  private void startupWebserver() {
    final Server server = new Server();

    try {
      server.addListener(":" + httpInterfacePort);
      StatsalyzerServlet.setXRTPs(_xrtp);
      StatsalyzerServlet.setASR(_asr);

      final HttpContext context = server.getContext("/");
      final ServletHandler handler = new ServletHandler();
      handler.addServlet("STATSALYZER", "/statsalyzer/*", StatsalyzerServlet.class.getName());

      context.addHandler(handler);
      server.start();
    } catch (Exception e) {
      LOG.error("Exception ", e);
      ErrorReporter rep = new ErrorReporter();
      rep.sendException(e);
    }
  }

  public static void main(String[] args) {
    LOG.info("Starting RTStat");
    ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");
    final Controller myController = (Controller) ctx.getBean("controller");
    myController.setup(ctx);
  }

  private void setup(ApplicationContext ctx) {
    LOG.info("Controller setup");
    try {
      reporter = (ErrorReporter) ctx.getBean("errorreporter");

      final ASR asr = (ASR) ctx.getBean("asr");
      addPrefixes(asr, Constants.PROVIDER_TELEFONICA, "");
      addPrefixes(asr, Constants.PROVIDER_QSC, "");
      addPrefixes(asr, Constants.PROVIDER_VODAFONE, "");
      addPrefixes(asr, Constants.PROVIDER_VERIZON, "");
      addPrefixes(asr, Constants.PROVIDER_COLT, "");

      addEmergencyPrefixes(asr, Constants.PROVIDER_QSC, Constants.EMERGENCYPREFIX_QSC_1);
      addEmergencyPrefixes(asr, Constants.PROVIDER_QSC, Constants.EMERGENCYPREFIX_QSC_2);
      addEmergencyPrefixes(asr, Constants.PROVIDER_TELEFONICA, Constants.EMERGENCYPREFIX_TELEFONICA);

      addEmergencyPrefixes(asr, Constants.PROVIDER_TELEFONICA, asr.getCon1251Filter());
      addEmergencyPrefixes(asr, Constants.PROVIDER_QSC, asr.getCon1251Filter());
      addEmergencyPrefixes(asr, Constants.PROVIDER_VODAFONE, asr.getCon1251Filter());
      addEmergencyPrefixes(asr, Constants.PROVIDER_VERIZON, asr.getCon1251Filter());
      addEmergencyPrefixes(asr, Constants.PROVIDER_COLT, asr.getCon1251Filter());

      // emergency calls are not yet tracked for Arcor because of the problem
      // with alpha-numerical chars in URI user
      addEmergencyPrefixes(asr, Constants.PROVIDER_VODAFONE, "");

      // this is not really for adding a prefix;
      // it is actually for adding a treeNode for data that does not
      // depend on a particular provider (mobile, abroad, announcements)
      asr.getASRTree().addPrefix(Constants.OTHERS, Constants.OTHERS_PREFIX, true);

      LOG.info("before run");
      ((Controller) ctx.getBean("controller")).run(ctx);
      LOG.info("finished");
    } catch (Exception e) {
      LOG.error("Exception in main", e);
      ErrorReporter rep = new ErrorReporter();
      rep.sendException(e);
    }
  }

  private void run(ApplicationContext ctx) throws IOException, SQLException {
    startProcessors();
    startupWebserver();

    StatsHttpClient.set_xrtp(_xrtp);
    StatsHttpClient.set_asr(_asr);

    Thread monitoringThread = new Thread(new MonitoringThread(_asr, _xrtp, aliveFile));
    monitoringThread.setPriority(Thread.MIN_PRIORITY);
    monitoringThread.start();

    DbConnection dbconn = (DbConnection) ctx.getBean("dbconnection");
    Thread tablesCreatorThread = new Thread(new TablesCreatorThread(dbconn, oldTableExpiration));
    tablesCreatorThread.setPriority(Thread.MIN_PRIORITY);
    tablesCreatorThread.start();
  }

  public void startProcessors() {
    System.out.println(this.hashCode() + " Starting processors");
    for (String s : _processorList.keySet()) {
      ProcessorThread t = _processorList.get(s);

      System.out.println("Starting processor:" + s + "->" + t.getClass().getName());
      if (LOG.isInfoEnabled()) {
        LOG.info("Starting processor:" + s + "->" + t.getClass().getName());
      }
      t.startThread();
    }
  }

  public void stopProcessors() {
    System.out.println(this.hashCode() + " Stopping processors");
    for (String s : _processorList.keySet()) {
      ProcessorThread t = _processorList.get(s);
      System.out.println("Stopping processor:" + s + "->" + t.getClass().getName());
      if (LOG.isInfoEnabled()) {
        LOG.info("Stopping processor:" + s + "->" + t.getClass().getName());
      }
      t.stopThread();
    }
  }

  public ErrorReporter getReporter() {
    return reporter;
  }

  public void setErrorReporter(ErrorReporter rep) {
    this.reporter = rep;
  }

  public static final int BUFFER_DURATION_6_MINUTES = 6 * 60 * 1000;

  public static void addPrefixes(ASR asr, String provider, String prefixprefix) {
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        asr.getASRTree().addPrefix(provider, prefixprefix + i + j, true);
      }
    }
  }

  /**
   * 
   * Add the emergency prefixes for generating statistics for emergency calls
   * 
   * @param asr
   * @param provider
   * @param emergencyprefix
   */
  public static void addEmergencyPrefixes(ASR asr, String provider, String emergencyprefix) {
    asr.getASRTree().addPrefix(provider, emergencyprefix, true);
  }

  public void setAliveFile(String aliveFile) {
    this.aliveFile = aliveFile;
  }

  public void setHttpInterfacePort(int port) {
    this.httpInterfacePort = port;
  }

  public void setOldTableExpiration(int oldTableExpiration) {
    this.oldTableExpiration = oldTableExpiration;
  }

}
