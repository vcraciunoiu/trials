package ro.daephenceeva.httpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HTTPProtocol {

	public static final Integer HTTP_200_SUCCESS = 200;
	public static final Integer HTTP_400_BAD_REQUEST = 400;
	public static final Integer HTTP_500_INTERNAL_ERROR = 500;

	public Request parseRequest(BufferedReader in) throws BadParseException {
		String line;
		Request request = new Request();
		
		try {
			// first line, ex: GET /gigi.html HTTP/1.1
			line = in.readLine();
			String[] splitResult = line.split("\\s");
			
			request.setMethod(splitResult[0]);
			request.setResource(splitResult[1]);
		
			while (true) {
				line = in.readLine();
				System.out.println(line);
				if (!line.isEmpty()) {
					splitResult = line.split(":\\s");
					request.getHeaders().put(splitResult[0], splitResult[1]);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			throw new BadParseException(e);
		}
		
		return request;
	}

	public Response processRequest(Request request, String serverWorkspace) throws BadProcessException {
		Response response = new Response();
		
		response.setStatus(HTTP_200_SUCCESS);
		
		// we get the file and put it on response
		String fileName = request.getResource();
		File file = new File(serverWorkspace + fileName).getAbsoluteFile();
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

}
