package de.schlund.rtstat.util;

import java.util.Map;

import org.apache.log4j.Logger;

//import de.schlund.fehlertool.client.ExceptionSender;

public class FehlertoolSender implements Sender {
    private static Logger LOG = Logger.getLogger(FehlertoolSender.class);

    public void sendException(Exception e, Map<String, String> m) {
        LOG.error(e);
//        ExceptionSender.send(e, m);
    }
    
    public void sendException(Exception e) {
        LOG.error(e);
//        ExceptionSender.send(e);
    }

}
