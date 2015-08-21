package com.oneandone.ta.spot.qm.recovery;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Jboss4ClientJMS {

	//// CHANGE THESE
	public static final String JNDI_URL = "jnp://vcraciunoiu.ro.schlund.net:1199";
	public static final String QUEUE_JNDI_NAME = "/queue/myqueue";

	private static final String FILE_PATH = "/home/vlad/Documents/qm";
	private static final String FILE_NAME = "msg_backup.log.2014-10-16";

	private static String STRING_DATE = "2014-10-16";
	////

	public static final String JNDI_CONTEXT_FACTORY = "org.jnp.interfaces.NamingContextFactory";
	public static final String JMS_USER = null;
	public static final String JMS_PASSWORD = null;
	public static final String JMS_CONNECTION_FACTORY = "ConnectionFactory";

	static QueueConnection qConn = null;
	static QueueSession qSession = null;
	static QueueSender qSender = null;
	static QueueReceiver qReceiver = null;

	private static final String JMSCorrelationID = "JMSCorrelationID";
	private static final String JMSDeliveryMode = "JMSDeliveryMode";
	private static final String JMSDestination = "JMSDestination";
	private static final String JMSExpiration = "JMSExpiration";
	private static final String JMSMessageID = "JMSMessageID";
	private static final String JMSPriority = "JMSPriority";
	private static final String JMSRedelivered = "JMSRedelivered";
	private static final String JMSReplyTo = "JMSReplyTo";
	private static final String JMSTimestamp = "JMSTimestamp";
	private static final String JMSType = "JMSType";

	private static Map<String,Destination> destinationsCache = new HashMap<String, Destination>();

	private static Context jndiContext;
	
	public static void main(String args[]) throws Exception {
		try {
			// parse file
			List<InternalMessageObject> msgList = parseFile();
			
			System.out.println("--- finished parsing file.");
/*			
			// Initialize connection
			init();
			
			for (InternalMessageObject internalMessageObject : msgList) { 
				// create message 
				MapMessage mapMessage = createMessage(internalMessageObject);

				// Send Message 
				qSender.send(mapMessage);

				// Wait 2 sec for answer 
				Thread.sleep(2000); 
			}

			// Disconnect 
			destroy();
*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init() throws JMSException, NamingException {
		// Set up JNDI Context
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, JNDI_URL);
		
		if (JMS_USER != null) {
			env.put(Context.SECURITY_PRINCIPAL, JMS_USER);
		}
		
		if (JMS_PASSWORD != null) {
			env.put(Context.SECURITY_CREDENTIALS, JMS_PASSWORD);
		}
		
		jndiContext = new InitialContext(env);

		// Lookup queue connection factory
		QueueConnectionFactory cFactory = (QueueConnectionFactory) jndiContext.lookup(JMS_CONNECTION_FACTORY);

		// Create Connection
		if (JMS_USER == null || JMS_PASSWORD == null)
			qConn = cFactory.createQueueConnection();
		else {
			qConn = cFactory.createQueueConnection(JMS_USER, JMS_PASSWORD);
		}

		// Create Session
		qSession = qConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		// Lookup Queue
		Queue queue = (Queue) jndiContext.lookup(QUEUE_JNDI_NAME);

		// Create Queue Sender
		qSender = qSession.createSender(queue);

		// Create Queue Receiver
		qReceiver = qSession.createReceiver(queue);
		// qReceiver.setMessageListener(this);

		// Start receiving messages
		qConn.start();
	}

	public static MapMessage createMessage(InternalMessageObject internalMessageObject) throws Exception {
		// TextMessage msg = qSession.createTextMessage(str);
		MapMessage msg = qSession.createMapMessage();
		
		// set headers
		msg.setJMSCorrelationID(internalMessageObject.getHeader().get(JMSCorrelationID));
		msg.setJMSDeliveryMode(Integer.parseInt(internalMessageObject.getHeader().get(JMSDeliveryMode)));
		
		String destinationString = internalMessageObject.getHeader().get(JMSDestination);
		Destination destination = getDestinationFromCache(destinationString);
		msg.setJMSDestination(destination);
		
		msg.setJMSExpiration(Long.parseLong(internalMessageObject.getHeader().get(JMSExpiration)));
		msg.setJMSMessageID(internalMessageObject.getId());
		msg.setJMSPriority(Integer.parseInt(internalMessageObject.getHeader().get(JMSPriority)));
		msg.setJMSRedelivered(Boolean.parseBoolean(internalMessageObject.getHeader().get(JMSRedelivered)));
		
		//TODO 
//		String replyTo = internalMessageObject.getHeader().get(JMSReplyTo)
		// we only have null in our file, so is ok for now
		msg.setJMSReplyTo(null);
		
		msg.setJMSTimestamp(Long.parseLong(internalMessageObject.getHeader().get(JMSTimestamp)));
		msg.setJMSType(internalMessageObject.getHeader().get(JMSType));

		// set properties
		for (String[] property : internalMessageObject.getProperties()) {
			msg.setString(property[0], property[2]);
		}
		
		return msg;
	}

	public static Destination getDestinationFromCache(String destinationString) throws Exception {
		// destinationString is like "JBossQueue[OrderStarterDLQ]"
		destinationString = destinationString.substring(destinationString.indexOf("[")+1, destinationString.indexOf("]"));
		destinationString = "/queue/" + destinationString;
		Destination destination = destinationsCache.get(destinationString);
		if (destination == null) {
			destination = (Queue) jndiContext.lookup(destinationString);
		}
		return destination;
	}

	public static void destroy() throws Exception {
		if (qSender != null)
			qSender.close();
		if (qReceiver != null)
			qReceiver.close();
		if (qSession != null)
			qSession.close();
		if (qConn != null)
			qConn.close();

		// Close JNDI context
		jndiContext.close();
	}

	private static List<InternalMessageObject> parseFile() throws Exception {
		List<InternalMessageObject> result = new ArrayList<InternalMessageObject>();

		Path path = FileSystems.getDefault().getPath(FILE_PATH, FILE_NAME);
		List<String> readAllLines = Files.readAllLines(path, StandardCharsets.UTF_8);

		Boolean parsingHeaderSection = false;
		Boolean parsingPropertiesSection = false;
		Boolean parsingEnvelopeSection = false;

		InternalMessageObject internalMessageObject = null;

		String orderEnvelope = "";
		String envelopePropertyName = null;
		String envelopePropertyType = null;
		
		for (String stringLine : readAllLines) {
//			System.out.println(stringLine);
//			System.out.println("---------------------------------------");

			String trimedLine = stringLine.trim();

			if (trimedLine.isEmpty()) {
				parsingHeaderSection = false;
				continue;
			}
			
			if (trimedLine.startsWith(STRING_DATE) && trimedLine.contains("Deleting message ID")) {
				internalMessageObject = new InternalMessageObject();
				
				String id = extractMessageId(stringLine);
				internalMessageObject.setId(id);
			}

			if (trimedLine.equals("Header:")) {
				parsingHeaderSection = true;
				continue;
			}
			
			if (parsingHeaderSection) {
				String[] values = trimedLine.split(":");
				internalMessageObject.getHeader().put(values[0].trim(), values[1].trim());
			}

			if (trimedLine.equals("Properties:")) {
				parsingPropertiesSection = true;
				continue;
			}

			if (parsingPropertiesSection) {
				
				if (trimedLine.equals("Body:")) {
					parsingPropertiesSection = false;
					parsingEnvelopeSection = false;
					continue;
				}
				
				if (parsingEnvelopeSection) {
					orderEnvelope = orderEnvelope + trimedLine;
					if (trimedLine.length() < 76) {
						// line is shorter than normal -> envelope finished; this assumption may not be correct !
						parsingEnvelopeSection = false;
						
						internalMessageObject.getProperties().add(
								new String[] { envelopePropertyName, envelopePropertyType, orderEnvelope });

					} else {
						orderEnvelope = orderEnvelope + "\n";
					}
				} else {
					String[] values = trimedLine.split(":");
					
					envelopePropertyName = values[0].trim();
					envelopePropertyType = values[1].trim();
					
					if (envelopePropertyName.equals("ORDER_ENVELOPE") || envelopePropertyName.equals("zippedOrderEnvelope")) {
						parsingEnvelopeSection = true;
						orderEnvelope = orderEnvelope + values[2].trim() + "\n";
					} else {
						internalMessageObject.getProperties().add(
								new String[] { envelopePropertyName, envelopePropertyType, values[2].trim() });
						envelopePropertyName = null;
						envelopePropertyType = null;
						orderEnvelope = "";
					}
				}
			}

			if (trimedLine.startsWith(STRING_DATE) && trimedLine.contains("Deleted message ID")) {
				result.add(internalMessageObject);
			}

		}

		return result;
	}

	private static String extractMessageId(String stringLine) {
		String pattern = "(.*Deleting message )(.*)( from queue.*)$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(stringLine);

		String id = null;
		if (m.matches() && m.groupCount() == 3) {
			id = m.group(2);
		}

		return id==null ? "NULLID" : id;
	}

}
