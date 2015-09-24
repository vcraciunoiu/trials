package ro.daephenceeva.httpd;

public interface HTTPProtocolConstants {

	String PROTOCOL_VERSION_HTTP_1_1 = "HTTP/1.1";
	
	String METHOD_HEAD = "HEAD";
	String METHOD_GET = "GET";
	String METHOD_POST = "POST";
	String METHOD_PUT = "PUT";
	String METHOD_DELETE = "DELETE";
	
	Integer HTTP_200_SUCCESS = 200;
	Integer HTTP_400_BAD_REQUEST = 400;
	Integer HTTP_404_NOT_FOUND = 404;
	Integer HTTP_405_METHOD_NOT_ALLOWED = 405;
	Integer HTTP_415_UNKNOWN_CONTENT_TYPE = 415;
	Integer HTTP_500_INTERNAL_ERROR = 500;
	
	String HEADER_NAME_CONTENT_LENGTH = "Content-Length";
	String HEADER_NAME_DATE = "Date";
	String HEADER_NAME_CONTENT_TYPE = "Content-Type";
	String HEADER_NAME_CONNECTION = "Connection";
	String HEADER_NAME_ALLOW = "Allow";

	String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
	String HEADER_VALUE_TEXT_HTML = "text/html";
	
	enum MimeTypes {
		json ("application/json"),
		html ("text/html"),
		txt ("text/plain"),
		xml ("text/xml"),
		zip ("application/zip"),
		jpg ("image/jpeg"),
		ico ("image/x-icon")
		// and others...
		;
		
	    public String contentType;
	    
	    public String getContentType() {
			return contentType;
		}
	    
		MimeTypes(String contentType) { 
	        this.contentType = contentType;
	    }	
	}
	
}
