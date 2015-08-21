package de.schlund.rtstat.util;

import java.util.Map;

public interface Sender {
    public void sendException(Exception e, Map<String, String> m);
    public void sendException(Exception e);
}
