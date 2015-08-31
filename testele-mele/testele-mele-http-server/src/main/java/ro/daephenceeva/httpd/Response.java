package ro.daephenceeva.httpd;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Response {

	private String protocolVersion;
	
	private Integer status;
	
	private Map<String, String> headers = new HashMap<String, String>();
	
	private File resource;

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public File getResource() {
		return resource;
	}

	public void setResource(File resource) {
		this.resource = resource;
	}

}
