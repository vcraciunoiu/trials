/*
 * Created on 31.05.2007
 * by sonja
 */
package de.schlund.rtstat.monitoring;

import java.net.URL;

/**
 * This interface contains constants used to configure the quality monitoring tool
 * @author Sonja Pieper <spieper@schlund.de>
 */
public interface Configuration {
        
    public static final String VOIP_DATA_URL = "http://sieber.schlund.de:1234/statsalyzer ";

    public static final String[] MONITORED_FIELDS = {
        "asr_p50_Telefonica.count",
        "asr_p50_Broadnet.count"         
    };
    
    /**
     * The values of the checked fields should not fall below this value 
     */
    public static final double QUALITY_TRESHOLD = 600; 
    
    /**
     * The number of intervalls that the quality must be below the quality_treshold to generate a 
     * warning email
     */
    public static final int WARN_TRESHOLD = 3;
    
    /**
     * The intervall for the monitoring task in seconds
     */
    public static final int MONITORING_INTERVAL = 15;
    
    /**
     * Who to send the warnings to. 
     */
    public static final String[] WARN_EMAIL = { "joerg.haecker@1und1.de" };
    
    URL getDataUrl();
    String[] getMonitoredFields();
    double getQualityTreshold();
    int getWarnTreshold();
    int getMonitoringInterval();
    String[] getWarningRecipients();
}
