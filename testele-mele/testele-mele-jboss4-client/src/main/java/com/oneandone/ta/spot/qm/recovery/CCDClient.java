package com.oneandone.ta.spot.qm.recovery;

import java.util.Hashtable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneandone.coredev.swistec.pis.api.PisQueryService;
import com.oneandone.coredev.swistec.pis.api.query.PisQueryConfig;

import de.einsundeins.crm.ccd.service.CcdCustomerService;
import de.einsundeins.crm.ccd.service.request.ro.GetByIntCustomerNumberRequest;
import de.einsundeins.crm.ccd.service.response.ro.GetCustomerPropertiesResponse;
import pachetu.IMyEjb3FacadeRemote;

public class CCDClient {

    public static final String JNDI_URL = "jnp://bsccdboss01.schlund.de:1200,bsccdboss02.schlund.de:1200,bsccdboss03.schlund.de:1200";
    public static final String JNDI_URL_AC1 = "jnp://ac1bsccdbossa01.mw.server.lan:1200,jnp://ac1bsccdbossa02.mw.server.lan:1200";

    public static final String JNDI_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";
	
    private static InitialContext jndiContext;

    public static void main(String args[]) {
    	
    	testMyEjb();
    	
    }

	private static void testMyEjb() {
		try {
            init();

            CcdCustomerService ejbulDeLaMamaDraq = (CcdCustomerService) jndiContext.lookup(CcdCustomerService.JNDI_NAME);
            
            GetByIntCustomerNumberRequest request = new GetByIntCustomerNumberRequest();
            request.setIntCustomerNumber("12345678");

            GetCustomerPropertiesResponse response = ejbulDeLaMamaDraq.getCustomerPropertiesByIntCustomerNumber(request);

            System.out.println(response.toString());
            
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

    public static void init() throws JMSException, NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, JNDI_URL_AC1);
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        jndiContext = new InitialContext(env);
    }

}
