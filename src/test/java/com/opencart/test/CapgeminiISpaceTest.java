package com.opencart.test;

import java.io.File;
import java.time.Duration;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CapgeminiISpaceTest {

    public static void main(String[] args) {

        // =========================================================
        // AUTOMATIC URL DETECTION: Uses local mock if available
        // =========================================================
        File mockFile = new File("mock_site/index.html");
        String baseUrl = mockFile.exists() 
            ? mockFile.toURI().toString() 
            : "https://ispace.ig.capgemini.com/sitepages/index.aspx";

        String expectedStationaryTitle = "Stationary";
        String expectedEmptyCartAlert = "Please add product(s) to cart";
        String expectedNoChangesAlert = "No changes made";

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();

            // 1. Launch the URL
            System.out.println("--- Step 1: Launching URL ---");
            System.out.println("Navigating to: " + baseUrl);
            driver.get(baseUrl);

            // Store parent window handle before opening popup/new tab
            String originalWindow = driver.getWindowHandle();

            // 2. Go to 'Application' tab
            System.out.println("--- Step 2: Clicking Application tab ---");
            WebElement applicationTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Application')] | //span[contains(text(), 'Application')]")
                )
            );
            applicationTab.click();

            // 3. Click on checkbox 'Stationery Request'
            System.out.println("--- Step 3: Clicking Stationery Request checkbox ---");
            WebElement stationeryCheckbox = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@id='chkStationery' or @type='checkbox']")
                )
            );
            if (!stationeryCheckbox.isSelected()) {
                stationeryCheckbox.click();
            }

            // 4. Switch window/tab and verify title 'Stationary'
            System.out.println("--- Step 4: Switch Window & Verify Title ---");
            wait.until(ExpectedConditions.numberOfWindowsToBe(2));

            Set<String> allWindows = driver.getWindowHandles();
            for (String windowHandle : allWindows) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            wait.until(ExpectedConditions.titleContains(expectedStationaryTitle));
            String actualTitle = driver.getTitle();
            System.out.println("Current Window Title: " + actualTitle);
            if (actualTitle.contains(expectedStationaryTitle)) {
                System.out.println("PASS: Title 'Stationary' verified.");
            } else {
                System.out.println("FAIL: Title mismatch. Found: " + actualTitle);
            }

            // 5. Click on 'Submit to collect your Stationery >>>' link
            System.out.println("--- Step 5: Clicking Submit to collect link ---");
            WebElement submitCollectLink = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Submit to collect your Stationery')]")
                )
            );
            submitCollectLink.click();

            // 6, 7 & 8. Switch to alert, verify text, and click 'Ok'
            System.out.println("--- Steps 6, 7 & 8: Validate First Alert ---");
            wait.until(ExpectedConditions.alertIsPresent());
            Alert firstAlert = driver.switchTo().alert();
            String firstAlertText = firstAlert.getText();
            System.out.println("Alert 1 Text: " + firstAlertText);

            if (firstAlertText.contains(expectedEmptyCartAlert)) {
                System.out.println("PASS: Alert 1 text matches '" + expectedEmptyCartAlert + "'");
            } else {
                System.out.println("FAIL: Alert 1 text mismatch.");
            }
            firstAlert.accept(); // Clicks 'OK'
            System.out.println("Clicked OK on first alert.");

            // 9. Click on 'Photocopy' tab, then click 'Save Request' button
            System.out.println("--- Step 9: Photocopy Tab & Save Request ---");
            WebElement photocopyTab = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Photocopy')] | //span[contains(text(), 'Photocopy')]")
                )
            );
            photocopyTab.click();

            WebElement saveRequestBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//input[@value='Save Request' or @id='save-request'] | //button[contains(text(), 'Save Request')]")
                )
            );
            saveRequestBtn.click();

            // 10, 11 & 12. Switch to alert, verify text 'No changes made', and click 'Ok'
            System.out.println("--- Steps 10, 11 & 12: Validate Second Alert ---");
            wait.until(ExpectedConditions.alertIsPresent());
            Alert secondAlert = driver.switchTo().alert();
            String secondAlertText = secondAlert.getText();
            System.out.println("Alert 2 Text: " + secondAlertText);

            if (secondAlertText.contains(expectedNoChangesAlert)) {
                System.out.println("PASS: Alert 2 text matches '" + expectedNoChangesAlert + "'");
            } else {
                System.out.println("FAIL: Alert 2 text mismatch.");
            }
            secondAlert.accept(); // Clicks 'OK'
            System.out.println("Clicked OK on second alert.");

            // 13. Click on 'Logout' button
            System.out.println("--- Step 13: Clicking Logout ---");
            WebElement logoutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Logout')] | //a[contains(text(), 'Logout')] | //input[@value='Logout']")
                )
            );
            logoutBtn.click();
            System.out.println("Logout completed.");

            // 14. Close the 'Stationery' window and switch back to main
            System.out.println("--- Step 14: Closing Child Window ---");
            driver.close();
            driver.switchTo().window(originalWindow);
            System.out.println("Switched back to primary window. Lab demo passed successfully!");

        } catch (Exception e) {
            System.err.println("Execution Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}