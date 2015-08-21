package com.oneandone.ta.spot.qm.recovery;

import java.util.Hashtable;
import java.util.Properties;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.oneandone.coredev.swistec.pis.api.PisQueryService;
import com.oneandone.coredev.swistec.pis.api.query.PisQueryConfig;

import pachetu.IMyEjb3FacadeRemote;

public class Jboss4ClientEJB {

    public static final String JNDI_URL = "jnp://vcraciunoiu.ro.schlund.net:1199";
    public static final String JNDI_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";
	
    private static InitialContext jndiContext;

    public static void main(String args[]) {
//    	testMyEjb();
    	
    	testPisEjb();
    }

	private static void testPisEjb() {
		try {
			initForPIS();
			
			PisQueryService pisQueryService = (PisQueryService) jndiContext.lookup(PisQueryService.JNDI_PATH);
			PisQueryConfig query = PisQueryConfig.create();
			int processCount = pisQueryService.getProcessCount(query);
			System.out.println("avem atatea procese: " + processCount);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testMyEjb() {
		try {
            init();

            IMyEjb3FacadeRemote ejbulDeLaMamaDraq = (IMyEjb3FacadeRemote) jndiContext.lookup("MyEjb3Facade/remote");
            String result1 = ejbulDeLaMamaDraq.giveMeSomething();
            System.out.println(result1);
            
            String result2 = ejbulDeLaMamaDraq.giveMeSomethingElse();
            System.out.println(result2);

    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}

    public static void init() throws JMSException, NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, JNDI_URL);
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        jndiContext = new InitialContext(env);
    }

    public static void initForPIS() throws Exception {
//        Hashtable<String, String> env = new Hashtable<String, String>();
//        env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_CONTEXT_FACTORY);
//        env.put(Context.PROVIDER_URL, JNDI_URL);
//        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
//        jndiContext = new InitialContext(env);
        
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.security.jndi.JndiLoginInitialContextFactory");
		props.setProperty(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		props.setProperty(Context.PROVIDER_URL, JNDI_URL);
		props.setProperty(Context.SECURITY_PRINCIPAL, "kermit");
		props.setProperty(Context.SECURITY_CREDENTIALS, "kermit");
		props.setProperty("jnp.multi-threaded", "true");
		props.setProperty("jnp.restoreLoginIdentity", "true");

		jndiContext = new InitialContext(props);
    }

}
