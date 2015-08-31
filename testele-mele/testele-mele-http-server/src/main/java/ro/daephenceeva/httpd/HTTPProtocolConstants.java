package ro.daephenceeva.httpd;

public interface HTTPProtocolConstants {

	String PROTOCOL_VERSION_HTTP_1_1 = "HTTP/1.1";
	
	Integer HTTP_200_SUCCESS = 200;
	Integer HTTP_400_BAD_REQUEST = 400;
	Integer HTTP_404_NOT_FOUND = 404;
	Integer HTTP_500_INTERNAL_ERROR = 500;
	
	String HEADER_NAME_CONTENT_LENGTH = "Content-Length";
	String HEADER_NAME_DATE = "Date";
	String HEADER_NAME_CONTENT_TYPE = "Content-Type";
	String HEADER_NAME_CONNECTION = "Connection";

	String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
	String HEADER_VALUE_TEXT_HTML = "text/html";
	
	enum MimeTypes {
		json ("application/json"),
		html ("text/html"),
		txt ("text/plain"),
		xml ("text/xml"),
		zip ("application/zip")
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
