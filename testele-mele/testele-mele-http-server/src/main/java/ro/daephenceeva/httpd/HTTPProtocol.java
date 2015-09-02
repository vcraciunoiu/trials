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
 * An HTTP request looks like this: HEAD|GET|POST|PUT|DELETE http://host[:port]/path[?queryString]
 * 
 * On the socket we will receive a first line with essential information and then headers.
 * Something like this:
 * 
 * GET /file.html HTTP/1.1
 * Host: localhost:8080
 * User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:40.0) Gecko/20100101 Firefox/40.0
 * Accept: text/html,application/xhtml+xml,application/xml
 * Accept-Language: en-US,en;q=0.5
 * Accept-Encoding: gzip, deflate
 * Cookie: ajs_user_id=null; ajs_group_id=null;
 * Connection: keep-alive
 * Cache-Control: max-age=0
 * {empty line}
 *  
 * We get all this info and process it.
 *  
 */
public class HTTPProtocol {

	public Request parseRequest(BufferedReader in) throws BadParseException {
		String line;
		Request request = new Request();
		
		try {
			// we get the first line, ex: "GET /gigi.html HTTP/1.1"
			line = in.readLine();
			System.out.println(line);
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

	public Response processRequest(Request request, Response response, String serverWorkspace) throws BadProcessException, 
			ResourceNotFoundException, NotYetImplementedException {
		
		response.setProtocolVersion(request.getProtocolVersion());
		
		response.setStatus(HTTPProtocolConstants.HTTP_200_SUCCESS);
		
		Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_DATE, dateFormat.format(date));
		
		String method = request.getMethod();
		
		// in the first version our server supports HEAD and GET methods
    	// for the methods which we don't allow, the response MUST include an Allow header containing 
    	// a list of valid methods for the requested resource. 
		switch (method) {
		case HTTPProtocolConstants.METHOD_HEAD:
			doHEAD(request, response);
			break;
		case HTTPProtocolConstants.METHOD_GET:
			doGET(request, serverWorkspace, response);
			break;
		case HTTPProtocolConstants.METHOD_POST:
			doPOST(request, response);
			break;
		case HTTPProtocolConstants.METHOD_PUT:
			doPUT(request, response);
			break;
		case HTTPProtocolConstants.METHOD_DELETE:
			doDelete(request, response);
			break;
		default:
			break;
		}
		
		return response;
	}

	private void doHEAD(Request request, Response response) throws NotYetImplementedException {
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new NotYetImplementedException("The HEAD method is not yet implemented.");
	}

	private void doGET(Request request, String serverWorkspace, Response response) throws ResourceNotFoundException {
		// set the headers on response
		String contentType = getMimeType(request.getResourceName());
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_TYPE, contentType);
		
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

	private void doPOST(Request request, Response response) throws NotYetImplementedException {
		// in the POST method we could upload a file to server, 
		// or receive a JSON payload which we would pass-on to a middleware
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new NotYetImplementedException("The POST method is not yet implemented.");
	}

	private void doPUT(Request request, Response response) throws NotYetImplementedException {
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new NotYetImplementedException("The PUT method is not yet implemented.");
	}

	private void doDelete(Request request, Response response) throws NotYetImplementedException {
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new NotYetImplementedException("The DELETE method is not yet implemented.");
	}

	private String getMimeType(String resourceName) {
		String mimeType = null;
		
		String[] splitResult = resourceName.split("\\.");
		String fileExtension = splitResult[1];
		
		mimeType = HTTPProtocolConstants.MimeTypes.valueOf(fileExtension).getContentType();
		
		return mimeType;
	}

}
