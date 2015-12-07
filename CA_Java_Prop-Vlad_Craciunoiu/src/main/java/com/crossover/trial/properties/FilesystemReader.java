package com.crossover.trial.properties;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Reads the properties file from file system.
 * 
 * @author vlad
 *
 */
public class FilesystemReader implements ReadStrategy {

//	private static final Logger logger = Logger.getLogger(FilesystemReader.class.getName());
	
	@Override
	public Properties readPropertiesSource(String uri) {
		MyLogger.log(Level.INFO, "We got the URI: " + uri);
		
		String fileName = uri.substring(uri.indexOf(":") + 3);

		MyLogger.log(Level.INFO, "File name is: " + fileName);
		
		Properties properties = new Properties();
		
		try (final InputStream stream = new FileInputStream(fileName)) {
		    properties.load(stream);
		} catch (IOException e) {
			MyLogger.log(Level.SEVERE, "Couldn't load properties from file " + fileName);
		}
		
		return properties;
	}

}
