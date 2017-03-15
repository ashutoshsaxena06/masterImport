package com.esave.mail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.StringUtils;

import com.esave.common.PropertiesManager;
import com.esave.entities.PurveyorDetails;
import com.esave.exception.PurveyorNotFoundException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;

public class MailProcessor {

	private static final String USER_EMAIL = "user@gmail.com";
	private static final String USER_PASSWORD = "password";
	private String saveDirectory;

	static Scanner scanner = null;

	/**
	 * Sets the directory where attached files will be stored.
	 * 
	 * @param dir
	 *            absolute path of the directory
	 */
	public void setSaveDirectory(String dir) {
		this.saveDirectory = dir;
	}

	private IMAPSSLStore createConnection() throws MessagingException {
		// Create IMAPSSLStore object
		Properties props = System.getProperties();
		props.setProperty("mail.store.protocol", "imaps");
		Session session = Session.getDefaultInstance(props, null);
		URLName urlName = new URLName("imap.gmail.com");
		IMAPSSLStore store = new IMAPSSLStore(session, urlName);

		// TODO: All sysout statements are used for testing, have to remove them
		// while implementation
		System.out.println("Connecting to gmail...");

		// Connect to GMail, enter user name and password here
		store.connect("imap.gmail.com", USER_EMAIL, USER_PASSWORD);

		System.out.println("Conected to - " + store);
		return store;
	}

	/**
	 * Main method evoke search function
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		PropertiesManager.purveyorPropertiesFile="purveyor.properties";
		PropertiesManager.locationPropertiesFile="location.properties";

		MailProcessor mailProcessor = new MailProcessor();

		// create an Imap connection with gmail
		IMAPSSLStore store = mailProcessor.createConnection();

		// process the orders from EMail
		mailProcessor.processOrdersFromEmail(store);

		if (scanner != null) {
			scanner.close();
		}
	}

	/**
	 * This method is used to connect to GMail account and search in it for
	 * keyword
	 * 
	 * @param store
	 * @param keywordToSearch
	 * @throws MessagingException
	 * @throws PurveyorNotFoundException 
	 */
	public void processOrdersFromEmail(IMAPSSLStore store)
			throws MessagingException, PurveyorNotFoundException {

		IMAPFolder folderInbox = null;
		IMAPFolder folderProcessed = null;

		try {

			// Get the folder you want to search in e.g. INBOX
			folderInbox = (IMAPFolder) store.getFolder("INBOX");
			folderProcessed = (IMAPFolder) store.getFolder("processed");
			System.out.println("Total mails in inbox are = " + folderInbox.getMessageCount());

			if (folderInbox != null) {

				System.out.println("Searching started....");

				// Create GMail raw search term and use it to search in folder
				folderInbox.open(Folder.READ_WRITE);
				SearchTerm rawTerm = new SearchTerm() {

					private static final long serialVersionUID = 1L;

					@Override
					public boolean match(Message message) {
						try {
							if (message.getSubject().contains("Order")) {
								return true;
							}
						} catch (MessagingException ex) {
							ex.printStackTrace();
						}
						return false;
					}
				};
				Message[] messagesFound = folderInbox.search(rawTerm);

				System.out.println("Total messages found for keyword are = " + messagesFound.length);
				System.out.println("Messages found are:");

				List<Message> tempList = new ArrayList<>();
				// Process the messages found in search
				System.out.println("--------------------------------------------");
				String contentType;
				String messageContent;
				for (Message message : messagesFound) {
					contentType = message.getContentType();
					System.out.println("# " + message.getSubject());
					if (contentType.contains("text/plain") || contentType.contains("text/html")) {
						Object content;
						content = message.getContent();

						if (content != null) {
							messageContent = content.toString();
							scanner = new Scanner(messageContent);
							processOrder(scanner, messageContent);
						}
					}
					if (contentType.contains("multipart")) {
						downloadEmailAttachmentsAndProcessOrder(message);

					}
					tempList.add(message);
					message.setFlag(Flags.Flag.DELETED, true);
				}
				Message[] tempMessageArray = tempList.toArray(new Message[tempList.size()]);
				folderInbox.copyMessages(tempMessageArray, folderProcessed);
				System.out.println("--------------------------------------------");
				folderInbox.expunge();
				System.out.println("Searching done!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (folderInbox.isOpen()) {
				folderInbox.close(true);
			}
			if (folderProcessed.isOpen()) {
				folderProcessed.close(true);
			}
			store.close();
		}
	}

	/**
	 * Downloads new messages and saves attachments to disk if any.
	 * 
	 * @throws PurveyorNotFoundException 
	 */
	public boolean downloadEmailAttachmentsAndProcessOrder(Message message) throws PurveyorNotFoundException {

		try {

			String messageContent = "";

			// store attachment file name, separated by comma
			String attachFiles = "";

			// content may contain attachments
			Multipart multiPart;

			multiPart = (Multipart) message.getContent();

			int numberOfParts = multiPart.getCount();
			for (int partCount = 0; partCount < numberOfParts; partCount++) {
				MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
				if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
					// this part is attachment
					String fileName = part.getFileName();
					attachFiles += fileName + ", ";
					part.saveFile(saveDirectory + File.separator + fileName);
				} else {
					// this part may be the message content
					messageContent = part.getContent().toString();
					scanner = new Scanner(messageContent);
//					processOrder(scanner, messageContent);
				}
			}

			if (attachFiles.length() > 1) {
				attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
			}

			System.out.println("\t Attachments: " + attachFiles);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void processOrder(Scanner scanner, String messageContent) throws IOException, MessagingException, PurveyorNotFoundException {
		String purveyorId = null;
		String locationId = null;
		PurveyorDetails purveyorDetails = null;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("Purveyor:")) {
				String purveyorIdRecieved = line.substring(line.indexOf("("), line.lastIndexOf(")"));
				purveyorId = purveyorIdRecieved.trim();
				String locationIdRecieved = line.substring(line.indexOf("("), line.lastIndexOf(")"));
				locationId = locationIdRecieved.trim();
			}
		}
		if(StringUtils.isEmpty(purveyorId)||StringUtils.isEmpty(locationId)){
			throw new PurveyorNotFoundException("Purveyor details not found in the order", 101);
		}
		Properties purveyorProperties = PropertiesManager.getPurveyorProperties();
		String purveyorStoreUrl = purveyorProperties.getProperty(purveyorId);
		Properties locationProperties = PropertiesManager.getPurveyorProperties();
		String storeCredentials = locationProperties.getProperty(locationId);
		String storeUserName = storeCredentials.split("/")[0];
		String storePassword = storeCredentials.split("/")[1];
		if(StringUtils.isEmpty(purveyorStoreUrl)||StringUtils.isEmpty(storeUserName)||StringUtils.isEmpty(storePassword)){
			throw new PurveyorNotFoundException("Purveyor details does not exist in the system", 101);
		}
		purveyorDetails = new PurveyorDetails(purveyorStoreUrl, storeUserName, storePassword);
	}
}
