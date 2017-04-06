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

	public void validateOrderStatus(WebDriver driver) throws InterruptedException {
		// verification
		if (driver
				.findElement(By
						.xpath("//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-draggable']"))
				.isDisplayed()) {
			RandomAction.isIframePresent(driver);
			driver.switchTo().frame(driver.findElement(By.xpath(
					"//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all ui-front ui-draggable']/div[1]/iframe")));
			System.out.println("iFrame captured");
			WebElement orderText = driver
					.findElement(By.xpath("//div[@id='orderdetails']/div[1]/div[contains(.,'has been processed']"));
			System.out.println(orderText.getText());

			System.out.println("#Success");

		}

	}

	public void validateOrder(WebDriver driver) throws InterruptedException {

		if (driver.getCurrentUrl()
				.equalsIgnoreCase("procurement.itradenetwork.com/Platform/Orders/Checkout/SelectSubmit")) {
			// Submit ---#s
			submitOrder(driver);

		} else {
			Thread.sleep(2000);
			System.out.println(driver.getCurrentUrl());
			// Submit ---#
			submitOrder(driver);
		}

	}

	public void submitOrder(WebDriver driver) {
		// validate/ Submit btn
		WebElement btn_SubmitOrder = wait.until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.xpath("//div[@class='orderInfo category-font']/*/div[7]"))));
		System.out.println(btn_SubmitOrder.getText());
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
			System.out.println("Final Checkout");
		}
	}

	public void goToCart(WebDriver driver) {

		try {
			WebElement btn_GoToCart = wait.until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
			// div[@id='TitleBar']/*/*/div[@id='TitleBarActionNavButtons']/*
			if (btn_GoToCart.getText().equalsIgnoreCase("Go to Cart")) {
				btn_GoToCart.click();
				System.out.println("Gotocart");
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
			System.out.println(alert.getText());
			// if present consume the alert
			if (alert.getText().equalsIgnoreCase("Add all valid products to your cart?")) {
				alert.accept();
				Thread.sleep(3000);
				return true;
			} else {
				System.out.println(alert.getText());
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
			System.out.println("lnk_UpdateCart not clicked - NoSuchElementException");
			ne.printStackTrace();

		} catch (TimeoutException te) {
			System.out.println("lnk_UpdateCart not clicked - TimeoutException");
			te.printStackTrace();

		} catch (WebDriverException e) {
			System.out.println("lnk_UpdateCart not clicked - WebDriverException");
			e.printStackTrace();

		}

	}

	public void clickUpdatecart(WebDriver driver) throws InterruptedException {
		// get Link text
		WebElement lnk_UpdateCart = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(
				By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a"))));
		System.out.println("Link text : " + lnk_UpdateCart.getAttribute("title"));

		Thread.sleep(3000);
		// Click
		if (lnk_UpdateCart.getAttribute("title").equalsIgnoreCase("Update Cart")) {
			WebElement btn_UpdatecCart = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath(
					"//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a/*/*"))));
			btn_UpdatecCart.click();
			System.out.println("Clicked on Update Cart");
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
			System.out.println("Desktop window upload failed");

			return false;
		} catch (AWTException e) {
			System.out.println("Desktop window upload failed");
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

		// System.out.println("Login Successful");

		return true;

	}

	public void verifyUpload(WebDriver driver) {
		ArrayList<WebElement> importedItems = new ArrayList<>(
				wait.until(ExpectedConditions.visibilityOfAllElements(driver.findElements(
						By.xpath("//div[@id='DataEntryGrid']/div[@class=\"t-grid-content\"]/table/tbody/*")))));
		if (importedItems.size() == 1) {
			System.out.println("No items imported");
		} else {
			System.out.println("Imported Items :- " + importedItems.size());
		}

	}

	public void enterPoNumber(WebDriver driver, String poNum) {
		try {
			WebElement poNumber = wait.until(ExpectedConditions.visibilityOf(
					driver.findElement(By.xpath("//input[@class='poNumber maxLengthRestriction OptionalField']"))));
			poNumber.sendKeys(poNum);
			// input[@class='poNumber maxLengthRestriction OptionalField']
		} catch (NoSuchElementException ne) {
			ne.printStackTrace();
		} catch (WebDriverException we) {
			we.printStackTrace();
		} catch (Exception e) {
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
			System.out.println("Screenshot failed");
			e.printStackTrace();
		}
	}
}
