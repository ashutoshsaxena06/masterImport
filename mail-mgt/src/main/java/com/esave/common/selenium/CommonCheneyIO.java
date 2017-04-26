package com.esave.common.selenium;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CommonCheneyIO {

	public WebDriverWait wait;
	public WebDriver driver;
	private static final Logger logger = Logger.getLogger(CommonCheneyIO.class);

	public void validateOrderStatus(WebDriver driver) throws InterruptedException {
		// verification
		if (driver
				.findElement(By
						.xpath("//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-draggable']"))
				.isDisplayed()) {
			RandomAction.isIframePresent(driver);
			driver.switchTo().frame(driver.findElement(By.xpath(
					"//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-draggable']/div[1]/iframe")));
			logger.info("iFrame captured");
			WebElement orderText = driver
					.findElement(By.xpath("//div[@id='orderdetails']/div[1]/div[contains(.,'has been processed']"));
			logger.info(orderText.getText());

			logger.info("#Success");

		}

	}

	public void validateOrder(WebDriver driver) throws InterruptedException {

		if (driver.getCurrentUrl()
				.equalsIgnoreCase("procurement.itradenetwork.com/Platform/Orders/Checkout/SelectSubmit")) {
			// Submit ---#s
			submitOrder(driver);

		} else {
			Thread.sleep(2000);
			logger.info(driver.getCurrentUrl());
			// Submit ---#
			submitOrder(driver);
		}

	}

	public void submitOrder(WebDriver driver) {
		// validate/ Submit btn
		WebElement btn_SubmitOrder = wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.xpath("//div[@class='orderInfo category-font']/*/div[7]"))));
		logger.info(btn_SubmitOrder.getText());
		if (btn_SubmitOrder.getText().equalsIgnoreCase("Validate/Submit")) {
			btn_SubmitOrder.click();
		}
	}

	// Checkout - btn
	public void checkOut(WebDriver driver) throws InterruptedException {

		Thread.sleep(2000);
		WebElement btn_CheckOut = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
		if (btn_CheckOut.getText().equalsIgnoreCase("Checkout")) {
			btn_CheckOut.click();
			logger.info("Final Checkout");
		}
	}

	public void goToCart(WebDriver driver) {

		try {
			WebElement btn_GoToCart = wait.until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
			// div[@id='TitleBar']/*/*/div[@id='TitleBarActionNavButtons']/*
			if (btn_GoToCart.getText().equalsIgnoreCase("Go to Cart")) {
				btn_GoToCart.click();
				logger.info("Gotocart");
			} else {
				driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]")).click();
			}
		} catch (WebDriverException e) {
			e.printStackTrace();
		}
	}

	public boolean addProductsToCartPopUp(WebDriver driver) throws InterruptedException {

		try {

			// Check the presence of alert
			Alert alert = driver.switchTo().alert();
			logger.info(alert.getText());
			// if present consume the alert
			if (alert.getText().equalsIgnoreCase("Add all valid products to your cart?")) {
				alert.accept();
				Thread.sleep(3000);
				return true;
			} else {
				logger.info(alert.getText());
				return false;
			}
		} catch (NoAlertPresentException ex) {
			// Alert not present
			ex.printStackTrace();
			return false;
		}
	}

	public void updateCart(WebDriver driver) throws InterruptedException {

		try {
			// Click _UpdateCart
			clickUpdatecart(driver);

		} catch (NoSuchElementException ne) {
			logger.info("lnk_UpdateCart not clicked - NoSuchElementException");
			ne.printStackTrace();

		} catch (TimeoutException te) {
			logger.info("lnk_UpdateCart not clicked - TimeoutException");
			te.printStackTrace();

		} catch (WebDriverException e) {
			logger.info("lnk_UpdateCart not clicked - WebDriverException");
			e.printStackTrace();

		}

	}

	public void clickUpdatecart(WebDriver driver) throws InterruptedException {
		// get Link text
		WebElement lnk_UpdateCart = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(
				By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a"))));
		logger.info("Link text : " + lnk_UpdateCart.getAttribute("title"));

		Thread.sleep(3000);
		// Click
		if (lnk_UpdateCart.getAttribute("title").equalsIgnoreCase("Update Cart")) {
			WebElement btn_UpdatecCart = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath(
					"//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a/*/*"))));
			btn_UpdatecCart.click();
			logger.info("Clicked on Update Cart");
		} else {
			driver.findElement(
					By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a/*/*"))
					.click();
		}
	}

	public boolean uploadFile(Transferable ss) throws InterruptedException {

		try {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
			Robot robot = new Robot();
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_V);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			robot.keyRelease(KeyEvent.VK_V);
			Thread.sleep(2000);
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
			return true;
		} catch (HeadlessException e) {
			logger.info("Desktop window upload failed");

			return false;
		} catch (AWTException e) {
			logger.info("Desktop window upload failed");
			return false;
		}
	}

	public WebDriver Preconditions() {

		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\ImportOrder\\Downloads\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver(options);

		return driver;

	}

	public boolean LoginCheney(WebDriver driver, String User, String pwd) throws InterruptedException {
		driver.get("http://www.procurement.itradenetwork.com/Platform/Membership/Login");

		Thread.sleep(2000);

		// pass login credentials
		wait = new WebDriverWait(driver, 15);
		// enter username ##
		WebElement chb_Username = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'username')]"))));
		chb_Username.sendKeys(User);

		// enter password ##
		WebElement chb_Password = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'password')]"))));
		chb_Password.sendKeys(pwd);

		driver.findElement(By.xpath("//input[contains(@id,'rememberMe')]")).click();

		// click login
		WebElement btn_Login = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//input[contains(@value,'Login')]"))));
		btn_Login.click();

		// logger.info("Login Successful");

		return true;

	}

	public void verifyUpload(WebDriver driver) {
		ArrayList<WebElement> importedItems = new ArrayList<>(
				wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(
						By.xpath("//div[@id='DataEntryGrid']/div[@class=\"t-grid-content\"]/table/tbody/*")))));
		if (importedItems.size() == 1) {
			logger.info("No items imported");
		} else {
			logger.info("Imported Items :- " + importedItems.size());
		}

	}

	public void enterPoNumber(WebDriver driver, String poNum) {
		try {
			WebElement poNumber = wait.until(ExpectedConditions.visibilityOf(
					driver.findElement(By.xpath("//input[@class='poNumber maxLengthRestriction OptionalField']"))));
			poNumber.sendKeys(poNum);
			logger.info("Updated PO# field");
			// input[@class='poNumber maxLengthRestriction OptionalField']
		} catch (NoSuchElementException ne) {
			logger.info("PO# - not Updated");
			ne.printStackTrace();
		} catch (WebDriverException we) {
			logger.info("PO# - not Updated");
			we.printStackTrace();
		} catch (Exception e) {
			logger.info("PO# - not Updated");
			e.printStackTrace();
		}
	}

	public void errorScreenshot(WebDriver driver, String orderID) {
		// Take screenshot and store as a file format
		File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			// now copy the screenshot to desired location using copyFile
			// //method
			FileUtils.copyFile(src, new File("C:\\errorScreenshot\\" + orderID + ".png"));
		}

		catch (IOException e) {
			logger.info("Screenshot failed");
			e.printStackTrace();
		}
	}
}
