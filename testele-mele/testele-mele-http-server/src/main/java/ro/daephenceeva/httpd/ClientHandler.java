package ro.daephenceeva.httpd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

	private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

	private Socket socket = null;
	
	public ClientHandler(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {
		try {
			logger.info("Processing request...");
			
		    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			while (true) {
				String line = in.readLine();
				// do some work with this data...
				System.out.println(line);
				if (line == null) {
					break;
				}
			}
			
		    StringBuffer headers = new StringBuffer();
		    headers.append("HTTP/1.0 200 OK\r\n");
	        headers.append("Server: VLADCRCserver v.0.1\r\n");
		    headers.append("Content-Type: text/html\r\n");
		    headers.append("Content-Length: 3\r\n");
		    headers.append("\r\n");
		    out.write(headers.toString());
			
	        FileInputStream requestedfile = new FileInputStream("/home/vlad/Documents/play/some.html");
			while (true) {
				int b = requestedfile.read();
				if (b == -1) {
					break;
				}
				out.write(b);
			}
			
			requestedfile.close();
			out.close();
			in.close();
			socket.close();
		
			logger.info("Succesfully processed request.");
		} catch (IOException e) {
			logger.severe("Error processing request: " + e.getMessage());
		}

	}

}
