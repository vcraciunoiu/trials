package de.schlund.rtstat.model;


public enum Method {
    INVITE, ACK, BYE, REGISTER, OPTION, SUBSCRIBE, PRACK, INFO;

    public static String getRegex() {
        StringBuffer result = new StringBuffer("");
        for (Method m : Method.values()) {
            result.append("|").append(m.toString());
        }
        return result.toString();
    }
}