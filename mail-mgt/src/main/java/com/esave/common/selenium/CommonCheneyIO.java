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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

public class CommonCheneyIO {

	public WebDriver driver;
	private static final Logger logger =	 Logger.getLogger(CommonCheneyIO.class);

	// public void validateOrderStatus(WebDriver driver) throws
	// InterruptedException {
	// // verification
	// if (driver
	// .findElement(By
	// .xpath("//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all
	// ui-front ui-draggable']"))
	// .isDisplayed()) {
	// RandomAction.isIframePresent(driver);
	// driver.switchTo().frame(driver.findElement(By.xpath(
	// "//div[@class='ui-dialog ui-widget ui-widget-content ui-corner-all
	// ui-front ui-draggable']/div[1]/iframe")));
	// logger.info("iFrame captured");
	// WebElement orderText = driver
	// .findElement(By.xpath("//div[@id='orderdetails']/div[1]/div[contains(.,'has
	// been processed']"));
	// logger.info(orderText.getText());
	//
	// logger.info("#Success");
	//
	// }
	//
	// }

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
		// Checkout
		WaitForPageToLoad(30);

		// validate/ Submit btn
		WebElement btn_SubmitOrder = Wait(30).until(ExpectedConditions.elementToBeClickable(
				driver.findElement(By.xpath("//div[@class='orderInfo category-font']/*/div[7]"))));
		logger.info(btn_SubmitOrder.getText());
		if (btn_SubmitOrder.getText().equalsIgnoreCase("Validate/Submit")) {
			btn_SubmitOrder.click();
		}
	}

	// Checkout - btn
	public void checkOut(WebDriver driver) throws InterruptedException {
		// Shoppingcart
		WaitForPageToLoad(30);
		PageExist("Shopping Cart");
		Thread.sleep(3000);

		WebElement btn_CheckOut = Wait(30).until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
		if (btn_CheckOut.getText().equalsIgnoreCase("Checkout")) {
			Thread.sleep(3000);
			Wait(30).until(ExpectedConditions.elementToBeClickable(btn_CheckOut));
			btn_CheckOut.click();
			logger.info("Final Checkout");
		}
	}

	public void goToCart(WebDriver driver) throws InterruptedException {

		try {
			// OrderEntry
			WaitForPageToLoad(30);
			PageExist("Order Products / Entry");

			WebElement btn_GoToCart = Wait(30).until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
			// div[@id='TitleBar']/*/*/div[@id='TitleBarActionNavButtons']/*
			if (btn_GoToCart.getText().equalsIgnoreCase("Go to Cart")) {
				btn_GoToCart.click();
				logger.info("Gotocart");
			} else {
				driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]")).click();
			}
		} catch (Exception e) {
			Thread.sleep(2000);
			driver.findElement(By.xpath(".//*[@id='cartInfo']/*/*/a/img")).click();

			logger.info("clicked Cart image");
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
				// OrderEntry
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

		} catch (Exception e) {
			logger.info("lnk_UpdateCart not clicked - WebDriverException");
			e.printStackTrace();

		}

	}

	public void clickUpdatecart(WebDriver driver) throws InterruptedException {
		// get Link text
		WebElement lnk_UpdateCart = Wait(30).until(ExpectedConditions.elementToBeClickable(driver.findElement(
				By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a"))));
		logger.info("Link text : " + lnk_UpdateCart.getAttribute("title"));

		Thread.sleep(3000);
		// Click
		if (lnk_UpdateCart.getAttribute("title").equalsIgnoreCase("Update Cart")) {
			WebElement btn_UpdatecCart = Wait(30).until(ExpectedConditions.elementToBeClickable(driver.findElement(By
					.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a/*/*"))));
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
		// Login
		WaitForPageToLoad(30);
		
		Thread.sleep(3000);

		PageExist("Login");
		// pass login credentials
		// wait = new WebDriverWait(driver, 15);
		Thread.sleep(3000);
		// enter username ##
		WebElement chb_Username = Wait(30).until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'username')]"))));
		chb_Username.sendKeys(User);

		// enter password ##
		WebElement chb_Password = Wait(30).until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'password')]"))));
		chb_Password.sendKeys(pwd);

		driver.findElement(By.xpath("//input[contains(@id,'rememberMe')]")).click();

		// click login
		WebElement btn_Login = Wait(30).until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//input[contains(@value,'Login')]"))));
		btn_Login.click();

		// logger.info("Login Successful");

		return true;

	}

	public int verifyUpload(WebDriver driver) throws InterruptedException {

		try {
			// OrderEntry
			WaitForPageToLoad(30);
			PageExist("Order Products / Entry");

			// On OrderEntry
			ArrayList<WebElement> importedItems = new ArrayList<>(
					Wait(30).until(ExpectedConditions.visibilityOfAllElements(driver.findElements(
							By.xpath("//div[@id='DataEntryGrid']/div[@class='t-grid-content']/table/tbody/*")))));
			if (importedItems.size() <= 1) {
				logger.info("No items imported");
			} else {
				logger.info("Imported Items :- " + importedItems.size());
			}
			return importedItems.size();
			
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}

	}

	public void enterPoNumber(WebDriver driver, String poNum) {
		try {
			// Checkout
			WaitForPageToLoad(30);
			PageExist("Checkout");

			WebElement poNumber = Wait(30).until(ExpectedConditions.visibilityOf(
					driver.findElement(By.xpath("//input[@class='poNumber maxLengthRestriction OptionalField']"))));
			poNumber.sendKeys(poNum);
			logger.info("Updated PO# field");
			// input[@class='poNumber maxLengthRestriction OptionalField']
		} catch (org.openqa.selenium.NoSuchElementException Ne) {
			logger.info("PO# - not Updated");
			Ne.printStackTrace();
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
		WaitForPageToLoad(30);

		try {
			// now copy the screenshot to desired location using copyFile
			// //method
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			FileUtils.copyFile(src, new File("C:\\errorScreenshot\\" + orderID + ".png"));
		}

		catch (IOException e) {
			logger.info("Screenshot failed");
			e.printStackTrace();
		}
	}

	public boolean PageExist(String expectedTitle) throws InterruptedException {

		try {
			for (int i = 0; i < 3; i++) {
				String act = getDriver().getTitle();
				if (act.equals(expectedTitle)) {
					logger.info("current page - " + expectedTitle);
					return true;
				} else {
					Thread.sleep(2000);
					logger.info("waiting for page.. ");
				}
			}
			logger.info("Not reached page - " + expectedTitle);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	public void WaitForPageToLoad(int... waitTime) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};

		if (waitTime.length > 0) {
			Wait(waitTime).until(expectation);
		} else {
			Wait(30).until(expectation);
		}

	}

	public Wait<WebDriver> Wait(int... waitTime) {
		int waitTimeInSeconds;
		if (waitTime.length > 0) {
			waitTimeInSeconds = waitTime[0];
		} else {
			waitTimeInSeconds = 5;
		}
		return new FluentWait<WebDriver>(getDriver()).withTimeout(waitTimeInSeconds, TimeUnit.SECONDS)
				.pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class)
				.ignoring(StaleElementReferenceException.class).ignoring(WebDriverException.class);
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void OrderEntry() throws InterruptedException {
		try {
			// Home
			WaitForPageToLoad(30);
			
			String ttl= driver.getTitle();
			
			if (ttl.equalsIgnoreCase("Home")) {		
				PageExist("Home");	
			} else if (ttl.equalsIgnoreCase("Shopping Cart")){
				PageExist("Shopping Cart");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	//	PageExist("Home");

		try {
			Thread.sleep(3000);
			// ordering
			WebElement lnk_Ordering = Wait(30)
					.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
			lnk_Ordering.click();

			Thread.sleep(3000);

			// **** Order Products / Entry ***
			List<WebElement> allElements = Wait(30).until(ExpectedConditions.visibilityOfAllElements(
					driver.findElements(By.xpath("//a[contains(.,'Ordering')]/following-sibling::div/ul/li/*/*/div/a"))));
			logger.info(allElements.size());

			Thread.sleep(3000);

			for (WebElement element : allElements) {

				if (element.getText().equalsIgnoreCase("Order Products / Entry")) {
					String OG_text = element.getText();
					element.click();
					logger.info("Clicked on link - " + OG_text);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Attempt 2- Using different locator");
			try {
				Thread.sleep(3000);
				// ordering
				WebElement lnk_Ordering = Wait(30)
						.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
				lnk_Ordering.click();
				WebElement lnk_OrderEntry = Wait(30)
						.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id='NavigationMenu-2']/div[3]/div/a"))));
				lnk_OrderEntry.click();
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.info("Attempt 3- Using cart to Order");
				cartToOrder();
			}
		}
	}

	public void cartToOrder() throws InterruptedException {
		WebElement img_cartIcon = Wait(30).until(ExpectedConditions
				.visibilityOf(driver.findElement(By.xpath("//*[@id='cartInfo']/div[1]/a/img"))));
		img_cartIcon.click();
		Thread.sleep(2000);
		WebElement btn_ContinueShopping = Wait(30)
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id='TitleBarActionNavButtons']/div[2]"))));
		btn_ContinueShopping.click();
	}

	public void uploadFile(WebDriver driver, String filename) throws InterruptedException {
		// OrderEntry
		WaitForPageToLoad(30);
		PageExist("Order Products / Entry");

		// Thread.sleep(2000);
		// Upload btn click

		logger.info(filename);

		WebElement uploadForm = driver.findElement(By.xpath("//form[@id='uploadForm']/input[@id='fileInput']"));
		uploadForm.sendKeys(filename);

		logger.info("OrderFile uploaded");
	}

	public void verifyCartItems(WebDriver driver, Integer importItemQty) throws InterruptedException {
		try {
			WaitForPageToLoad(30);
			PageExist("Shopping Cart");
			
			Thread.sleep(3000);
			ArrayList<WebElement> importedItemsToCart = new ArrayList<>(Wait(30).until(ExpectedConditions
					.visibilityOfAllElements(driver.findElements(By.xpath(".//*[@id='CartGrid']/*/table/tbody/*")))));
			if (importedItemsToCart.size() <= 1) {
				logger.info("No items imported to Cart");
			} else if ((importedItemsToCart.size()-1) == importItemQty) {
				logger.info("All Items Imported to Cart:- " + importItemQty);
			} else {
				logger.info("Items uploaded - " + importItemQty + " Imported Items to Cart - "
						+ (importedItemsToCart.size()-1) + " Not Equal !!!");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void checkAndEmptyCart() throws InterruptedException {
		try {
			// CartIcon Items Quantity
			String CartQty = Wait(30)
					.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(".//*[@id='ItemCountLabel']"))))
					.getText();
			int cartQty = Integer.parseInt(CartQty);
			//logger.info(cartQty);
			// Cart is not Empty
			if (cartQty != 0) {
				
				logger.info("Items already in Cart - " +cartQty);
				WebElement img_cartIcon = Wait(30).until(ExpectedConditions
						.visibilityOf(driver.findElement(By.xpath(".//*[@id='cartInfo']/*/*/a/img"))));
				img_cartIcon.click();

				// Empty Cart
				emptyCart(driver);

			} else {
				logger.info("No Items present in Cart - "+ CartQty);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void emptyCart(WebDriver driver) {
		try {
			// Shoppingcart
			WaitForPageToLoad(30);
			PageExist("Shopping Cart");
			Thread.sleep(3000);

			// Empty Cart
			WebElement img_RemoveCart = Wait(30).until(ExpectedConditions
					.visibilityOf(driver.findElement(By.xpath(".//*[@id='RemoveFromCart']/*/*/*/*/*"))));
			img_RemoveCart.click();
			WebElement ddl_ClearCart = Wait(30)
					.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(".//*[@id='ClearCart']/*"))));
			ddl_ClearCart.click();

			Thread.sleep(3000);
			// Check the presence of alert
			Alert alert = driver.switchTo().alert();
			logger.info(alert.getText());
			// if present consume the alert
			if (alert.getText().equalsIgnoreCase("Remove all items from cart?")) {
				alert.accept();
				logger.info("Removed All Items from Cart");
			} else {
				Thread.sleep(3000);
				alert.accept();
				logger.info("Removed All Items from Cart");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
