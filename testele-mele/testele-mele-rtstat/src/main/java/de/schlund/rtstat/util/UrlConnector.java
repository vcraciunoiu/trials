package de.schlund.rtstat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class UrlConnector {
    private String url;

    final static Logger LOG = Logger.getLogger(UrlConnector.class);

    public UrlConnector(String url) {
        this.url = url;
    }

    public void openConnection(String content) {
        try {

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", content.length() + "");

            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(content);

            out.flush();
            out.close();

            BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String str;
            while (null != ((str = input.readLine()))) {
                LOG.info(str);
            }
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("", e);
        }

    }
}