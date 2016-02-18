package com.crossover.trial.properties;

import java.util.Properties;

/**
 * This interface is implemented by the different readers.
 * They all read properties from a source which can be a file in classpath,
 * a file in the file-system or from a web service.
 * 
 * @author vlad
 *
 */
public interface ReadStrategy {

	Properties readPropertiesSource(String uri);
}

