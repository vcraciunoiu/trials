package ro.daephenceeva.httpd;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
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

	private static final Integer HTTP_200_SUCCESS = 200;
	private static final Integer HTTP_400_BAD_REQUEST = 400;
	private static final Integer HTTP_500_INTERNAL_ERROR = 500;

	private Socket socket = null;

	private String serverWorkspace;
	
	public ClientHandler(Socket socket, String serverWorkspace) {
		this.socket = socket;
		this.serverWorkspace = serverWorkspace;
	}
	
	@Override
	public void run() {
		logger.info("Processing request from " + socket.toString());

		try (
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
		){
            try {
                Response response = null;
                
                try {
                    Request request = parseRequest(in);
                    response = processRequest(request);
                } catch (BadParseException bpe) {
                    response = newExceptionStatus(HTTP_400_BAD_REQUEST, bpe);
                } catch (BadProcessException bqe) {
                    response = newExceptionStatus(HTTP_500_INTERNAL_ERROR, bqe);
                }
                
                returnRespone(response, out);
                
                socket.close();
            } catch (Exception e) {
                // log this locally, we can't return it to the client....
            	e.printStackTrace();
            }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private Request parseRequest(BufferedReader in) throws BadParseException {
		String line;
		Request request = new Request();
		
		try {
			// first line, ex: GET /gigi.html HTTP/1.1
			line = in.readLine();
			String[] splitResult = line.split("\\s");
			
			request.setMethod(splitResult[0]);
			request.setResource(splitResult[1]);
		
			do {
				line = in.readLine();
				System.out.println(line);
				if (!line.isEmpty()) {
					splitResult = line.split(":\\s");
					
					request.getHeaders().put(splitResult[0], splitResult[1]);
				}
			} while (line != null);
		} catch (IOException e) {
			throw new BadParseException(e);
		}
		
		return request;
	}

	private Response processRequest(Request request) throws BadProcessException {
		Response response = new Response();
		
		response.setStatus(HTTP_200_SUCCESS);
		
		// we get the file and put it on response
		String fileName = request.getResource();
		File file = new File(serverWorkspace + System.getProperty("file.separator") + fileName).getAbsoluteFile();
        if(!(file.exists())) {
            throw new BadProcessException("File not found error");
        }
        
        response.setResource(file);
        
		// set the headers on response
		response.getHeaders().put("Content-Type", "text/html");
		
		Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		response.getHeaders().put("Date", dateFormat.format(date));
		
		int length = (int) file.length();
		response.getHeaders().put("Content-Length", String.valueOf(length));
        
		return response;
	}

	private void returnRespone(Response response, PrintStream out) throws Exception {
		response.getHeaders().put("HTTP/1.1", "200 OK");
		out.println("HTTP/1.1" + response.getStatus() + "\r\n");
		
		// write the headers on socket's output stream
		for (Entry<String, String> header : response.getHeaders().entrySet()) {
			out.println(header.getKey() + ": " + header.getValue() + "\r\n");
		}
		out.println("\r\n");
		
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
		//TODO
		
		return response;
	}

}
