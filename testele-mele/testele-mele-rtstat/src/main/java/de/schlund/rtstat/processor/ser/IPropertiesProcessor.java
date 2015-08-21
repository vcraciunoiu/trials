package de.schlund.rtstat.processor.ser;

import de.schlund.rtstat.model.SERLogEvent;

public interface IPropertiesProcessor {
    public void processEvent(SERLogEvent event);
}
