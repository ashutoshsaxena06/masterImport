package com.esave.common.selenium;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.esave.common.NotificationEvent;
import com.esave.common.Utils;
import com.esave.entities.OrderDetails;

public class SeleniumItradeIO extends CommonCheneyIO {

	private static final Logger logger = Logger.getLogger(SeleniumItradeIO.class);

	/*
	 * private WebDriverWait wait; private WebDriver driver;
	 */

	public void start(OrderDetails orderDetails) {

		String userName = orderDetails.getUserName();
		String password = orderDetails.getPassword();
		String orderID = orderDetails.getOrderId();
		String filepath = "C:\\orders\\";
		String date = orderDetails.getDeliverydate();
		String locationID = orderDetails.getPurveyorId();
		// Actual File path ##
		String filename = filepath + orderID + ".csv";
		int importItemQty = 0;

		logger.info(userName + " : " + password + " and " + filename);
		logger.info("Order delivery date is : " + date);
		try {

			// Launch setProperty for chrome, Launch, Implicit wait & maximize

			// #Step 1 - // Browser
			driver = Preconditions();

			// Enter username, pwd and Return successful
			// #Step 2 - LoginCheney(driver, userName, password);
			try {
				System.out.println("Login successful :- " + LoginCheney(driver, userName, password));
			} catch (WebDriverException e) {
				System.out.println("Login failed");
				e.getMessage();
			}

			Thread.sleep(3000);
			// Check and Empty all Items from Cart
			checkAndEmptyCart();

			Thread.sleep(3000);

			// #Step 3 - ordering
			OrderEntry();

			Thread.sleep(3000);

			// #Step 4 - Upload btn click
			uploadFile(driver, filename);

			try {
				// get Link text
				Thread.sleep(20000);
				importItemQty = verifyUpload(driver);

				// Click _UpdateCart
				updateCart(driver);

			} catch (WebDriverException e) {

				logger.info("Failed !!! at verifyUpload / UpDate Cart");
				e.printStackTrace();
			}

			// Pop Up- confirm - Checkout2
			try {

				if (addProductsToCartPopUp(driver) == true) {
					// Go To Cart
					goToCart(driver);
					// errorScreenshot(driver, orderID);
				}
			} catch (WebDriverException e) {
				e.printStackTrace();
			}

			// Verify Items at Cart page
			verifyCartItems(driver, importItemQty);

			Thread.sleep(2000);
			// Final- checkout3
			checkOut(driver);

			Thread.sleep(3000);

			// PO number
			enterPoNumberandInvoice(driver, orderID);

			Thread.sleep(3000);

			// Delivery date
			if (orderDetails.getUserName().equalsIgnoreCase("60008181CBI")) {
				enterDeliverydate(driver, date);
			}

			// validate/ Submit btn
			submitOrder(driver);

			Thread.sleep(10000);
			
			validateOrderImport(driver, orderID);

			if (orderDetails != null) {
				try {
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.SUCCESS);
					SendMailSSL.sendMailAction(locationID, orderID, "Success!");
				} catch (IOException e1) {
					logger.info("Communication failure occured while sending success notification");
					e1.printStackTrace();
				}
			}
		} catch (InterruptedException e) {

			logger.info("Failed !!!!" + e.getMessage());
			if (orderDetails != null) {
				try {
					// send failure
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
					SendMailSSL.sendMailAction(locationID, orderID, "Failure!");
				} catch (IOException e1) {
					logger.info("Communication failure occured while sending success notification");
					e1.printStackTrace();
				} catch (KeyManagementException e1) {
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
			}

		} catch (WebDriverException e) {
			logger.info("Failed !!!!" + e.getMessage());
			if (orderDetails != null) {
				try {
					// send Failure
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
					SendMailSSL.sendMailAction(locationID, orderID, "Failure!");
				} catch (IOException e1) {
					logger.info("Communication failure occured while sending success notification");
					e1.printStackTrace();
				} catch (KeyManagementException e1) {
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e1) {
					e1.printStackTrace();
				}
			}

		} catch (Exception ex) {
			logger.info("Failed !!!!" + ex.getMessage());
			ex.printStackTrace();
			try {
				// send failure
				new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
						NotificationEvent.FAILURE);
				SendMailSSL.sendMailAction(locationID, orderID, "Failure!");
			} catch (IOException e1) {
				logger.info("Communication failure occured while sending success notification");
				e1.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		} finally {
			// Choose Logout option
			// errorScreenshot(driver, orderID);
			//driver.close();
		}
	}

	
}
