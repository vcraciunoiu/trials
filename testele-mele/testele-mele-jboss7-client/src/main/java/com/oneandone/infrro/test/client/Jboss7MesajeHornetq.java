package com.oneandone.infrro.test.client;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnectionFactory;

public class Jboss7MesajeHornetq {

    private static String applUser;
    private static String applPass;
    
    public static final String testQueueName = "DLQ";
    
    public static Map<String, Object> getProps() throws Exception{
//        applUser = "jboss"; applPass = "jboss-12345";
        applUser = "pqatrotest"; applPass = "M2VjNmM0MTZl";
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("host", "vm-dchelcioiu.sandbox.lan"); //vm-dchelcioiu.sandbox.lan
        map.put("port", 5445);
        
        map.put("ssl-enabled", true);
//        map.put("key-store-path", "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net.jks");
//        map.put("key-store-password", "test123");
        return map;
    }

    public static void main(String args[]) throws Exception{
        setupCommon();
        
        TransportConfiguration config = new TransportConfiguration(NettyConnectorFactory.class.getName(), 
                getProps()); 
        HornetQConnectionFactory cf = null;
        QueueConnection c = null;       
        try{
            cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.QUEUE_CF, config);
            c = (QueueConnection)cf.createConnection(applUser, applPass);     
            c.start();
            
            //use queue name, not jndi queue name
            Queue q = HornetQJMSClient.createQueue(testQueueName);
            QueueSession session =  c.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
//            JMSUtils.viewQueue(session, q);
//            JMSUtils.produce(session, q, 2);
//            JMSUtils.consume(session, q, 2);
            
        } finally{
            if (cf != null){
                cf.close();
            }
            if (c != null){
                c.close();
            }
        }
    }

    private static void setupCommon() {
        String keystoreFile = "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net.jks";
        String truststoreFile = "/home/vlad/Documents/various-settings/ssl/vcraciunoiu.ro.schlund.net-truststore.jks";
        String password = "test123";
        
        System.setProperty("javax.net.ssl.trustStore", truststoreFile);
        System.setProperty("javax.net.ssl.trustStorePassword", password);
        System.setProperty("javax.net.ssl.keyStore", keystoreFile);
        System.setProperty("javax.net.ssl.keyStorePassword", password);
    }

}
