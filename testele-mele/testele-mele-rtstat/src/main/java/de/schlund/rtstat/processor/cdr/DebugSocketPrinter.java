package de.schlund.rtstat.processor.cdr;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import de.schlund.rtstat.util.ErrorReporter;

/**
 * Simple helper class which creates a server socket and prints statistics
 * to every established connection.
 * 
 * @author mic
 * @author Frank Spychalski (<a href="mailto:spychalski@schlund.de">spychalski@schlund.de</a>)
 */
public class DebugSocketPrinter  extends Thread {

    final static Logger LOG = Logger.getLogger(DebugSocketPrinter.class);
    private Object _object;
    private int _port;

    public DebugSocketPrinter(Object object, int port) {
        _object = object;
        _port = port;
    }

    @Override
    public void run() {
        ServerSocket server;
        Socket s;
        OutputStreamWriter os;

        try {
            LOG.info("Server socket on port["+_port+"]");
            server = new ServerSocket(_port);
            for (;;) {
                try {
                    s = server.accept();
                    LOG.info("accepted new connection from " + s.getRemoteSocketAddress());                    
                    os = new OutputStreamWriter(s.getOutputStream());
                    os.append(_object.toString());
                    os.flush();
                    s.close();
                    LOG.info("finished.");

                } catch (Exception e) {
                    ErrorReporter r = new ErrorReporter();
                    r.sendException(e);
                }
            }
        } catch (IOException e) {
            ErrorReporter r = new ErrorReporter();
            r.sendException(e);
        }
    }
}