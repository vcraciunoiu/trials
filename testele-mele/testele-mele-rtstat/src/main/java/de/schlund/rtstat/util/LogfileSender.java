package de.schlund.rtstat.util;

import java.util.Map;

import org.apache.log4j.Logger;

public class LogfileSender implements Sender {
    private static Logger LOG = Logger.getLogger(LogfileSender.class);
    
    public void sendException(Exception e, Map<String, String> m) {
        LOG.error("Exception: "+m, e);
    }
    
    public void sendException(Exception e) {
        LOG.error("Exception: ", e);
    }

}
