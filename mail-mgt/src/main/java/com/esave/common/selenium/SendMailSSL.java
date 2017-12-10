package com.esave.common.selenium;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendMailSSL {

	public static  Session createConnection() throws MessagingException {
		// Create IMAPSSLStore object
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		// All sysout statements are used for testing, have to remove them
		// while implementation
		System.out.println("Connecting to gmail...");
		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("onlineweekend.diningedge@gmail.com", "edge2016");// change
																								// accordingly
			}
		});
		return session;
	}

	public static void sendMailAction(String OrderTD, String status ) {
		String[] to = {"ashutoshsaxena06@gmail.com","raj.esave@gmail.com","dawn@diningedge.com","paola@diningedge.com"};
		String user = "onlineweekend.diningedge@gmail.com";// change
																	// accordingly
		try {
			// get connection
			Session session = createConnection();
			//String filepath = RandomAction.setdownloadDir();
		
			
			MimeMessage message = new MimeMessage(session);
			
	        message.setFrom(new InternetAddress(user)); 

			InternetAddress[] recipientAddress = new InternetAddress[to.length];
			int counter = 0;
			for (String recipient : to) {
				recipientAddress[counter] = new InternetAddress(recipient.trim());
				counter++;
			}

			message.addRecipients(Message.RecipientType.TO, recipientAddress);
			// accordingly
			// message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			//smessageBodyPart1.addRecipient(Message.RecipientType.CC, new InternetAddress("teamesave@gmail.com"));

			// Subject of mails
			message.setSubject("OrderID : " +OrderTD+ "- status :: " + status);
			// Body of mails 
	         message.setText("<Auto generated mail notification> 'Order Information for CheneyItrade'");  

			Transport.send(message);

			System.out.println("Message send success");

		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			// TODO: handle exception
		}

	}

}