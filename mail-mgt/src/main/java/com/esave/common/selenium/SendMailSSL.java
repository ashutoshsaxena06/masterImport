package com.esave.common.selenium;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


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
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("onlineweekend.diningedge@gmail.com", "edge2016");// change
																								// accordingly
			}
		});
		return session;
	}

	/*
	 * public static IMAPSSLStore createConnection() throws MessagingException {
	 * // Create IMAPSSLStore object Properties props = System.getProperties();
	 * props.setProperty("mail.store.protocol", "imaps");
	 * 
	 * Session session = Session.getDefaultInstance(props, new
	 * javax.mail.Authenticator() { protected PasswordAuthentication
	 * getPasswordAuthentication() { return new
	 * PasswordAuthentication(Constant.GmailUser, Constant.GmailPassword);//
	 * change // accordingly } }); URLName urlName = new
	 * URLName("imap.gmail.com"); IMAPSSLStore store = new IMAPSSLStore(session,
	 * urlName);
	 * 
	 * // All sysout statements are used for testing, have to remove them //
	 * while implementation System.out.println("Connecting to gmail...");
	 * 
	 * // Connect to GMail, enter user name and password here
	 * store.connect("imap.gmail.com", Constant.GmailUser,
	 * Constant.GmailPassword);
	 * 
	 * System.out.println("Connected to - " + store);
	 * 
	 * sendMailAction(session, Constant.GmailUser, Constant.GmailPassword);
	 * 
	 * System.out.println("send mail success"); return store; }
	 */

	public static void sendMailAction(String OrderTD, String status ) {
		String to = "ashutoshsaxena06@gmail.com";
		String user = "onlineweekend.diningedge@gmail.com";// change
																	// accordingly
		try {
			// get connection
			Session session = createConnection();
			//String filepath = RandomAction.setdownloadDir();
		
			
			MimeMessage message = new MimeMessage(session);
		//	message.setFrom(new InternetAddress(user));
		//	message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
		//	message.setSubject("Message Alert");

			MimeMessage messageBodyPart1 = new MimeMessage(session);
			messageBodyPart1.setFrom(new InternetAddress(user));// change
			// accordingly
			messageBodyPart1.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			//smessageBodyPart1.addRecipient(Message.RecipientType.CC, new InternetAddress("teamesave@gmail.com"));

			// Subject of mails
			message.setSubject("OrderID : " +OrderTD+ "- status :: " + status);
			// Body of mails 
		//	message.setContent("GFS OG export- date & time : " + RandomAction.getDate(), "text/html");

			//message.setText();

			// 4) create new MimeBodyPart object and set DataHandler object to
			// this object
			MimeBodyPart messageBodyPart2 = new MimeBodyPart();

		//	DataSource source = new FileDataSource(filename);
		//	messageBodyPart2.setDataHandler(new DataHandler(source));
		//	messageBodyPart2.setFileName(filename);

			// 5) create Multipart object and add MimeBodyPart objects to this
			// object
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart2);

			// 6) set the multiplart object to the message object
			message.setContent(multipart);

			// send message
			/*
			 * Transport transport = session.getTransport("smtp");
			 * transport.connect(Constant.GmailURL,Constant.GmailUser ,
			 * Constant.GmailPassword); transport.sendMessage(message,
			 * messageBodyPart1.getAllRecipients());
			 */
			Transport.send(message, messageBodyPart1.getAllRecipients());

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