/*
 * Created on 31.05.2007
 * by sonja
 */
package de.schlund.rtstat.monitoring;

import java.net.MalformedURLException;
import java.net.URL;

public class ConfigurationImpl implements Configuration {

    public URL getDataUrl() {
        try {
            return new URL(VOIP_DATA_URL);
        } catch (MalformedURLException e) {
            //oh puleeeze
            return null;
        }
    }

    public String[] getMonitoredFields() {
        return MONITORED_FIELDS;
    }

    public int getMonitoringInterval() {
        return MONITORING_INTERVAL;
    }

    public double getQualityTreshold() {
        return QUALITY_TRESHOLD;
    }

    public int getWarnTreshold() {
        return WARN_TRESHOLD;
    }

    public String[] getWarningRecipients() {
        return WARN_EMAIL;
    }

}
