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
			
			// with this loop we implement the keep-alive feature, which means we keep the socket open,
			// and we process more requests in the same TCP connection. We close the connection when the client sends
			// the header "Connection: close", in case of an error, or in case of the keep-alive timeout 
			// (not implemented in this version)
			do {
	            try {
	                Response response = new Response();
	                
	                try {
	                    Request request = protocol.parseRequest(in);
	                    
	                    String connection = request.getHeaders().get(HTTPProtocolConstants.HEADER_NAME_CONNECTION);
	                    if (connection.equals(HTTPProtocolConstants.HEADER_VALUE_KEEP_ALIVE)) {
	                    	keepAlive = true;
	                    } else {
	                    	keepAlive = false;
	                    }
	                    
	                    protocol.processRequest(request, response, serverWorkspace);
	                    
	                } catch (NotYetImplementedException nyi) {
	                	newExceptionStatus(response, HTTPProtocolConstants.HTTP_405_METHOD_NOT_ALLOWED, nyi);
	                	keepAlive = false;
	                } catch (ResourceNotFoundException fnf) {
	                	newExceptionStatus(response, HTTPProtocolConstants.HTTP_404_NOT_FOUND, fnf);
	                	keepAlive = false;
	                } catch (BadParseException bpe) {
	                    newExceptionStatus(response, HTTPProtocolConstants.HTTP_400_BAD_REQUEST, bpe);
	                	keepAlive = false;
	                } catch (BadProcessException bqe) {
	                    newExceptionStatus(response, HTTPProtocolConstants.HTTP_500_INTERNAL_ERROR, bqe);
	                    keepAlive = false;
	                }
	                
	                returnRespone(response, out);
	                
	                logger.info("Succesfully processed request from " + socket.toString());
	            } catch (Exception e) {
	            	logger.severe("Error processing request from " + socket.toString() + ": " + e.getMessage());
	    			keepAlive = false;
	            }
            } while (keepAlive);
			
		} catch (Exception e) {
			logger.severe("Something ugly happened in the HTTP conversation of socket " + socket.toString() 
						+ ": " + e.getMessage());
		} finally {
            try {
    			logger.info("Closing socket " + socket.toString());
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void returnRespone(Response response, PrintStream out) throws Exception {
		out.println(HTTPProtocolConstants.PROTOCOL_VERSION_HTTP_1_1 + " " + response.getStatus());
		
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

	private Response newExceptionStatus(Response response, Integer status, Exception e) {
		response.setStatus(status);
		
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_TYPE, 
				HTTPProtocolConstants.HEADER_VALUE_TEXT_HTML);
		
		// set the exception message as the "resource" file
		
		// TODO these temporary files will stay on disk, ex. in folder "c:\Users\vlad\AppData\Local\Temp\".
		// we should get rid of them, or don't create them at all
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
			
			int length = (int) temp.length();
			response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_LENGTH, String.valueOf(length));

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return response;
	}

}
