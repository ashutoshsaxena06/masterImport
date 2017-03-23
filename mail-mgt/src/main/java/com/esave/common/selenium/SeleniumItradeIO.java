package com.esave.common.selenium;

import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esave.common.NotificationEvent;
import com.esave.common.Utils;
import com.esave.entities.OrderDetails;

public class SeleniumItradeIO extends CommonCheneyIO {
	
	private static final Logger logger = Logger.getLogger(SeleniumItradeIO.class);

	private WebDriverWait wait;
	private WebDriver driver;

	public void start(OrderDetails orderDetails) {

		String userName = orderDetails.getUserName();
		String password = orderDetails.getPassword();
		String orderID = orderDetails.getOrderId();
		String filepath = "C:\\orders\\";
		// Actual File path ##
		String filename = filepath + orderID + ".csv";

		logger.info(userName + " : " + password + " and " + filename);
		try {
			// Launch setProperty for chrome, Launch, Implicit wait & maximize
			// Browser
			driver = Preconditions();

			// Enter username, pwd and Return successful
			LoginCheney(driver, userName, password);

			// RandomAction.isFramePresent(driver);
			Thread.sleep(2000);

			// ordering
			// WebElement lnk_Ordering = wait.until(
			// ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
			// lnk_Ordering.click();

			// **** Order Products / Entry ***
			List<WebElement> allElements = wait.until(ExpectedConditions.visibilityOfAllElements(driver
					.findElements(By.xpath("//a[contains(.,'Ordering')]/following-sibling::div/ul/li/*/*/div/a"))));
			logger.info(allElements.size());

			for (WebElement element : allElements) {

				if (element.getText().equalsIgnoreCase("Order Products / Entry")) {
					String OG_text = element.getText();
					element.click();
					logger.info("Clicked on link - " + OG_text);
					break;
				}

			}

			// Thread.sleep(2000);
			// Upload btn click
			Actions act = new Actions(driver);

			logger.info(filename);
			StringSelection ss = new StringSelection(filename);

			act.moveToElement(driver.findElement(
					By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[1]")))
					.click().build().perform();

			uploadFile(ss);

			logger.info("OrderFile uploaded");

			// Update cart- Checkout1
			updateCart(driver);

			// Pop Up- confirm - Checkout2
			if (addProductsToCartPopUp(driver) == true) {
				// Go To Cart
				goToCart(driver);
			}
			// else {
			//
			// }
			// Final- checkout3
			checkOut(driver);

			// Validate/ Submit Order
			validateOrder(driver);

			// Confirm Order Status
			validateOrderStatus(driver);

			if (orderDetails != null) {
				try {
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.SUCCESS);
				} catch (IOException e1) {
					logger.info("Communication failure occured while sending success notification");
					e1.printStackTrace();
				}
			}

		} catch (InterruptedException e) {

			logger.info("Failed !!!!" + e.getMessage());
			if (orderDetails != null) {
				try {
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
				} catch (IOException e1) {
					logger.info("Communication failure occured while sending success notification");
					e1.printStackTrace();
				}
			}

		} catch (WebDriverException e) {
			logger.info("Failed !!!!" + e.getMessage());
			if (orderDetails != null) {
				try {
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
				} catch (IOException e1) {
					logger.info("Communication failure occured while sending success notification");
					e1.printStackTrace();
				}
			}

		} catch (Exception ex) {
			logger.info("Failed !!!!" + ex.getMessage());
			try {
				new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
						NotificationEvent.FAILURE);
			} catch (IOException e1) {
				logger.info("Communication failure occured while sending success notification");
				e1.printStackTrace();
			}

		} finally {
			// driver.switchTo().parentFrame();
			// Choose Logout option
			try {
				WebElement lnk_Logout = wait.until(ExpectedConditions
						.elementToBeClickable(driver.findElement(By.xpath("//a[contains(.,'Logout')]"))));
				lnk_Logout.click();
				driver.quit();
			} catch (WebDriverException e) {
				driver.quit();
				e.printStackTrace();
			}
		}

	}

	// Order push Steps

	// public void OrderPushSteps(WebDriver driver, String orderNo) throws
	// InterruptedException {
	//
	// }

}
