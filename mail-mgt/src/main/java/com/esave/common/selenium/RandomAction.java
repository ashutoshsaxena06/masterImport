package com.esave.common.selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RandomAction {

//	public static File WaitForNewFile(Path folder, String extension, int timeout_sec)
//			throws InterruptedException, IOException {
//		long end_time = System.currentTimeMillis() + timeout_sec * 1000;
//		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
//			folder.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
//			for (WatchKey key; null != (key = watcher.poll(end_time - System.currentTimeMillis(),
//					TimeUnit.MILLISECONDS)); key.reset()) {
//				for (WatchEvent<?> event : key.pollEvents()) {
//					File file = folder.resolve(((WatchEvent<Path>) event).context()).toFile();
//					if (file.toString().toLowerCase().endsWith(extension.toLowerCase()))
//						return file;
//				}
//			}
//		}
//		return null;
//	}
//
//	public static void DownloadOG(Robot robot) {
//
//		try {
//			robot = new Robot();
//			Thread.sleep(2000); // Thread.sleep throws InterruptedException
//			robot.keyPress(KeyEvent.VK_DOWN); // press arrow down key of
//												// keyboard to navigate and
//												// select Save radio button
//
//			Thread.sleep(2000); // sleep has only been used to showcase each
//								// event separately
//			robot.keyPress(KeyEvent.VK_TAB);
//			Thread.sleep(2000);
//			robot.keyPress(KeyEvent.VK_TAB);
//			Thread.sleep(2000);
//			robot.keyPress(KeyEvent.VK_TAB);
//			Thread.sleep(2000);
//			robot.keyPress(KeyEvent.VK_ENTER);
//			// press enter key of keyboard to perform above selected action
//			System.out.println("File is downloaded");
//
//		} catch (AWTException e) {
//			ErrRemedy.ErrReportingMail();
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			ErrRemedy.ErrReportingMail();
//			e.printStackTrace();
//		}
//	}

	public static boolean isIframePresent(WebDriver driver) throws InterruptedException {
		//
		// driver.findElement(By.xpath("//html/body/table/tbody/tr[2]/td[1]/div/div[2]/table/tbody/tr[1]/td/input")).click();
		Thread.sleep(3000);
		// List to get & store frame
		List<WebElement> ele = driver.findElements(By.tagName("iframe"));
		System.out.println("Number of frames in a page :" + ele.size()); // ele.size
																			// -
																			// size
																			// of
																			// frame
																			// list

		if (ele.size() == 0) {
			System.out.println("No iframes on this page");
			return false; // No frames
		} else {
			System.out.println("iFrames present on this page, Below are the details -");

			for (WebElement el : ele) {
				// Returns the Id of a frame
				System.out.println("iFrame Id :" + el.getAttribute("id"));
				// Returns the Name of a frame.
			//	System.out.println("iFrame name :" + el.getAttribute("name"));
			}
			return true; // frames present
		}

	}


	public void checkAllCheckBoxes(WebDriver driver) {

		List<WebElement> chk_ItemList = driver.findElements(By.xpath("//input[@class='CheckBox']"));
		for (WebElement chk_CurrentItem : chk_ItemList) {
			if (!chk_CurrentItem.isSelected()) {
				chk_CurrentItem.click();
			}
		}

	}

}
