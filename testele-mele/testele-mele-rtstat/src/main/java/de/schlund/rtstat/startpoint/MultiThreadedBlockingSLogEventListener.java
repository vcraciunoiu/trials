package de.schlund.rtstat.startpoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.schlund.rtstat.model.SERLogEvent;
import de.schlund.rtstat.model.SERLogEventFactory;
import de.schlund.rtstat.util.ErrorReporter;

public class MultiThreadedBlockingSLogEventListener extends SERLogProcessorFeeder {
	final static Logger LOG = Logger.getLogger(MultiThreadedBlockingSLogEventListener.class);
	final static Logger LOG_EVENT = Logger.getLogger("LOGGER_EVENTLOG");
	private ServerSocket ss;

	private SERLogEventFactory parser;

	public MultiThreadedBlockingSLogEventListener(String name, Integer port, Integer backlog) throws Exception {
		super(name);
		ss = new ServerSocket(port, backlog);
	}

	public void run() {

		while (true) {
			try {
				Socket s = ss.accept();
				String client = s.getRemoteSocketAddress().toString();
				LOG.debug("accepted new connection from " + s);
				Thread cl = new Thread(new ClientHandler(s));
				cl.setName(client);
				cl.start();
			} catch (IOException e) {
				ErrorReporter r = new ErrorReporter();
				r.sendException(e);
			}
		}
	}

	public void setParser(SERLogEventFactory parser) {
		this.parser = parser;
	}

	class ClientHandler implements Runnable {
		private Socket sock = null;

		public ClientHandler(Socket s) {
			sock = s;
		}

		public void run() {
			InputStream in = null;
			String line = null;

			try {
				in = sock.getInputStream();
			} catch (IOException e) {
				Map<String, String> message = new HashMap<String, String>();
				message.put("IOException: ", "While trying to getInputStream()");
				ErrorReporter r = new ErrorReporter();
				r.sendException(e);
				LOG.error(e);
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(in));

			boolean readSomething = false;
			while (true) {
				try {
					line = r.readLine();
				} catch (IOException e) {
					Map<String, String> message = new HashMap<String, String>();
					message.put("IOException: ", "While trying to readLine()");
					LOG.error(e);
				}

				if (line == null) {
					LOG.info("line is null");
					break;
				}
				readSomething = true;

				SERLogEvent ev = null;
				try {
					ev = parser.parse(line);
				} catch (Exception e) {
					LOG.error("Exception in SERLogEventFactory.parse: " + line, e);
				}
				line = null;
				if (ev != null) {
					process(ev);
					ev = null;
				}
			}
			if (!readSomething) {
				LOG.error("no data received from " + sock.getInetAddress() + " at " + new Date());
			}

			try {
				if (sock != null) {
					sock.close();
					LOG.debug("socket closed.");
				} else {
					LOG.error("Socket was null!");
				}
			} catch (IOException e) {
				Map<String, String> message = new HashMap<String, String>();
				message.put("IOException: ", "While trying to close()");
				LOG.error(e);
			}
		}
	}

}
