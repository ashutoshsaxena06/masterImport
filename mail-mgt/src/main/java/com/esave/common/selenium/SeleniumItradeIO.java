package com.esave.common.selenium;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
			System.setProperty("webdriver.chrome.driver",
					"C:\\Users\\ImportOrder\\Downloads\\chromedriver_win32\\chromedriver.exe");
			// RandomAction.setDownloadFilePath();
			driver = new ChromeDriver();
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			// BrowserAction.ClearBrowserCache(driver);
			driver.manage().window().maximize();

			// Enter username, pwd and Return successful
			// LoginCheney(driver, userName, password);
			driver.get("http://www.procurement.itradenetwork.com/Platform/Membership/Login");

			Thread.sleep(2000);

			// pass login credentials
			wait = new WebDriverWait(driver, 15);
			// enter username ##
			WebElement chb_Username = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'username')]"))));
			chb_Username.sendKeys(userName);

			// enter password ##
			WebElement chb_Password = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'password')]"))));
			chb_Password.sendKeys(password);

			driver.findElement(By.xpath("//input[contains(@id,'rememberMe')]")).click();

			// click login
			WebElement btn_Login = wait.until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//input[contains(@value,'Login')]"))));
			btn_Login.click();

			// logger.info("Login Successful");

			// RandomAction.isFramePresent(driver);
			Thread.sleep(2000);

			// ordering

			WebElement lnk_Ordering = wait.until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
			lnk_Ordering.click();

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
			// Thread.sleep(2000);
			// robot.keyPress(KeyEvent.VK_CONTROL);
			// robot.keyPress(KeyEvent.VK_V);
			// robot.keyRelease(KeyEvent.VK_CONTROL);
			// robot.keyRelease(KeyEvent.VK_V);
			// Thread.sleep(2000);
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
				Thread.sleep(20000);
				verifyUpload(driver);

				// Click _UpdateCart
				updateCart(driver);

			} catch (WebDriverException e) {

				logger.info("Failed !!! at veridyUpload / upDate cart");
				e.printStackTrace();
			}

			// Pop Up- confirm - Checkout2
			try {

				// Check the presence of alert
				Thread.sleep(2000);
				Alert alert = driver.switchTo().alert();
				logger.info(alert.getText());
				// if present consume the alert
				if (alert.getText().equalsIgnoreCase("Add all valid products to your cart?")) {
					alert.accept();
					Thread.sleep(3000);
				} else {
					logger.info(alert.getText());
				}
			} catch (NoAlertPresentException ex) {
				// Alert not present
				Robot robot = new Robot();
				Thread.sleep(2000);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);

				ex.printStackTrace();
			}

			Thread.sleep(2000);

			WebElement btn_GoToCart = wait.until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
			// div[@id='TitleBar']/*/*/div[@id='TitleBarActionNavButtons']/*
			if (btn_GoToCart.getText().equalsIgnoreCase("Go to Cart")) {
				btn_GoToCart.click();
				logger.info("Gotocart");
			} else {
				driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]")).click();
			}

			// Final- checkout3
			// checkOut(driver);
			Thread.sleep(2000);
			WebElement btn_CheckOut = wait.until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
			if (btn_CheckOut.getText().equalsIgnoreCase("Checkout")) {
				btn_CheckOut.click();
				logger.info("Final Checkout");
			}

			// validate/ Submit btn
			WebElement btn_SubmitOrder = wait.until(ExpectedConditions.elementToBeClickable(
					driver.findElement(By.xpath("//div[@class='orderInfo category-font']/*/div[7]"))));
			logger.info(btn_SubmitOrder.getText());
			// btn_SubmitOrder.click();

			Thread.sleep(2000);

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
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
					// Screenshot
					errorScreenshot(driver, orderID);
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
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
					// Screenshot
					errorScreenshot(driver, orderID);
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
				new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
						NotificationEvent.FAILURE);
				// Screenshot
				errorScreenshot(driver, orderID);
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
			driver.close();
		}
	}

	void errorScreenshot(WebDriver driver, String orderID) {
		// Take screenshot and store as a file format
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			// now copy the screenshot to desired location using copyFile
			// //method
			FileUtils.copyFile(src, new File("C:\\errorScreenshot\\" + orderID + ".png"));
		}

		catch (IOException e) {
			logger.info(e.getMessage());

		}
	}

}
