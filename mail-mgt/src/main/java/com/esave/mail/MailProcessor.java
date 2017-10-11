package com.esave.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.apache.log4j.Logger;

import com.esave.common.NotificationEvent;
import com.esave.common.PropertiesManager;
import com.esave.common.Utils;
import com.esave.common.selenium.SeleniumItradeIO;
import com.esave.entities.OrderDetails;
import com.esave.exception.PurveyorNotFoundException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPSSLStore;

public class MailProcessor {

	private static final String DEFAUT_ATTACHMET_DIR = "C:/orders";
	private static final String DEFAULT_PURVEYOR_PROPERTIES = "purveyor.properties";
	private static final String DEFAULT_LOCATION_PROPERTIES = "C:\\Users\\ImportOrder\\.jenkins\\jobs\\ImportOrder_V1\\workspace\\mail-mgt\\src\\main\\resources\\location.properties";
	private static final String DEFAULT_PURVEYOR_ID = "1308";
	private static final String USER_EMAIL = "importorders.diningedge@gmail.com";
	private static final String USER_PASSWORD = "edge2016";

	private static final Logger logger = Logger.getLogger(MailProcessor.class);

	private String saveDirectory;
	
//	String pattern = "yyyy-MM-dd";
//	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	
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

		// while implementation
		logger.info("Connecting to gmail...");

		// Connect to GMail, enter user name and password here
		store.connect("imap.gmail.com", USER_EMAIL, USER_PASSWORD);

