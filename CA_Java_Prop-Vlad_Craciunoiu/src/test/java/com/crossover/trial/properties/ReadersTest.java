package com.crossover.trial.properties;

import java.net.URL;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class ReadersTest {

	@Test
	public void testFilesystemReader() throws Exception {
		FilesystemReader reader = new FilesystemReader();
		
		Assert.assertNotNull("Test file missing", getClass().getResource("/jdbc.properties"));
		URL resource = getClass().getResource("/jdbc.properties");
		System.out.println(resource.toString());
		
		String resourcePath = resource.toString();
		resourcePath = resourcePath.replace("file:/", "file://");
		System.out.println(resourcePath);
		
		Properties properties = reader.readPropertiesSource(resourcePath);
		Assert.assertNotNull(properties);
		
		Assert.assertNotEquals(0, properties.size());
		
		String property = (String) properties.get("JDBC_USERNAME");
		Assert.assertEquals("username123", property);
	}
	
}
