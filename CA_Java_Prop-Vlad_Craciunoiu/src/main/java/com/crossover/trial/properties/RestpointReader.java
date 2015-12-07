package com.crossover.trial.properties;

import java.util.Properties;

import org.springframework.web.client.RestTemplate;

/**
 * Reads the properties file from a REST endpoint.
 * 
 * @author vlad
 *
 */
public class RestpointReader implements ReadStrategy {

	@Override
	public Properties readPropertiesSource(String uri) {
		RestTemplate restTemplate = new RestTemplate();
		Properties jsonConfig = restTemplate.getForObject(uri, Properties.class);
		
		return jsonConfig;
	}

}
