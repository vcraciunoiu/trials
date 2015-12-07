package com.crossover.trial.properties;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * This is the main class of my application.
 * 
 * @author vlad
 *
 */
public class Main {
	
	/*
 	 * Arguments can be like this:	
 	 * 	
 	 * classpath:resources/jdbc.properties
 	 * file://t:\tmp\aws.properties
 	 * http://localhost:8080/testele-mele-war-0.0.2-SNAPSHOT/superserviciu/testare
	 */
	public static void main(String[] args) {
		
		Map<String, MyProperty> propertiesToDisplay = new TreeMap<String, MyProperty>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		});
		
		PropertiesProcessor processor = new PropertiesProcessor();
		
		for (String uri : args) {
			MyLogger.log(Level.INFO, "Processing argument " + uri);
			
			String uriType = findURIType(uri);
			if (uriType == null) {
				System.out.println("error: we couldn't determine the URI type");
				continue;
			}
			
			ReadStrategy readStrategy = null;

			switch (uriType) {
			case "classpath":
				readStrategy = new ClasspathReader();
				break;
			case "file":
				readStrategy = new FilesystemReader();
				break;
			case "http":
				readStrategy = new RestpointReader();
				break;
			default:
				break;
			}
			
			processor.setUri(uri);
			processor.setReader(readStrategy);
			
			processor.processURI(propertiesToDisplay);
		}

		displayProperties(propertiesToDisplay);
	}

	private static String findURIType(String uri) {
		String uriType = uri.substring(0, uri.indexOf(":"));
		
		MyLogger.log(Level.INFO, "For URI " + uri + " the type is " + uriType);
		
		return uriType;
	}

	/*
	 * We have to display to stdout the names of all properties in case insensitive alphabetical order, in the following format:
     * propertyName, propertyType, propertyValue
	 */
	private static void displayProperties(Map<String, MyProperty> propertiesToDisplay) {
		for (Entry<String, MyProperty> entry : propertiesToDisplay.entrySet()) {
			displayProperty(entry.getValue());
		}
	}

	private static void displayProperty(MyProperty myProperty) {
		System.out.println(myProperty);
	}

}
