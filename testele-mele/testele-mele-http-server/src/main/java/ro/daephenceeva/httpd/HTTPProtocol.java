package ro.daephenceeva.httpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class implements the HTTP protocol details: the way we parse the input,
 * what we do with the request depending on method type and so on.
 * 
 * A request looks like this: HEAD/GET/POST/PUT/DELETE http://host[:port]/path[?queryString] 
 */
public class HTTPProtocol {

	public Request parseRequest(BufferedReader in) throws BadParseException {
		String line;
		Request request = new Request();
		
		try {
			// we get the first line, ex: GET /gigi.html HTTP/1.1
			line = in.readLine();
			String[] splitResult = line.split("\\s");
			
			request.setMethod(splitResult[0]);
			request.setResourceName(splitResult[1]);
			request.setProtocolVersion(splitResult[2]);
		
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

	public Response processRequest(Request request, String serverWorkspace) throws BadProcessException, ResourceNotFoundException {
		Response response = new Response();
		
		response.setProtocolVersion(request.getProtocolVersion());
		
		response.setStatus(HTTPProtocolConstants.HTTP_200_SUCCESS);
		
		String method = request.getMethod();
		
		// for now our server supports HEAD, GET and POST methods
		switch (method) {
		case "HEAD":
			//TODO
			doHEAD();
			break;
		case "GET":
			doGET(request, serverWorkspace, response);
			break;
		case "POST":
			//TODO
			doPOST();
			break;
		default:
			break;
		}
		
		return response;
	}

	private void doHEAD() {
		// TODO Auto-generated method stub
		
	}

	private void doGET(Request request, String serverWorkspace, Response response) throws ResourceNotFoundException {
		// set the headers on response
		String contentType = getMimeType(request.getResourceName());
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_TYPE, contentType);
		
		Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_DATE, dateFormat.format(date));
		
		// we get the file and put it on response
		String fileName = request.getResourceName();
		File file = new File(serverWorkspace + fileName).getAbsoluteFile();
        if(!(file.exists())) {
            throw new ResourceNotFoundException("Resource not found");
        }
        
        response.setResource(file);
        
		int length = (int) file.length();
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_LENGTH, String.valueOf(length));
	}

	private void doPOST() {
		// TODO we should handle json payload and file upload
	}

	private String getMimeType(String resourceName) {
		String mimeType = null;
		
		String[] splitResult = resourceName.split("\\.");
		String fileExtension = splitResult[1];
		
		mimeType = HTTPProtocolConstants.MimeTypes.valueOf(fileExtension).getContentType();
		
		return mimeType;
	}

}
