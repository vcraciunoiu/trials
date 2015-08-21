/*
 * Created on 31.05.2007
 * by sonja
 */
package de.schlund.rtstat.monitoring;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class reads the complete output from a given url via a http connection and
 * returns it as a string for further processing.
 * @author Sonja Pieper <spieper@schlund.de>
 */
public class URLRequest {

public static final int MAX_TRIES =3;
    
    static {
        System.setProperty("sun.net.client.defaultConnectTimeout", "20000");
        System.setProperty("sun.net.client.defaultReadTimeout", "20000");        
    }
       
    private URL url;
    
    public URLRequest(URL url) {
        this.url = url;
    }
    
    public String read() throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)this.url.openConnection();        
        return this.readStream(connection.getInputStream());                         
    }
    
    protected String readStream(final InputStream stream) throws IOException {
        LineNumberReader input = null;
        try {
            final StringBuffer result = new StringBuffer();
            input = new LineNumberReader(new InputStreamReader(stream));
            String line = input.readLine();
            while(line!=null) {                     
                result.append(line);
                result.append("\n");                
                line = input.readLine();
            }
            return result.toString();
        } finally {
            try {
                if (input != null) {
                    input.close();
                }                
            } catch (IOException e) {
                throw e;
            }       
        }
    }
    
  
    
}
