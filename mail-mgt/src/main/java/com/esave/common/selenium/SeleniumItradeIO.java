package com.esave.common.selenium;

import java.awt.datatransfer.StringSelection;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.esave.entities.OrderDetails;
import com.esave.exception.ImportOrderException;

import junit.framework.Assert;

public class SeleniumItradeIO extends CommonCheneyIO {

	private WebDriverWait wait;
	private WebDriver driver;

	public void start(OrderDetails orderDetails) {

		String userName = orderDetails.getUserName();
		String password = orderDetails.getPassword();
		String orderID = orderDetails.getOrderId();

		System.out.println(userName + " : " + password + " and " + orderID);
		try {
			// Launch setProperty for chrome, Launch, Implicit wait & maximize
			// Browser
			driver = Preconditions();

			// Enter username, pwd and Return successful
			try {
				if (LoginCheney(driver, userName, password) == false) {
					throw new ImportOrderException("Account login failure -", 202);
				} else {
					System.out.println("login success");

				}
			} catch (ImportOrderException e) {
				System.out.println("Account login failure -" + e.getMessage());

			}

			// RandomAction.isFramePresent(driver);
			try {
				OrderPushSteps(driver, orderID);
			} catch (ImportOrderException e) {
				Thread.sleep(2000);
				OrderPushSteps(driver, orderID);
				System.out.println("Exception while calling OrderPushSteps " + e.getMessage());
			}
		} catch (InterruptedException e) {
			System.out.println("Failed !!!!" + e.getMessage());
		} catch (WebDriverException e) {
			System.out.println("Failed !!!!" + e.getMessage());
		} finally {

		}

	}

	public void OrderPushSteps(WebDriver driver, String orderNo) throws InterruptedException {

		String filepath = "C:\\orders\\";
		// Actual File path ##
		String filename = filepath + orderNo;
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

		System.out.println(filename);
		StringSelection ss = new StringSelection(filename);

		act.moveToElement(driver.findElement(
				By.xpath("//ul[@class='rtbUL']/li[@class='rtbTemplate rtbItem'][2]/following-sibling::li[1]"))).click()
				.build().perform();

		Assert.assertEquals(uploadFile(ss), true);
		System.out.println("OrderFile uploaded");

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

	}



}
