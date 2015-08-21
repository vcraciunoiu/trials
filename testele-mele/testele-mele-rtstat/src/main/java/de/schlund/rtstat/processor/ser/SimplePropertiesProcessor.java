package de.schlund.rtstat.processor.ser;

import java.util.HashMap;
import java.util.regex.Pattern;

import de.schlund.rtstat.model.SERLogEvent;

public class SimplePropertiesProcessor implements IPropertiesProcessor {

    private HashMap<Pattern, String[]> _providerMap = new HashMap<Pattern, String[]>();
    private ProviderUtil util;
    
    public void addProvider(String regex, String name, String target) {
        _providerMap.put(Pattern.compile(regex), new String[] { name, target });
    }

    public void processEvent(SERLogEvent event) {
        util.setFromProvider(event);
        util.setToProvider(event);
    }
    
    public void setProviderUtil(ProviderUtil p) {
        this.util = p;
    }
}
