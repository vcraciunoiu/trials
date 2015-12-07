package com.crossover.trial.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Reads the properties file from classpath.
 * 
 * @author vlad
 *
 */
public class ClasspathReader implements ReadStrategy {

//	private static final Logger logger = Logger.getLogger(ClasspathReader.class.getName());
	
	@Override
	public Properties readPropertiesSource(String uri) {
		MyLogger.log(Level.INFO, "We got the URI: " + uri);
		
		String fileName = uri.substring(uri.indexOf(":") + 1);

		MyLogger.log(Level.INFO, "File name is: " + fileName);
		
		Properties properties = new Properties();
		
		try (final InputStream stream = this.getClass().getResourceAsStream(fileName)) {
		    properties.load(stream);
		} catch (IOException e) {
			MyLogger.log(Level.SEVERE, "Error loading props: " + e.getMessage());
		}
		
		return properties;
	}

}
