package pachetu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

public class MailTest {

	private static Session mailSession;

	public static void main(String[] args) {
//		init();
//
//		List<String> toAddresses = new ArrayList<String>();
//		toAddresses.add("vlad.craciunoiu@1and1.ro");
//		toAddresses.add("ccosman@msint.1and1.com");
//		
//		sendEmail(toAddresses);
		
	}

	private static void init() {
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "mri.server.lan");
//		properties.setProperty("mail.smtp.host", "172.19.220.129");
		properties.put("mail.smtp.port", "25");

		properties.put("mail.smtp.connectiontimeout", 1000*120 );
		properties.put("mail.smtp.timeout ", 1000*120 );

//		properties.setProperty("mail.user", "vcraciunoiu");
//		properties.setProperty("mail.password", "mypwd");
		mailSession = Session.getDefaultInstance(properties);
		mailSession.setDebugOut(System.out);
	}

	public static void sendEmail(Collection<String> toAddresses) {
		MimeMessage mimeMessage = new MimeMessage(mailSession);
		try {
			mimeMessage.setSubject("RHQ mail test");
			mimeMessage.setContent("This is a test, please ignore it.", "text/plain");
			mimeMessage.setFrom(new InternetAddress("vlad.craciunoiu@1and1.ro"));
			mimeMessage.setSentDate(new Date());
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		// Send to each recipient individually, do not throw exceptions until we try them all
		for (String toAddress : toAddresses) {
			try {
				System.out.println("Sending email to recipient [" + toAddress + "]");
				
				InternetAddress recipient = new InternetAddress(toAddress);
				mimeMessage.setRecipients(RecipientType.TO, toAddress);
				Transport.send(mimeMessage, new InternetAddress[] { recipient });

				System.out.println("Email sent to [" + toAddress + "]");
			} catch (Exception e) {
				System.out.println("Failed to send email to recipient [" + toAddress + "] ");
				e.printStackTrace();
			}
		}
	}

}
