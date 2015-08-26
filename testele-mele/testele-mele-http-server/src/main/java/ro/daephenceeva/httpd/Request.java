package ro.daephenceeva.httpd;

import java.util.HashMap;
import java.util.Map;

public class Request {

	private String method;
	
	private String resource;
	
	private Map<String, String> headers = new HashMap<String, String>();

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

}
