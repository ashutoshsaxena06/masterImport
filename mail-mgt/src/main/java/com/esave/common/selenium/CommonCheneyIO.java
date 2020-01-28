package com.esave.common.selenium;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.esave.common.NotificationEvent;
import com.esave.common.Utils;
import com.esave.entities.OrderDetails;

public class CommonCheneyIO {

	public WebDriver driver;
	private static final Logger logger = Logger.getLogger(CommonCheneyIO.class);

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

		WebElement btn_SubmitOrder = Wait(30).until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//div[contains(text(),'Validate/Submit')]"))));
		logger.info(btn_SubmitOrder.getText());
			btn_SubmitOrder.click();
	}

	// Checkout - btn
	public void checkOut(WebDriver driver) throws InterruptedException {
		// Shoppingcart
		try {
			WaitForPageToLoad(30);
			PageExist("Shopping Cart");
			Thread.sleep(3000);

			WebElement btn_CheckOut = Wait(30).until(ExpectedConditions
					.visibilityOf(driver.findElement(By.xpath("//div[@class='right-arrow-text'][1]"))));
			if (btn_CheckOut.getText().equalsIgnoreCase("Checkout")) {
				Thread.sleep(3000);
				Wait(30).until(ExpectedConditions.elementToBeClickable(btn_CheckOut));
				btn_CheckOut.click();
				logger.info("Final Checkout");
			}
		} catch (Exception e) {
			e.printStackTrace();
			driver.get("https://www.procurement.itradenetwork.com/Platform/Orders/Checkout/SelectSubmit");
			Thread.sleep(3000);
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
			e.printStackTrace();
			Thread.sleep(2000);
			driver.get("http://www.procurement.itradenetwork.com/Platform/Products/Cart/Details");
			logger.info("Launched url for cart page");
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
				logger.info("ALert pop up accepted - Items added to cart");
				// OrderEntry
				Thread.sleep(3000);
				return true;
			} else {
				logger.info(alert.getText());
				alert.accept();
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

	public void validateOrderImport(WebDriver driver, String orderID) {
		// home
		try {
			driver.get("https://www.procurement.itradenetwork.com/Platform/Membership/Dashboard/Detail");
			WebElement orderNumber = Wait(30).until(ExpectedConditions
					.visibilityOf(driver.findElement(By.xpath("//tr/td/a[contains(.,'" + orderID + "')]"))));
			String status = orderNumber.getText();
			logger.info("Order number imported : " + status);
		} catch (Exception e) {
			//
			logger.info("Order number not found");
			e.printStackTrace();
		}
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

	public void separateInvoice(WebDriver driver) {
		try {
			// Checkout
			WaitForPageToLoad(30);
			PageExist("Checkout");

			Thread.sleep(2000);
			WebElement separateInvoice = Wait(30).until(ExpectedConditions
					.elementToBeClickable(driver.findElement(By.xpath("//input[@class='separateInvoice']"))));
			separateInvoice.click();
			logger.info("Separate Invoice Choosen");
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.info("failed to choose separate invoice");
		}
	}

	public void enterPoNumber(WebDriver driver, String poNum) {
		try {
			// Checkout
			WaitForPageToLoad(30);
			PageExist("Checkout");
			// po#
			WebElement poNumber = Wait(30).until(ExpectedConditions.visibilityOf(
					driver.findElement(By.xpath("//input[@class='poNumber maxLengthRestriction OptionalField']"))));
			poNumber.sendKeys(poNum);

			int retry = 0;
			while (retry < 3) {
				if (!poNumber.getAttribute("value").isEmpty()) {
					logger.info("PO# not empty : " + poNumber.getAttribute("value"));
					break;
				} else {
					WebElement poNumber_absolute = Wait(30).until(ExpectedConditions.visibilityOf(driver.findElement(By
							.xpath("//*[@id='MainContentContainer']/div[1]/ul/li/div/ul/li/div[2]/div[1]/div[1]/div[1]/input"))));
					poNumber_absolute.sendKeys(poNum);
				}
				Thread.sleep(1000);
				logger.info("PO# is empty, retry -" + retry);
				retry++;
			}
			logger.info("Updated PO# field : " + poNum);

		} catch (Exception e) {
			e.printStackTrace();
			logger.info("PO# - not Updated");
			errorScreenshot(driver, poNum);
		}
	}

	public void errorScreenshot(WebDriver driver, String orderID) {
		// Take screenshot and store as a file format
		// WaitForPageToLoad(30);

		try {
			// now copy the screenshot to desired location using copyFile
			// //method
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

			FileUtils.copyFile(src, new File("C:\\Users\\ImportOrder\\Log\\" + orderID + ".png")); // System.currentTimeMillis()
		}

		catch (Exception e) {
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
		// PageExist("Home");
		// Home
		WaitForPageToLoad(30);

		String ttl = driver.getTitle();

		if (ttl.equalsIgnoreCase("Home")) {
			PageExist("Home");
		} else if (ttl.equalsIgnoreCase("Shopping Cart")) {
			PageExist(" Cart");
		}

		try {
			Thread.sleep(3000);
			// ordering
			WebElement lnk_Ordering = Wait(30).until(
					ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
			lnk_Ordering.click();

			Thread.sleep(3000);

			// **** Order Products / Entry ***
			List<WebElement> allElements = Wait(30).until(ExpectedConditions.visibilityOfAllElements(driver
					.findElements(By.xpath("//a[contains(.,'Ordering')]/following-sibling::div/ul/li/*/*/div/a"))));
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
				driver.get("http://www.procurement.itradenetwork.com/Platform/Products/ProductEntry/Details");
			} catch (Exception e1) {
				e1.printStackTrace();
				logger.info("Attempt 3- Using cart to Order");
				cartToOrder();
			}
		}
	}

	public void cartToOrder() throws InterruptedException {
		WebElement img_cartIcon = Wait(30).until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//*[@id='cartInfo']/div[1]/a/img"))));
		img_cartIcon.click();
		Thread.sleep(2000);
		WebElement btn_ContinueShopping = Wait(30).until(ExpectedConditions
				.visibilityOf(driver.findElement(By.xpath("//*[@id='TitleBarActionNavButtons']/div[2]"))));
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
			} else if ((importedItemsToCart.size() - 1) == importItemQty) {
				logger.info("All Items Imported to Cart:- " + importItemQty);
			} else {
				logger.info("Items uploaded - " + importItemQty + " Imported Items to Cart - "
						+ (importedItemsToCart.size() - 1) + " Not Equal !!!");
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
			// logger.info(cartQty);
			// Cart is not Empty
			if (cartQty != 0) {

				logger.info("Items already in Cart - " + cartQty);
				Thread.sleep(3000);
				driver.get("http://www.procurement.itradenetwork.com/Platform/Products/Cart/Details");
				// WebElement img_cartIcon = Wait(30).until(ExpectedConditions
				// .visibilityOf(driver.findElement(By.xpath(".//*[@id='cartInfo']/*/*/a/img"))));
				// imghpty Cart
				emptyCart(driver);

			} else {
				logger.info("No Items present in Cart - " + CartQty);
			}
		} catch (Exception e) {
			logger.info("Failed at check and empty cart ");
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
			logger.info("Failed at Empty cart");

		}
	}

	public void enterDeliverydate(WebDriver driver, String date) {
		try {
			if (date!=null) {
				String mm = date.substring(0, 2);
				String dd = date.substring(3, 5);
				if (dd.charAt(0) == '0') {
					dd = dd.substring(1);
				}
				System.out.println("Date : " + dd + " and Month : " + mm);
				Thread.sleep(2000);

				WebElement lnk_dd = Wait(30).until(ExpectedConditions
						.visibilityOf(driver.findElement(By.xpath("//input[@class='deliveryDate']"))));
				String actDate = lnk_dd.getAttribute("value");
				logger.info("date in iTrade : " + actDate);
				if (!actDate.equalsIgnoreCase(date)) {
					// Calender open
					WebElement cln = Wait(30).until(ExpectedConditions
							.elementToBeClickable(driver.findElement(By.xpath("//img[@title='Choose Date']"))));
					cln.click();
					driver.switchTo().frame("editDeliveryDateDialogFrame");
					String actMM = date.substring(0, 2);
					// month compare
					if (!actMM.equalsIgnoreCase(mm)) {
						Thread.sleep(2000);
						// Next month
						driver.findElement(By.xpath("//*[@id='NextMonthButton']/span")).click();
					} else {
						logger.info("Delivery in same month");
					}
					Thread.sleep(2000);
					// deliver date entry
					inputDeliverydate(driver, dd);
					logger.info("Delivery date is entered : " + date);
				} else {
					logger.info("Delivery dates are same !");
				}
			} else {
				logger.info("Delivery date is Null");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Not able to input Delivery date in App");
			pressEscape();
		} finally {
			try {
				Thread.sleep(2000);
				driver.switchTo().defaultContent();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void pressEscape() {
		try {
			Actions action = new Actions(driver);
			action.sendKeys(Keys.ESCAPE).build().perform();
			// driver.findElement(By.xpath("//button[@title='close']/span")).click();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void inputDeliverydate(WebDriver driver, String dd) {
		List<WebElement> li = Wait(30).until(ExpectedConditions.visibilityOfAllElements(
				driver.findElements(By.xpath("//td/a[@class='t-link t-action-link Dates_Selectable']"))));
		logger.info(li.size());

		if (!(li.size() == 0)) {
			for (WebElement wb : li) {
				logger.info(wb.getText());
				if (wb.getText().equals(dd)) {
					wb.click();
					break;
				}
			}
		}
	}

	public void sendOrderStatusMail(OrderDetails orderDetails, String status) {
		if (orderDetails != null) {
			try {
				if (!status.equalsIgnoreCase("Success")) {
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.FAILURE);
					SendMailSSL.sendFailedOrder(orderDetails.getOrderId(), status);
				} else {
					new Utils().sendNotification(orderDetails.getOrderId(), orderDetails.getPurveyorId(),
							NotificationEvent.SUCCESS);
					SendMailSSL.sendMailAction(orderDetails.getOrderId(), "Success");
				}
			} catch (Exception e1) {
				logger.info("Communication failure occured while sending " + status + " notification");
				e1.printStackTrace();
			}
		}
	}

}
