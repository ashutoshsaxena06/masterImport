package com.esave.common.selenium;

import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;

import junit.framework.Assert;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

public class SeleniumItradeIO {

	private WebDriverWait wait;
	private WebDriver driver;
	public static final String DIR = "C:\\orders\\";

	@Test
	public void startUp() throws InterruptedException {

		// Launch setProperty for chrome, Launch, Implicit wait & maximize
		// Browser
		driver = Preconditions();

		// Enter username, pwd and Return successful
		try {
			if (LoginCheney(driver) == false) {
				throw new ImportOrderException("Account login failure -", 202);
			} else {
				System.out.println("login success");

			}
		} catch (ImportOrderException e) {
			System.out.println("Account login failure -" + e.getMessage());

		}

		// RandomAction.isFramePresent(driver);
		try {
			OrderPushSteps(driver);
		} catch (ImportOrderException e) {
			Thread.sleep(2000);
			OrderPushSteps(driver);
			System.out.println("Exception while calling OrderPushSteps " + e.getMessage());
		}

	}

	private void OrderPushSteps(WebDriver driver2) throws InterruptedException {

		Thread.sleep(2000);
		// ordering
		WebElement lnk_Ordering = wait
				.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//a[contains(.,'Ordering')]"))));
		lnk_Ordering.click();

		// **** Order Products / Entry ***
		List<WebElement> allElements = wait.until(ExpectedConditions.visibilityOfAllElements(
				driver.findElements(By.xpath("//a[contains(.,'Ordering')]/following-sibling::div/ul/li/*/*/div/a"))));
		System.out.println(allElements.size());

		for (WebElement element : allElements) {

			if (element.getText().equalsIgnoreCase("Order Products / Entry")) {
				String OG_text = element.getText();
				element.click();
				System.out.println("Clicked on link - " + OG_text);
				break;
			}

		}

		// Thread.sleep(2000);
		// Upload btn click
		Actions act = new Actions(driver);

		String filename = "45100.csv";

		String path = DIR + filename;

		StringSelection ss = new StringSelection(DIR + filename);
		System.out.println(path);

		act.moveToElement(driver.findElement(
				By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[1]"))).click()
				.build().perform();

		Assert.assertEquals(uploadFile(ss), true);
		System.out.println("OrderFile uploaded");
		
		// Update cart- Checkout1
		updateCart(driver);
		
		//Pop Up- confirm - Checkout2
		addProductsToCartPopUp(driver);
		
		//GoToCart - CheckOut3
		goToCart(driver);

	}

	public void goToCart(WebDriver driver) {
		
		System.out.println("Time to click Gotocart");
		
	}

	public void addProductsToCartPopUp(WebDriver driver2) {
		try {
			Alert alert = driver.switchTo().alert();
			System.out.println(alert.getText());
			alert.accept();
			System.out.println(alert.getText());
			alert.dismiss();
		} catch (NoAlertPresentException e) {
			System.out.println("No Alert found -" + e);
		}
		
	}

	public void updateCart(WebDriver driver) throws InterruptedException {

		try {
			//Click _UpdateCart
			clickUpdatecart();

		} catch (NoSuchElementException ne) {
			Thread.sleep(5000);
			//Click _UpdateCart
			clickUpdatecart();
			System.out.println("lnk_UpdateCart not clicked - NoSuchElementException");

		} catch (TimeoutException te) {
			Thread.sleep(5000);
			//Click _UpdateCart
			clickUpdatecart();
			System.out.println("lnk_UpdateCart not clicked - TimeoutException");

		} catch (WebDriverException e) {
			Thread.sleep(5000);
			//Click _UpdateCart
			clickUpdatecart();
			System.out.println("lnk_UpdateCart not clicked - WebDriverException");

		}

	}



	public void clickUpdatecart() throws InterruptedException {
		// get Link text
		WebElement lnk_UpdateCart = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(
				By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a"))));
		System.out.println("Link text : " + lnk_UpdateCart.getAttribute("title"));

		Thread.sleep(5000);
		// Click
		if (lnk_UpdateCart.getAttribute("title").equalsIgnoreCase("Update Cart")) {
			WebElement btn_UpdatecCart = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a/*/*"))));
			btn_UpdatecCart.click();
			System.out.println("Clicked on Update Cart");
		} else {
			driver.findElement(By
					.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[7]/a/*/*"))
					.click();
		}		
	}

	private boolean uploadFile(Transferable ss) throws InterruptedException {

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

		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\ashsaxen\\Downloads\\chromedriver_win32\\chromedriver.exe");
		// RandomAction.setDownloadFilePath();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		// BrowserAction.ClearBrowserCache(driver);
		driver.manage().window().maximize();

		return driver;

	}

	public boolean LoginCheney(WebDriver driver) throws InterruptedException {
		driver.get("http://www.procurement.itradenetwork.com/Platform/Membership/Login");

		Thread.sleep(2000);

		// pass login credentials
		wait = new WebDriverWait(driver, 15);
		// enter username
		WebElement chb_Username = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'username')]"))));
		chb_Username.sendKeys("60023492CBI");

		// enter password
		WebElement chb_Password = wait.until(
				ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[contains(@id,'password')]"))));
		chb_Password.sendKeys("Sbpc2349");

		driver.findElement(By.xpath("//input[contains(@id,'rememberMe')]")).click();

		// click login
		WebElement btn_Login = wait.until(ExpectedConditions
				.elementToBeClickable(driver.findElement(By.xpath("//input[contains(@value,'Login')]"))));
		btn_Login.click();

		// System.out.println("Login Successful");

		return true;

	}

	public void start(OrderDetails orderDetails) {
		// TODO Auto-generated method stub

	}
}
