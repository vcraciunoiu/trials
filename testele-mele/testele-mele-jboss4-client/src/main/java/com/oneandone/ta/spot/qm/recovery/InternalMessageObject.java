package com.oneandone.ta.spot.qm.recovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InternalMessageObject {

	private	String id;
	
	private Map<String,String> header = new HashMap<String, String>();

	private List<String[]> properties = new ArrayList<String[]>();
	
	public InternalMessageObject() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public List<String[]> getProperties() {
		return properties;
	}

	public void setProperties(List<String[]> properties) {
		this.properties = properties;
	}

}
