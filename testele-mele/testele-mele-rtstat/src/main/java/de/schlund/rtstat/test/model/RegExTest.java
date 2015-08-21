package de.schlund.rtstat.test.model;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import de.schlund.rtstat.Controller;
import de.schlund.rtstat.processor.ser.ProviderUtil;
import de.schlund.rtstat.test.RtstatTestSuite;
import de.schlund.rtstat.util.Constants;

public class RegExTest extends TestCase {
	ApplicationContext ctx;
	Controller myController;

	@Override
	protected void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext("bean.xml");
		myController = (Controller) ctx.getBean("controller");
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		myController.stopProcessors();
	}

	public void testEutexRegex() {
		ProviderUtil util = (ProviderUtil) ctx.getBean("providerutil");
		assertTrue(util.getProvider("147.151.129.243") == null);

		assertTrue(util.getProvider(
				"1und1-2.interconnect.sip.voip.telefonica.de").equals(
				Constants.PROVIDER_TELEFONICA));
	}

	static {
		RtstatTestSuite.configureLogging();
	}

}
