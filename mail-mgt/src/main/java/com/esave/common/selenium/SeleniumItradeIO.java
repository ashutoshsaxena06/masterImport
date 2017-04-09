package com.esave.common.selenium;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

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
		// Actual File path ##
		String filename = filepath + orderID + ".csv";

		logger.info(userName + " : " + password + " and " + filename);
		try {


			// Launch setProperty for chrome, Launch, Implicit wait & maximize
			// Browser
			driver = Preconditions();

			// Enter username, pwd and Return successful
			// LoginCheney(driver, userName, password);
			try {
				System.out.println("Login successful :- " + LoginCheney(driver, userName, password));
			} catch (WebDriverException e) {
				System.out.println("Login failed");
				e.getMessage();
			}

			Thread.sleep(3000);

			// ordering
			WebElement lnk_Ordering = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
			lnk_Ordering.click();
			
			Thread.sleep(3000);

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

			Thread.sleep(3000);
			// Upload btn click

			WebElement uploadForm = driver.findElement(By.xpath("//form[@id='uploadForm']/input[@id='fileInput']"));
			uploadForm.sendKeys(filename);

			logger.info("OrderFile uploaded :-" + filename);

			// StringSelection ss = new StringSelection(filename);
			//
			// act.moveToElement(driver.findElement(
			// By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate
			// rtbItem'][2]/following-sibling::li[1]")))
			// .click().build().perform();
			//
			// // uploadFile(ss);
			// try {
			// Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,
			// null);
			// Robot robot = new Robot();
			// Thread.sleep(3000);
			// robot.keyPress(KeyEvent.VK_CONTROL);
			// robot.keyPress(KeyEvent.VK_V);
			// robot.keyRelease(KeyEvent.VK_CONTROL);
			// robot.keyRelease(KeyEvent.VK_V);
			// Thread.sleep(3000);
			// robot.keyPress(KeyEvent.VK_ENTER);
			// robot.keyRelease(KeyEvent.VK_ENTER);
			// } catch (HeadlessException e) {
			// logger.info("Desktop window upload failed");
			//
			// } catch (AWTException e) {
			// logger.info("Desktop window upload failed");
			// }

			try {
				// get Link text
				Thread.sleep(3000);
				verifyUpload(driver);

				// Click _UpdateCart
				updateCart(driver);

			} catch (WebDriverException e) {

				logger.info("Failed !!! at verifyUpload / upDate Cart");
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

			// Final- checkout3
			checkOut(driver);
		//	errorScreenshot(driver, orderID);
			Thread.sleep(3000);
			
			enterPoNumber(driver, orderID);
	
			Thread.sleep(3000);
			// validate/ Submit btn
			WebElement btn_SubmitOrder = wait.until(ExpectedConditions.elementToBeClickable(
					driver.findElement(By.xpath("//div[@class='orderInfo category-font']/*/div[7]"))));
			logger.info(btn_SubmitOrder.getText());
			Thread.sleep(3000);
			
			btn_SubmitOrder.click();

			Thread.sleep(3000);

			// Confirm Order Status
			// validateOrderStatus(driver);
			// if (driver
			// .findElement(By
			// .xpath("//div[@class='ui-dialog ui-widget ui-widget-content
			// ui-corner-all ui-front ui-draggable']"))
			// .isDisplayed()) {
			// try {
			// RandomAction.isIframePresent(driver);
			// driver.switchTo().frame(driver.findElement(By.xpath(
			// "//div[@class='ui-dialog ui-widget ui-widget-content
			// ui-corner-all ui-front ui-draggable']/div[1]/iframe")));
			// logger.info("iFrame captured");
			// WebElement orderText = driver.findElement(
			// By.xpath("//div[@id='orderdetails']/div[1]/div[contains(.,'has
			// been processed']"));
			// logger.info(orderText.getText());
			//
			// logger.info("#Success");
			//
			// if (orderDetails != null) {
			// try {
			// new Utils().sendNotification(orderDetails.getOrderId(),
			// orderDetails.getPurveyorId(),
			// NotificationEvent.SUCCESS);
			// } catch (IOException e1) {
			// logger.info("Communication failure occured while sending success
			// notification");
			// e1.printStackTrace();
			// }
			// }
			// } catch (Exception e) {
			// if (orderDetails != null) {
			// try {
			// new Utils().sendNotification(orderDetails.getOrderId(),
			// orderDetails.getPurveyorId(),
			// NotificationEvent.SUCCESS);
			// } catch (IOException e1) {
			// logger.info("Communication failure occured while sending success
			// notification");
			// e1.printStackTrace();
			// }
			// }
			// // TODO Auto-generated catch block
			//
			// e.printStackTrace();
			// }
			//
			// }

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
					// send failure
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
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
			errorScreenshot(driver, orderID);
			driver.close();
		}
	}



}
