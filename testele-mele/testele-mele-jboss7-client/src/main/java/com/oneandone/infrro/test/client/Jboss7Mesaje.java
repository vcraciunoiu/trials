package com.oneandone.infrro.test.client;

import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Jboss7Mesaje {
	
	private static Context ctx1;
	private static ConnectionFactory factory1;
	private static Connection con1;
	private static Session session1;
	private static Queue q1;

	private static Context ctx2;
	private static ConnectionFactory factory2;
	private static Connection con2;
	private static Session session2;
	private static Queue q2;

	public static void main(String[] args) throws Exception {
		setupCommon();
		
		setup1();
//		setup2();

//		testProducer1();
		testConsumer1();
		
//		testProducer2();
//		testConsumer2();

//		closeup2();
		closeup1();
	}

	private static void setupCommon() {
		String keystoreFile = "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net.jks";
		String truststoreFile = "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net-truststore.jks";
		String password = "test123";
		
		System.setProperty("javax.net.ssl.trustStore", truststoreFile);
		System.setProperty("javax.net.ssl.trustStorePassword", password);
		System.setProperty("javax.net.ssl.keyStore", keystoreFile);
		System.setProperty("javax.net.ssl.keyStorePassword", password);

		////////////////////////////////////////////////
//		KeyStore ks = KeyStore.getInstance("JKS");
//		ks.load(new FileInputStream(keystoreFile), password.toCharArray());
//		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//		kmf.init(ks, password.toCharArray());
//
//		KeyStore ts = KeyStore.getInstance("JKS");
//		ts.load(new FileInputStream(truststoreFile), password.toCharArray());
//		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//		tmf.init(ts);
//
//		SSLContext sslContext = SSLContext.getInstance("TLS");
//		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
//		SSLSupport support = new SSLSupport();
//		SSLContext sslContext = SSLSupport.getInstance(true, keystoreFile, password, truststoreFile, password);

//		SSLContext.setDefault(sslContext);
		////////////////////////////////////////////////
	}

	private static void setup1() throws Exception {
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		env.put(Context.PROVIDER_URL, "remote://vm-dchelcioiu.sandbox.lan:4447");
		env.put(Context.SECURITY_PRINCIPAL, "pqatrotest");
		env.put(Context.SECURITY_CREDENTIALS, "M2VjNmM0MTZl");
		
		env.put("jboss.naming.client.remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "true");
		env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		env.put("jboss.naming.client.connect.options.org.xnio.Options.SSL_STARTTLS", "false");
		
		ctx1 = new InitialContext(env);
		factory1 = (ConnectionFactory) ctx1.lookup("/jms/RemoteConnectionFactory");
		con1 = factory1.createConnection("pqatrotest", "M2VjNmM0MTZl");
		session1 = con1.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		q1 = (Queue) ctx1.lookup("queue/DLQ");
	}

	private static void closeup1() throws JMSException, NamingException {
		session1.close();
		con1.close();
		ctx1.close();
	}

	// these are for the second server
	private static void setup2() throws Exception {
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		env.put(Context.PROVIDER_URL, "remote://vm-fslevoaca-9869.sandbox.lan:4647");
		env.put(Context.SECURITY_PRINCIPAL, "pqatrotest");
		env.put(Context.SECURITY_CREDENTIALS, "M2VjNmM0MTZl");
		
		env.put("jboss.naming.client.remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "true");
		env.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		env.put("jboss.naming.client.connect.options.org.xnio.Options.SSL_STARTTLS", "false");
		
		ctx2 = new InitialContext(env);
		factory2 = (ConnectionFactory) ctx2.lookup("/jms/RemoteConnectionFactory");
		con2 = factory2.createConnection("pqatrotest", "M2VjNmM0MTZl");
		session2 = con2.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		q2 = (Queue) ctx2.lookup("queue/ExpiryQueue");
	}

	private static void closeup2() throws JMSException, NamingException {
		session2.close();
		con2.close();
		ctx2.close();
	}

	private static void testProducer1() throws JMSException, Exception {
		MessageProducer p = session1.createProducer(q1);
		for (int i = 0; i < 10; i++) {
			produceTextMessage(session1, p);
//			produceObjectMessage(session, p);
//			produceKnownObjectMessage(session, p);
//			produceBytesMessage(session, p);
//			produceStreamMessage(session, p);
//			produceMapMessage(session, p);
		}
		p.close();
	}

	private static void testConsumer1() throws JMSException {
		MessageConsumer c = session1.createConsumer(q1);
		con1.start();
		Message msg = c.receive();
		System.out.println("consumed message is: " + msg.getStringProperty("prop_string"));
		c.close();
		con1.stop();
	}

	private static void testProducer2() throws JMSException, Exception {
		MessageProducer p = session2.createProducer(q2);
		for (int i = 0; i < 10; i++) {
			produceTextMessage(session2, p);
		}
		p.close();
	}

	private static void testConsumer2() throws JMSException {
		MessageConsumer c = session2.createConsumer(q2);
		con2.start();
		Message msg = c.receive();
		System.out.println("consumed message is: " + msg.getStringProperty("prop_string"));
		c.close();
		con2.stop();
	}

	private static void produceTextMessage(Session session, MessageProducer p) throws Exception {
		TextMessage tm = session.createTextMessage();
		tm.setText("text message");
		tm.setStringProperty("prop_string", "un string");
		tm.setIntProperty("prop_int", 12);
		p.send(tm);
	}

	private static void produceObjectMessage(Session session, MessageProducer p) throws Exception {
		String b = new String("wewe");
		ObjectMessage om = session.createObjectMessage(b);
		om.setIntProperty("prop_int", 12);
		p.send(om);
	}

	private static void produceKnownObjectMessage(Session session, MessageProducer p) throws Exception {
		ObjectMessage om = session.createObjectMessage(new String("republica congo"));
		om.setIntProperty("prop_int", 12);
		p.send(om);
	}

	private static void produceBytesMessage(Session session, MessageProducer p) throws Exception {
		BytesMessage bm = session.createBytesMessage();
		bm.writeFloat(3.14f);
		bm.setByteProperty("prop_byte", (byte) 2);
		p.send(bm);
	}

	private static void produceStreamMessage(Session session, MessageProducer p) throws Exception {
		StreamMessage sm = session.createStreamMessage();
		sm.writeString("un string scris in stream");
		sm.setDoubleProperty("prop_double_in_stream", 24.23);
		p.send(sm);
	}

	private static void produceMapMessage(Session session, MessageProducer p) throws Exception {
		MapMessage mm = session.createMapMessage();
		mm.setString("string_scris_in_map", "erebus");
		mm.setLong("long_prop", 25l);
		mm.setLongProperty("proprietate_long", 2l);

		System.out.println(mm.getObject("long_prop"));

		p.send(mm);
	}

}
