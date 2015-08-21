/*
 * Created on 31.05.2007
 * by sonja
 */
package de.schlund.rtstat.monitoring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.MessagingException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * This class contains a small tool that regularly requests the statistical data for the statsalyzer
 * from sieber and monitors a few values (@see Configuration#MONITORED_FIELDS). If the values
 * in these fields drop below a certain treshold a warning email will be send.
 * 
 * @see Configuration
 * @author Sonja Pieper <spieper@schlund.de>
 */
public class VoipQualityMonitor {

    static final Logger LOG = Logger.getLogger(VoipQualityMonitor.class);
    
    private Timer vqmTimer;
    private Configuration config;
    
    public VoipQualityMonitor(Configuration config) {
        LOG.info("[Starting]");        
        this.config = config;
        this.vqmTimer = new Timer("VQM-Timer");   
        LOG.debug("Created VQM-Timer");
    }

    public void startMonitoring()  {        
        this.vqmTimer.scheduleAtFixedRate(new MonitoringTask(this.config), 1000, this.config.getMonitoringInterval()*1000);
        LOG.debug("Scheduled monitoring task");            
    }
    
    /* static helpers */
    
    private static void configureLogging() throws IOException {        
        LOG.addAppender( new DailyRollingFileAppender( 
                new PatternLayout("%d{ISO8601} [%t] %-5p %c [%M():%L] %x - %m\n"),
                "vqm.log",
                "yyyy-MM-dd" ) );
        LOG.setLevel( Level.DEBUG );
    }
    
    /* main method */

    public static void main(String[] args) {
        try {     
            VoipQualityMonitor.configureLogging();
            final VoipQualityMonitor vqm = new VoipQualityMonitor(new ConfigurationImpl());
            vqm.startMonitoring();                       
        } catch (Exception e) {
            LOG.error("VQM failed, you will no longer be notified of problems",e);
        }
    }

    /* inner class */

    private static class MonitoringTask extends TimerTask {

        private EmailSender sender = new EmailSender();
        private URLRequest urlRequest;    
        private Map<String,Integer> monitoringCount = new HashMap<String,Integer>();
        private int warnTreshold;
        private double qualityTreshold;
        private String recipient;
        
        public MonitoringTask(Configuration config) {
            this.urlRequest = new URLRequest(config.getDataUrl());
            this.warnTreshold = config.getWarnTreshold();
            this.qualityTreshold = config.getQualityTreshold();
            this.recipient = config.getWarningRecipients()[0];
            for(String key : config.getMonitoredFields()) {
                this.monitoringCount.put(key, 0);
            }
        }

        private Map<String,Double> createMap(String fetchString) {            
            final Map<String,Double> result = new HashMap<String,Double>();
            final String[] pairs = fetchString.split("\n");
            for (int i = 0; i < pairs.length; i++) {
                if(pairs[i].length()>0 && pairs[i].indexOf("=")>0) {
                    final String[] pair = pairs[i].split("=");
                    result.put(pair[0],new Double(pair[1].trim()));
                } else {                   
                    LOG.warn("Empty or malformed line -"+pairs[i]+"-");
                }
            }
            return result;
        }    

        private String updateCounts(final Map<String, Double> resultMap) {
            final StringBuffer report = new StringBuffer();            
            for(String key : this.monitoringCount.keySet()) {
                double currentValue = resultMap.get(key);
                final int newCount;
                if(currentValue<this.qualityTreshold) {
                    LOG.debug("Treshold missed: "+key+"="+currentValue);      
                    final int currentMonitoringCount = this.monitoringCount.get(key);
                    if(currentMonitoringCount+1>=this.warnTreshold) {            
                        LOG.info("Report needed for "+key+", resetting monitoring count");
                        report.append("Warning for ").append(key).append("=").append(resultMap.get(key)).append("\n");
                        newCount = 0;
                    } else {
                        newCount = currentMonitoringCount+1;
                    }
                } else {
                    LOG.debug(key+"="+currentValue);
                    newCount = 0;
                }
                this.monitoringCount.put(key, newCount);
            }
            return report.toString();
        }

       
        @Override
        public void run() {
            try {
                LOG.info("[Running]");
                final String httpResult = this.urlRequest.read();  //fetch current statistics
                final Map<String,Double> resultMap = this.createMap(httpResult); //make a map for easy handling           
                final String report = this.updateCounts(resultMap); //check the values 
                if(report.length()>0) {
                    LOG.info("Sending report");                                        
                    this.sender.sendEmail(report, "[VQM] Warning",this.recipient);                
                } 
                LOG.debug("[Done]");
            } catch (IOException e) {
                LOG.error("Could not retrieve data",e);
            } catch (MessagingException e) {
                LOG.error("Could not send email",e);
            }
        }


    }

}
