package ro.daephenceeva.httpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * This class implements the HTTP protocol details: the way we parse the input,
 * what we do with the request depending on method type and so on.
 * 
 * The HTTP protocol specification is here:
 * 
 * https://www.ietf.org/rfc/rfc2616.txt
 * http://www.w3.org/Protocols/rfc2616/rfc2616.html
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

	private static final Logger logger = Logger.getLogger(HTTPProtocol.class.getName());
	
	public Request parseRequest(BufferedReader in) throws ProcessingException {
		String line;
		Request request = new Request();
		
		try {
			// we get the first line, ex: "GET /gigi.html HTTP/1.1"
			line = in.readLine();
			logger.fine("First line is: " + line);

			// TODO sometimes this line is null; I have to see why;
			// for now I make a workaround
			if (line == null) {
				return null;
			}

			String[] splitResult = line.split("\\s");

			request.setMethod(splitResult[0]);
			request.setResourceName(splitResult[1]);
			request.setProtocolVersion(splitResult[2]);

			while (true) {
				line = in.readLine();
				logger.fine(line);
				if (!line.isEmpty()) {
					splitResult = line.split(":\\s");
					request.getHeaders().put(splitResult[0], splitResult[1]);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			throw new ProcessingException(HTTPProtocolConstants.HTTP_400_BAD_REQUEST, e.getMessage());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return request;
	}

	public Response processRequest(Request request, Response response, String serverWorkspace) throws ProcessingException {
		
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
			doHEAD(request, serverWorkspace, response);
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

	/*
	 * HEAD is identical to GET except that only the HTTP headers are returned. 
	 * The body is discarded. It is primarily used for checking the validity of URLs.
	 */
	private void doHEAD(Request request, String serverWorkspace, Response response)	throws ProcessingException {
		doGETInternal(request, serverWorkspace, response, false);
	}

	/*
	 * GET is meant for retrieving content from the web server. The requests should be idempotent.
	 */
	private void doGET(Request request, String serverWorkspace, Response response) throws ProcessingException {
		doGETInternal(request, serverWorkspace, response, true);
	}

	private void doGETInternal(Request request, String serverWorkspace, Response response, boolean isGet)
			throws ProcessingException {
		// set the headers on response
		String resourceName = request.getResourceName();
		
		if (resourceName.equals("/")) {
			// we display the server's default welcome page
			resourceName = "/index.html";
		}
		
		String contentType = getMimeType(resourceName);
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_TYPE, contentType);
		
		// we get the file and put it on response
		String fileName = resourceName;
		File file = new File(serverWorkspace + fileName).getAbsoluteFile();
        if(!(file.exists())) {
            throw new ProcessingException(HTTPProtocolConstants.HTTP_404_NOT_FOUND, "Resource not found");
        }
        
        if (isGet) {
            response.setResource(file);
        }
        
		int length = (int) file.length();
		response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_CONTENT_LENGTH, String.valueOf(length));
	}

	/*
	 * POST is used for operations that manipulate content on the server, such as adding, editing or removing content.
	 * In the POST method we could upload a file to server, 
     * or receive a JSON payload which we would pass-on to a middleware.
	 */
	private void doPOST(Request request, Response response) throws ProcessingException {
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new ProcessingException(HTTPProtocolConstants.HTTP_405_METHOD_NOT_ALLOWED, 
				"The POST method is not yet implemented.");
	}

	private void doPUT(Request request, Response response) throws ProcessingException {
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new ProcessingException(HTTPProtocolConstants.HTTP_405_METHOD_NOT_ALLOWED, 
				"The PUT method is not yet implemented.");
	}

	private void doDelete(Request request, Response response) throws ProcessingException {
    	response.getHeaders().put(HTTPProtocolConstants.HEADER_NAME_ALLOW, "HEAD,GET");
		throw new ProcessingException(HTTPProtocolConstants.HTTP_405_METHOD_NOT_ALLOWED, 
				"The DELETE method is not yet implemented.");
	}

	private String getMimeType(String resourceName) throws ProcessingException {
		String mimeType = null;
		
		String[] splitResult = resourceName.split("\\.");
		
		if (splitResult.length != 2) {
			throw new ProcessingException(HTTPProtocolConstants.HTTP_415_UNKNOWN_CONTENT_TYPE, "Cannot find content type.");
		}
		
		String fileExtension = splitResult[1];
		
		try {
			mimeType = HTTPProtocolConstants.MimeTypes.valueOf(fileExtension).getContentType();
		} catch (IllegalArgumentException e) {
			throw new ProcessingException(HTTPProtocolConstants.HTTP_415_UNKNOWN_CONTENT_TYPE, 
					"Unsupported or unknown content type.");
		}
		
		return mimeType;
	}

}
