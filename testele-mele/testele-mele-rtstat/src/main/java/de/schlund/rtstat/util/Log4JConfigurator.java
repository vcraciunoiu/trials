package de.schlund.rtstat.util;

import java.net.URL;

import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.xml.DOMConfigurator;

public class Log4JConfigurator implements Configurator {
   

    public void doConfigure(URL url, LoggerRepository arg1) {
        long watchDelaySeconds = 30;
        DOMConfigurator.configureAndWatch(url.getPath(), watchDelaySeconds * 1000);
 
        org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
        logger.info("log4j configured using " + url);
        logger.info("log4j watch-delay is " + watchDelaySeconds + "s");
        
    }
}
