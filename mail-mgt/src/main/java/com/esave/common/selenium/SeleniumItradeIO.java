package com.esave.common.selenium;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;

public class SeleniumItradeIO extends CommonCheneyIO {

	private static final Logger logger = Logger.getLogger(SeleniumItradeIO.class);
	private static String loginFail = "failed due to : Incorrect Username/Password";

	/*
	 * private WebDriverWait wait; private WebDriver driver;
	 */

	public void start(OrderDetails orderDetails) {

		String userName = orderDetails.getUserName();
		String password = orderDetails.getPassword();
		String orderID = orderDetails.getOrderId();
		String filepath = "C:\\orders\\";
		String date = orderDetails.getDeliverydate();
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
			if (LoginCheney(driver, userName, password)) {
				logger.info("Login successful !");
			} else {
				throw new ImportOrderException(loginFail, 1001);
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

				addProductsToCartPopUp(driver);

					// Go To Cart
					goToCart(driver);
			} catch (WebDriverException e) {
				e.printStackTrace();
			}

			// Verify Items at Cart page
			verifyCartItems(driver, importItemQty);

			Thread.sleep(2000);
			// Final- checkout3
			checkOut(driver);

			Thread.sleep(3000);

			if (!orderDetails.getUserName().equalsIgnoreCase("60036371CBI")) {
				Thread.sleep(3000);
				separateInvoice(driver);
			}

			// PO number
			enterPoNumber(driver, orderID);

			// Delivery date
			enterDeliverydate(driver, date);
			// if (orderDetails.getUserName().equalsIgnoreCase("60008181CBI")) {
			// }
			Thread.sleep(3000);

			pressEscape();
			// validate/ Submit btn
			submitOrder(driver);
			
			Thread.sleep(5000);
			errorScreenshot(driver, orderID);


			Thread.sleep(10000);

			checkOrderStatus();

//			 validateOrderImport(driver, orderID);

			// notification
			sendOrderStatusMail(orderDetails, "Success");

		} catch (ImportOrderException i) {
			i.printStackTrace();
			if (orderDetails != null) {
				SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), loginFail);
			}
			errorScreenshot(driver, orderID);
		} catch (Exception ex) {
			logger.info("Failed !!!!" + ex.getMessage());
			ex.printStackTrace();
			// notification
			if (orderDetails != null) {
				SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), "Order Import Failed");
			}
			errorScreenshot(driver, orderID);
		} finally {
			// Choose Logout option
			driver.close();
		}
	}

	public void checkOrderStatus() {
		logger.info("Checking order status ... ");
		if (RandomAction.isIframePresent(driver)) {
			driver.switchTo().frame("openValidationSubmitFrame");		
			WebElement orderStatus = Wait(30).until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(
					"//div[@id='MainContentContainer']/div[@id='orderdetails']/div[@class='orderTitle important-bg category-font']/div[@id ='orderSubmitedTitle']"))));
			logger.info(orderStatus.getText());
		}else {
			logger.info("No iframe is present");
		}
		// div ValidationSubmitDialog //iframe[id openValidationSubmitFrame]
		// div[class= main ]/div [id=MainContentContainer]/ div[id= orderdetails]/ div
		// [class= orderTitle important-bg category-font]/ div [id = orderSubmitedTitle]

	}

}
