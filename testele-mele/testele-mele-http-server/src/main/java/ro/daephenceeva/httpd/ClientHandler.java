package ro.daephenceeva.httpd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

	private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

	private Socket socket = null;

	private HTTPProtocol protocol = null;
	
	private String serverWorkspace;
	
	public ClientHandler(Socket socket, String serverWorkspace) {
		this.socket = socket;
		this.serverWorkspace = serverWorkspace;
		this.protocol = new HTTPProtocol();
	}
	
	@Override
	public void run() {
		logger.info("Processing request from " + socket.toString());

		try (
			// this is a nice feature from Java 7: try-with-resources; closing streams is handled automatically
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
		){
			boolean keepAlive = false;
			
			do {
	            try {
	                Response response = null;
	                
	                try {
	                    Request request = protocol.parseRequest(in);
	                    
	                    String connection = request.getHeaders().get("Connection");
	                    if (connection.equals("keep-alive")) {
	                    	keepAlive = true;
	                    } else {
	                    	keepAlive = false;
	                    }
	                    
	                    response = protocol.processRequest(request, serverWorkspace);
	                } catch (BadParseException bpe) {
	                    response = newExceptionStatus(protocol.HTTP_400_BAD_REQUEST, bpe);
	                } catch (BadProcessException bqe) {
	                    response = newExceptionStatus(protocol.HTTP_500_INTERNAL_ERROR, bqe);
	                }
	                
	                returnRespone(response, out);
	                
	                logger.info("Succesfully processed request from " + socket.toString());
	            } catch (Exception e) {
	    			logger.severe("Something ugly happened...");
	    			keepAlive = false;
	            }
            } while (keepAlive);
		} catch (Exception e) {
        	logger.severe("Error processing request from " + socket.toString());
		} finally {
            try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void returnRespone(Response response, PrintStream out) throws Exception {
		out.println("HTTP/1.1" + response.getStatus());
		
		// write the headers on socket's output stream
		for (Entry<String, String> header : response.getHeaders().entrySet()) {
			out.println(header.getKey() + ": " + header.getValue());
		}
		// we have to add an empty line after headers, as the protocol requires
		out.println("");
		
		// write the file
        DataInputStream fin = new DataInputStream(new FileInputStream(response.getResource()));
        int length = (int) response.getResource().length();
		byte buf[] = new byte[length];

        fin.readFully(buf);
        out.write(buf, 0, length);
        fin.close();
	}

	private Response newExceptionStatus(Integer status, Exception e) {
		Response response = new Response();
		
		response.setStatus(status);
		
		response.getHeaders().put("Content-Type", "text/html");
		
		Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		response.getHeaders().put("Date", dateFormat.format(date));

		// set the exception message as the "resource" file
		try {
			File temp = File.createTempFile("exception", ".html");
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
		    StringBuffer htmlErrorPage = new StringBuffer();
		    htmlErrorPage.append("<html>");
		    htmlErrorPage.append("<head>");
		    htmlErrorPage.append("<title>");
		    htmlErrorPage.append("Error, duh!");
		    htmlErrorPage.append("</title>");
		    htmlErrorPage.append("</head>");
		    htmlErrorPage.append("<body>");
		    htmlErrorPage.append("Internal server error.");
		    htmlErrorPage.append("<p>");
		    htmlErrorPage.append(e.getMessage());
		    htmlErrorPage.append("</p>");
		    htmlErrorPage.append("</body>");
		    htmlErrorPage.append("</html>");
			out.write(htmlErrorPage.toString());
		    out.close();
		    
			response.setResource(temp);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return response;
	}

}
