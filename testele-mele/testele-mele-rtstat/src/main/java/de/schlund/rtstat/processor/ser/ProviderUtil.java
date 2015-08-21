package de.schlund.rtstat.processor.ser;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.schlund.rtstat.db.DbConnection;
import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.util.Constants;

public class ProviderUtil {
    private static Logger LOG = Logger.getLogger(ProviderUtil.class);

    public static final String A_USER = "A-User";
    public static final String A_PROVIDER = "A-Provider";
    public static final String TARGET = "Target";
    public static final String B_USER = "B-User";
    public static final String B_PROVIDER = "B-Provider";
    private static String NA = "n/a";

    private static Map<String, String> provmap = new HashMap<String, String>();

    private DbConnection dbConnection;

    public void setToProvider(SERLogEvent e) {
        String user = e.getOURI().getUser();
        if (user != null) {
            if (user.startsWith(Constants.TAL_RSP)) {
                user = e.getTo().getUser();
            }
        } else {
            LOG.warn("user from o-uri is null: " + e.getCall_id());
        }

        String domain = null;
        // if (e.getLastDestination() != null &&
        // !e.getLastDestination().equals(NA)) {
        // domain = e.getLastDestination();
        // } else {
        domain = e.getOURI().getDomain();
        // }

        // if we do not know the provider of the o-uri it is probably a call to
        // an ip location. In this case we extract the b-user from the
        // To:-header.
        
        //TODO does this make sense ?
        if (provmap.get(domain) == null) {
            user = e.getOURI().getUser();
        }

        e.setProperty(B_PROVIDER, provmap.get(domain));
        e.setProperty(B_USER, user);
        e.setProperty(TARGET, "n/a");
    }

    public void setFromProvider(SERLogEvent e) {
        String user = e.getFrom().getUser();
        String p = provmap.get(e.getFrom().getDomain());
        e.setProperty(A_PROVIDER, p);
        e.setProperty(A_USER, user);
    }

    public static Map<String, String> getProviderMap() {
        return provmap;
    }

    public String getProvider(String domain) {
        return provmap.get(domain);
    }

    public void setDbConnection(DbConnection dbConnection) {
        this.dbConnection = dbConnection;

        // we set here the values taken from database
        populateProviderMap();
    }

    /**
     * This method takes data from database table and populates the map.
     * 
     * @param m
     */
    private void populateProviderMap() {
        try {
            Statement stmt = dbConnection.getStatement();
            String selectString = "select mp.domain as domain, p.string as provider " + 
                                  "from map_providers mp, providers p " + 
                                  "where mp.provider = p.number";
            ResultSet rs = stmt.executeQuery(selectString);
            String key;
            String value;
            while (rs.next()) {
                key = rs.getString("domain");
                value = rs.getString("provider");
                provmap.put(key, value);
            }
            rs.close();
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    public static String getNA() {
        return NA;
    }

}
