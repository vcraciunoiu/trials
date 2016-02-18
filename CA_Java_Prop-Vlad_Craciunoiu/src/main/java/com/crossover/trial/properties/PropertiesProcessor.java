package com.crossover.trial.properties;

import java.util.Map;
import java.util.Map.Entry;

import java.util.Properties;

import com.amazonaws.regions.Regions;

/**
 * This class processes the data sources. It reads from each source according to it's type
 * and aggregates the properties in a Map.
 * 
 * @author vlad
 *
 */
public class PropertiesProcessor {

	private String uri;
	private ReadStrategy reader;

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setReader(ReadStrategy reader) {
		this.reader = reader;
	}

	/*
	 * The main method of the properties processor.
	 * It reads the source based on the read strategy which was previously set.
	 */
	public void processURI(Map<String, MyProperty> propertiesToDisplay) {
		Properties propertiesFromSource = reader.readPropertiesSource(uri);
		
		for (Entry<Object, Object> propertyFromSource : propertiesFromSource.entrySet()) {
			MyProperty property = new MyProperty();
			
			String propertyName = (String) propertyFromSource.getKey();
			
			property.setPropertyName(propertyName);
			
			Object propertyValue = propertyFromSource.getValue();
			property.setPropertyValue(propertyValue);
			
			String propertyType = findPropertyType(propertyValue);
			property.setPropertyType(propertyType );
			
			propertiesToDisplay.put(propertyName, property);
		}
	}

	/*
	 * Determine the type of the property.
	 * The values from the REST endpoint are already parsed by Jackson Mapper.
	 * The others come as strings and we check their type by catching NumberFormatException.
	 * An alternative would be to use regexp, but that can be less performant.
	 */
	private String findPropertyType(Object propertyValue) {
		String type = null;
		
		String result = null;
		try {
			result = (String) propertyValue;
			
			if ("true".equalsIgnoreCase(result) || "false".equalsIgnoreCase(result)) {
				type = Boolean.class.getName();
			} else {
				try {
					Integer.parseInt(result);
					type = Integer.class.getName();
				} catch (NumberFormatException e) {
					try {
						Double.parseDouble(result);
						type = Double.class.getName();
					} catch (NumberFormatException e1) {
						try {
							Regions.fromName(result);
							type = Regions.class.getName();
						} catch (Exception e2) {
							type = String.class.getName();
						}
					}
				}
			}
			
		} catch (ClassCastException e) {
			type = propertyValue.getClass().getName();
		}
		
		return type;
	}
	
}
