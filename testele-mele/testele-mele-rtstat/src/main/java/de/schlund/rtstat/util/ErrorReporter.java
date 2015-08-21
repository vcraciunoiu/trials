package de.schlund.rtstat.util;

import java.util.Map;

import org.apache.log4j.Logger;

public class ErrorReporter {
    private static Logger LOG = Logger.getLogger(ErrorReporter.class);
    private Sender sender;
    
    public void setSender(Sender s) {
        this.sender = s;
    }

    public  void sendException(Exception e, Map<String, String> m) {
        LOG.error("Sending Exception", e);
        try {
            sender.sendException(e, m);
        } catch (Exception ex) {
            LOG.fatal("Error when sending exception!", ex);
        }
    }

    public  void sendException(Exception e) {
        LOG.error("Sending Exception", e);
        try {
            sender.sendException(e);
        } catch (Exception ex) {
            LOG.fatal("Error when sending exception!", ex);
        }
    }
}


