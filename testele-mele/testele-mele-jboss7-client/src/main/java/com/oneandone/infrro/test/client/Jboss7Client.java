package com.oneandone.infrro.test.client;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

//import org.activiti.engine.ManagementService;
//import org.activiti.engine.RuntimeService;
//import org.activiti.engine.runtime.Execution;
//import org.activiti.engine.runtime.ExecutionQuery;
//
//import com.camunda.fox.platform.api.ProcessEngineService;
//import com.oneandone.infrro.ejibiuri.ICevateste;

public class Jboss7Client {

	public static void main(String[] args) {
		testJboss7();
	}

	public static void testJboss7() {
		// Properties pr = new Properties();
		// pr.put("endpoint.name", "client-endpoint");
		// pr.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		// pr.put("remote.connections", "default");
		// pr.put("remote.connection.default.port", "4447");
		// pr.put("remote.connection.default.host", "vcraciunoiu.ro.schlund.net");
		// pr.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS",
		// "JBOSS-LOCAL-USER");
		// pr.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
		// pr.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		// pr.put("remote.connection.default.username", "jboss");
		// pr.put("remote.connection.default.password", "jboss-12345");
		//
		// EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(pr);
		// ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
		// EJBClientContext.setSelector(selector);

//		Properties props = new Properties();
//		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
//		props.put("jboss.naming.client.ejb.context", true);
//		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//		props.put(Context.PROVIDER_URL, "remote://localhost:4447");
//		props.put(Context.SECURITY_PRINCIPAL, "jboss");
//		props.put(Context.SECURITY_CREDENTIALS, "12345");
//
//		Context context = null;
//		ICevateste ejbul = null;
//		try {
//			context = new InitialContext(props);
//			ejbul = (ICevateste) context
//					.lookup("ejb:/test-ejb-jar-0.0.1-SNAPSHOT//Cevateste!com.oneandone.infrro.ejibiuri.ICevateste");

			// for (int i = 0; i < 10; i++) {
//			ejbul.putSomeStatus();
//			System.out.println("am pus niste status");

//			ejbul.getSomeStatus();
//			System.out.println("am luat niste status");

//			String spuneAltCeva = ejbul.spuneAltCeva();
//			System.out.println(spuneAltCeva);
			// }

//			ejbul.testActivitiDiagram();
//			System.out.println("gata testActivitiDiagram");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	// this is to test fox-platform from outside
	private static void testCamundaPlatform() {
//		Properties props = new Properties();
//		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
//		props.put("jboss.naming.client.ejb.context", true);
//		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
//		props.put(Context.PROVIDER_URL, "remote://vm465.development.lan:4447");
//
//		Context context = null;
//		ProcessEngineService processEngineService = null;
//		RuntimeService runtimeService = null;
//		ManagementService managementService;
//
//		try {
//			context = new InitialContext(props);
//			processEngineService = (ProcessEngineService) context
//					.lookup("java:global/camunda-fox-platform/process-engine/"
//							+ "PlatformService!com.camunda.fox.platform.api.ProcessEngineService");
//
//			runtimeService = processEngineService.getDefaultProcessEngine().getRuntimeService();
//
//			managementService = processEngineService.getDefaultProcessEngine().getManagementService();
//			managementService.executeJob("job");
//
//			List<Execution> list = runtimeService.createExecutionQuery().list();
//
//			for (Execution execution : list) {
//				String processInstanceId = execution.getProcessInstanceId();
//				System.out.println(processInstanceId);
//			}
//
//			ExecutionQuery processVariableValueNotEqualsIgnoreCase = runtimeService.createExecutionQuery()
//					.processVariableValueNotEqualsIgnoreCase("fsdf", "fdf");
//			List<Execution> list2 = processVariableValueNotEqualsIgnoreCase.list();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
