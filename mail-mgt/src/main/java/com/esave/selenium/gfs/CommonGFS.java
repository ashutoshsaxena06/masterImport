package com.esave.selenium.gfs;

import java.awt.Robot;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.esave.common.selenium.ErrRemedy;
import com.esave.common.selenium.RandomAction;

public class CommonGFS {

	public void LogOutGFSAccount(WebDriver driver) {

		try {
			driver.findElement(By.xpath("//a[@class='account-menu-toggle']")).click();

			driver.findElement(By.xpath("//li[@class='logout-option']")).click();

			driver.findElement(By.xpath("//div/a[@class='confirm blue button ng-binding']")).click();
		} catch (Exception e2) {

			driver.quit();

			e2.printStackTrace();
		}
		driver.quit();
	}

	private static void isAlertPresent(WebDriver driver) {
		try {
			driver.findElement(By.xpath("//button[@class='button gfsexperience-modal-close']")).click();
		} catch (Exception e) {
			System.out.println("No Alert present !!" + e.getMessage());
			e.printStackTrace();
		}

	}

	public void CheckExportOGStatus(WebDriver driver) {

		if (driver.findElement(By.className("dialogWindow smaller")).isDisplayed()) {

			String ExportStatus = driver.findElement(By.xpath("//div[@class='dialogWindow smaller']")).getText();

			System.out.println(ExportStatus);

			driver.findElement(By.xpath("//div[@class='dialogWindow smaller'][contains(.,'OK')]")).click();

			if (ExportStatus.equalsIgnoreCase("Export Failed")) {

				System.out.println("Order Guide Export failed");
				ErrRemedy.ErrScreenshotCapture(driver);
				;
				ErrRemedy.ErrReportingMail();

			} else {
				Robot robot = null;
				RandomAction.DownloadOG(robot);
				// RenameDownloadedOG();

			}

		}
	}

	public void DialogWinExportOG(WebDriver driver) {

		if (driver.findElement(By.className("dialogWindow")).isDisplayed()) {

			System.out.println("Export list window is displayed.");

			driver.findElement(By.xpath("//span[contains(.,'Price')]")).click();

			driver.findElement(By.xpath("//button[contains(.,'Export')]")).click();

			CheckExportOGStatus(driver);
		}

		else {
			System.out.println("Select data columns dialog window is not popped up");
		}
	}

}
