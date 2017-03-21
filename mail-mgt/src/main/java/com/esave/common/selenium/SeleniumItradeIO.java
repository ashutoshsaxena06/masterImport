package com.esave.common.selenium;

import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;
import com.esave.mail.MailProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

	//@Test
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
			e.printStackTrace();
		}
		
		RandomAction.isFramePresent(driver);
		try {
			OrderPushSteps(driver);
		} catch (ImportOrderException e) {
			System.out.println("");
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
		
		//Thread.sleep(2000);
		// Upload btn click
		Actions act = new Actions(driver);
		//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem']/input[@type='text']
		//iframe[@id='uploadTarget']/preceding-sibling::*/*/input[@id='fileInput']
		WebElement uploadFile = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//li[10]/input"))));
		if (uploadFile.isEnabled()==false){
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("document.querySelector(\"button[id=fileName]\").click()");
		}
		else {
			act.moveToElement(uploadFile).sendKeys("C:/orders/order.csv");
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
		// Wait For Page To Loads
		// if
		// (driver.getCurrentUrl().equalsIgnoreCase("https://sts.gemalto.com/adfs/ls/"))
		// {
		// driver.findElement(By.xpath("//input[@id='userNameInput']")).sendKeys("ashsaxen@gemalto.com");
		// driver.findElement(By.xpath("//input[@id='passwordInput']")).sendKeys("Companyof4");
		// driver.findElement(By.xpath("//span[@id='submitButton']")).click();
		// }

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

		//System.out.println("Login Successful");

		return true;

	}

	public void start(OrderDetails orderDetails) {
		// TODO Auto-generated method stub

	}
}