		logger.info("Connected to - " + store);
		return store;
	}

	/**
	 * Main method evoke search function
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {

		PropertiesManager.purveyorPropertiesFile = DEFAULT_PURVEYOR_PROPERTIES;
		PropertiesManager.locationPropertiesFile = DEFAULT_LOCATION_PROPERTIES;

		try {

			MailProcessor mailProcessor = new MailProcessor();

			mailProcessor.setSaveDirectory(DEFAUT_ATTACHMET_DIR);

			// create an Imap connection with gmail
			IMAPSSLStore store = null;
			try {
				store = mailProcessor.createConnection();
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			// process the orders from EMail
			mailProcessor.processOrdersFromEmail(store);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unknown error occured please contact developer");
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
	 * @throws IOException
	 */
	private void processOrdersFromEmail(IMAPSSLStore store) {

		IMAPFolder folderInbox = null;
		IMAPFolder folderProcessed = null;
		IMAPFolder folderUnprocessed = null;
		try {

			boolean isProcessed = false;
			// Get the folder you want to search in e.g. INBOX
			try {
				folderInbox = (IMAPFolder) store.getFolder("INBOX");
				folderProcessed = (IMAPFolder) store.getFolder("processed");
				folderUnprocessed = (IMAPFolder) store.getFolder("unprocessed");
				logger.info("Total mails in inbox are = " + folderInbox.getMessageCount());

				if (folderInbox != null) {

					logger.info("Searching started....");

					// Create GMail raw search term and use it to search in
					// folder
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

					logger.info("Total messages found for keyword are = " + messagesFound.length);
					logger.info("Messages found are:");

					List<Message> tempSuccessList = new ArrayList<>();
					List<Message> tempFailureList = new ArrayList<>();
					// Process the messages found in search
					logger.info("--------------------------------------------");
					String contentType;
					String messageContent = null;
					for (Message message : messagesFound) {
						contentType = message.getContentType();
						logger.info("# " + message.getSubject());
						OrderDetails orderDetails = null;
						try {

							if (contentType.contains("text/plain") || contentType.contains("text/html")) {
								Object content = message.getContent();
								if (content != null) {
									messageContent = content.toString();
									processOrder(messageContent);
									isProcessed = true;
								}
							}

							if (contentType.contains("multipart")) {
								Multipart multiPart = (Multipart) message.getContent();
								int numberOfParts = multiPart.getCount();
								for (int partCount = 0; partCount < numberOfParts; partCount++) {
									MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
									if (!Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
										messageContent = getTextFromMimeMultipart(part);
										if (messageContent != null) {
											try {
												orderDetails = processOrder(messageContent);
											} catch (MessagingException e) {
												// block
												e.printStackTrace();
											}
											if (orderDetails != null) {

												logger.info("Id's fetched");
											}
										}
									}
									if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
										if (orderDetails != null) {
											part.saveFile(saveDirectory + File.separator + orderDetails.getOrderId()
													+ ".csv");
											try {
												// Call Selenium ##
												SeleniumItradeIO sel = new SeleniumItradeIO();
												sel.start(orderDetails);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							}
							isProcessed = true;
						} catch (MessagingException | IndexOutOfBoundsException | IOException e) {
							e.printStackTrace();
						} catch (PurveyorNotFoundException e) {
							try {
								new Utils().sendNotification(e.getOrderId(), e.getPurveyorId(),
										NotificationEvent.FAILURE);
							} catch (IOException e1) {
								logger.info("Communication failure occured while sending failure notification");
								e1.printStackTrace();
							} catch (KeyManagementException e1) {
								e1.printStackTrace();
							} catch (NoSuchAlgorithmException e1) {
								e1.printStackTrace();
							}
							e.printStackTrace();
						}

						if (isProcessed) {
							tempSuccessList.add(message);
						} else {
							tempFailureList.add(message);
						}
						message.setFlag(Flags.Flag.DELETED, true);
					}
					Message[] tempSuccessMessageArray = tempSuccessList.toArray(new Message[tempSuccessList.size()]);
					Message[] tempFailureMessageArray = tempFailureList.toArray(new Message[tempFailureList.size()]);
					folderInbox.copyMessages(tempSuccessMessageArray, folderProcessed);
					folderInbox.copyMessages(tempFailureMessageArray, folderUnprocessed);
					logger.info("--------------------------------------------");
					folderInbox.expunge();
					logger.info("Searching done!");
				}
			} finally {
				if (folderInbox.isOpen()) {
					folderInbox.close(true);
				}
				if (folderProcessed.isOpen()) {
					folderProcessed.close(true);
				}
				if (folderUnprocessed.isOpen()) {
					folderProcessed.close(true);
				}
				store.close();
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Downloads new messages and saves attachments to disk if any.
	 *
	 * @param messageContent
	 *            the message content
	 * @return the string
	 * @throws MessagingException
	 *             the messaging exception
	 * @throws PurveyorNotFoundException
	 *             the purveyor not found exception
	 */
	/*
	 * private boolean downloadEmailAttachmentsAndProcessOrder(Message message)
	 * throws PurveyorNotFoundException, MessagingException { String
	 * messageContent = "";
	 * 
	 * // content may contain attachments Multipart multiPart = null;
	 * 
	 * try { multiPart = (Multipart) message.getContent(); int numberOfParts =
	 * multiPart.getCount(); for (int partCount = 0; partCount < numberOfParts;
	 * partCount++) { MimeBodyPart part = (MimeBodyPart)
	 * multiPart.getBodyPart(partCount);
	 * 
	 * // this part may be the message content scanner = new
	 * Scanner(part.getInputStream()); String fileName = processOrder(scanner,
	 * messageContent);
	 * 
	 * if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) { // this
	 * part is attachment fileName = part.getFileName();
	 * part.saveFile(saveDirectory + File.separator + fileName); } } } catch
	 * (IOException e) { e.printStackTrace(); }
	 * 
	 * return false; }
	 */

	/**
	 * Process order.
	 *
	 * @param scanner
	 *            the scanner
	 * @param messageContent
	 *            the message content
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws MessagingException
	 *             the messaging exception
	 * @throws PurveyorNotFoundException
	 *             the purveyor not found exception
	 */

	// private String processOrder(Scanner scanner, String messageContent)
	// throws MessagingException, PurveyorNotFoundException {
	// String purveyorId = null;
	// String locationId = null;
	// String orderId = null;
	// while (scanner.hasNextLine()) {
	// String line = scanner.nextLine();
	// if (line.startsWith("Purveyor:")) {
	// String purveyorIdRecieved = line.substring(line.indexOf("(") + 1,
	// line.indexOf(")", line.indexOf("(")));
	// purveyorId = purveyorIdRecieved.trim();
	// }
	// if (line.startsWith("Location:")) {
	// String locationIdRecieved = line.substring(line.indexOf("(") + 1,
	// line.indexOf(")", line.indexOf("(")));
	// locationId = locationIdRecieved.trim();
	// }
	// if (line.startsWith("Order #:")) {
	// orderId = line.trim();
	// }
	// }
	// if (StringUtils.isEmpty(purveyorId)) {
	// if (StringUtils.isEmpty(locationId)) {
	// throw new PurveyorNotFoundException("Location details not found in the
	// order email", 101, purveyorId,
	// orderId);
	// } // Need to consult DEFAULT_PURVEYOR_ID throw new
	// PurveyorNotFoundException("Purveyor details not found in the order
	// email", 101, DEFAULT_PURVEYOR_ID,
	// orderId);
	// }
	// PurveyorDetails purveyorDetails = null;
	// try {
	// purveyorDetails = fetchPurveyorDetailsFromSystem(purveyorId, locationId,
	// orderId);
	// if (purveyorDetails != null) {
	// try {
	// Utils.sendNotification(purveyorId, orderId, NotificationEvent.SUCCESS);
	// } catch (IOException e1) {
	// logger.info("Communication failure occured while sending success
	// notification");
	// e1.printStackTrace();
	// }
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// Selenium sel = new Selenium();
	// sel.start(purveyorDetails);
	// return orderId;
	// }

//	private boolean updateDeliveryDate(String deDate){
//		Date date = new Date();
//		DateFormat estDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		estDate.setTimeZone(TimeZone.getTimeZone("EST"));
//		String TodaysDate = simpleDateFormat.format(new Date(estDate.format(date)));
//		String Date = simpleDateFormat.format(new Date(deDate));	
//		
//		
//		
//		return false;
//		
//	}
	
	private OrderDetails processOrder(String messageContent) throws MessagingException, PurveyorNotFoundException {
		String purveyorId = null;
		String locationId = null;
		String orderId = null;
	//	String deliveryDate = "";
		OrderDetails orderDetails = null;
		messageContent = messageContent.replace("\n", "").replace("\r", "").replace("=", "");
		
		logger.info(messageContent);
		purveyorId = messageContent.substring(messageContent.indexOf("Purveyor:(") + "Purveyor:(".length(),
				messageContent.indexOf(")", messageContent.indexOf("(")));
		logger.info(purveyorId);
		locationId = messageContent.substring(messageContent.indexOf("Location:(") + "Location:(".length(),
				messageContent.indexOf(")", messageContent.indexOf("Location:(")));
		logger.info(locationId);
		orderId = messageContent.substring(messageContent.indexOf("Order #:") + "Order #:".length(),
				messageContent.indexOf("Location:("));
		orderId = orderId.replaceAll("[^0-9]", "");
		logger.info(orderId);
//		deliveryDate = messageContent.substring(messageContent.indexOf("2017-") + "2017-".length(),
//				messageContent.indexOf("Cheney"));
//		logger.info(deliveryDate);
//		updateDeliveryDate(deliveryDate);
		if (StringUtils.isNotEmpty(purveyorId)) {
			if (StringUtils.isNotEmpty(locationId)) {
				if (StringUtils.isNotEmpty(orderId)) {
					try {
						orderDetails = fetchPurveyorDetailsFromSystem(purveyorId, locationId, orderId);
						// Send Success Notification
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					throw new PurveyorNotFoundException("Order Id not found in the order email", 101, purveyorId, null);
				}
			} else {
				throw new PurveyorNotFoundException("Location details not found in the order email", 101, purveyorId,
						orderId);
			}
		} else {
			// Need to consult DEFAULT_PURVEYOR_ID
			throw new PurveyorNotFoundException("Purveyor details not found in the order email", 101,
					DEFAULT_PURVEYOR_ID, orderId);
		}

		return orderDetails;
	}

	/**
	 * @param purveyorId
	 * @param locationId
	 * @throws IOException
	 * @throws PurveyorNotFoundException
	 */
	private OrderDetails fetchPurveyorDetailsFromSystem(String purveyorId, String locationId, String orderId)
			throws IOException, PurveyorNotFoundException {
		OrderDetails orderDetails = null;
		Properties purveyorProperties = PropertiesManager.getPurveyorProperties();
		String purveyorStoreUrl = purveyorProperties.getProperty(purveyorId);
		logger.info("Purveyor URL is : " + purveyorStoreUrl);
		Properties locationProperties = PropertiesManager.getLocationProperties();
		String storeCredentials = locationProperties.getProperty(locationId);
		if (StringUtils.isNotEmpty(storeCredentials)) {
			String storeUserName = storeCredentials.split("/")[0];
			String storePassword = storeCredentials.split("/")[1];
			if (StringUtils.isEmpty(purveyorStoreUrl)) {
				if (StringUtils.isEmpty(storeUserName) || StringUtils.isEmpty(storePassword)) {
					throw new PurveyorNotFoundException("Location details does not exist in the system", 103,
							purveyorId, orderId);
				}
				throw new PurveyorNotFoundException("Location details does not exist in the system", 102, purveyorId,
						orderId);
			}
			orderDetails = new OrderDetails(purveyorStoreUrl, storeUserName, storePassword, orderId, purveyorId);
			logger.info(orderDetails);
		} else {
			throw new PurveyorNotFoundException("Location details does not exist in the system", 102, purveyorId,
					orderId);
			
		}

		return orderDetails;
	}

	private String getTextFromMimeMultipart(MimeBodyPart bodyPart) throws PurveyorNotFoundException {
		String messageContent = null;
		InputStream inputStream;
		StringBuffer responseBuffer = new StringBuffer();
		try {
			inputStream = bodyPart.getInputStream();
			byte[] temp = new byte[1024];

			int countCurrentRead;
			while ((countCurrentRead = inputStream.read(temp)) > 0) {
				responseBuffer.append(new String(temp, 0, countCurrentRead, "UTF-8"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		messageContent = responseBuffer.toString();
		return messageContent;
	}

}
