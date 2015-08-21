package de.schlund.rtstat.statistics.model;

import de.schlund.rtstat.util.Util;

public class ASRData {

    private long timestamp;

    private short code;

    public short getCode() {
        return code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ASRData(short c, long t) {
        timestamp = t;
        code = c;
    }
    
    public String toString() {
        return "Hash["+hashCode()+"] Timestamp["+Util.readableTimestamp(timestamp)+"] " +
                "Code["+code+"]";
    }
}