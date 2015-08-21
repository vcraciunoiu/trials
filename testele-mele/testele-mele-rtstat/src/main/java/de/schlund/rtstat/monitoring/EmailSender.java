/*
 * Created on 31.05.2007
 * by sonja
 */
package de.schlund.rtstat.monitoring;

import java.util.Date;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static final String CONTENT_TYPE = "text/plain; charset=UTF8";
    private static final Properties SMTP_PROPERTIES = new Properties();
    static {
        SMTP_PROPERTIES.put("mail.smtp.host","mxintern.schlund.de");
        SMTP_PROPERTIES.put("mail.smtp.auth", "false");
    }
        
    public void sendEmail(String content,String subject,String to) throws MessagingException {
        final InternetAddress from = new InternetAddress("spieper@schlund.de");
        final Session session = Session.getDefaultInstance(SMTP_PROPERTIES);
        final MimeMessage mail = new MimeMessage(session);
        mail.setFrom(from);
        mail.setRecipients(RecipientType.TO, to);
        mail.setContent(content, CONTENT_TYPE);
        mail.setSubject(subject);
        mail.setSentDate(new Date( ));
        Transport.send(mail);           
    }
}
            
