package com.opencart.test;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class OpenCartCrossBrowserLab9 {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;
    private final String baseUrl = "https://naveenautomationlabs.com/opencart/";

    @BeforeClass
    @Parameters("browser")
    public void setUp(String browser) {
        Reporter.log("Starting test setup for browser: " + browser, true);

        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--remote-allow-origins=*");
            driver = new ChromeDriver(chromeOptions);
        } else if (browser.equalsIgnoreCase("ie") || browser.equalsIgnoreCase("internet explorer")) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                driver = new InternetExplorerDriver();
            } else {
                Reporter.log("Non-Windows OS detected: Using Microsoft Edge for IE suite.", true);
                driver = new EdgeDriver();
            }
        } else if (browser.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }

        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        Reporter.log("Browser successfully initialized: " + browser, true);
    }

    /**
     * Helper to reliably open Desktops -> Mac across all browsers
     */
    private void navigateToMacCategory() {
        WebElement desktopsMenu = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav[@id='menu']//a[contains(text(), 'Desktops')]"))
        );
        
        // Hover over Desktops
        actions.moveToElement(desktopsMenu).perform();

        try {
            // Attempt normal click
            WebElement macLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href, 'category&path=20_27') or contains(text(), 'Mac')]"))
            );
            macLink.click();
        } catch (Exception e) {
            // Fallback for Chromium-based drivers: direct JS click bypasses hover state timing
            WebElement macLink = driver.findElement(By.xpath("//a[contains(@href, 'category&path=20_27') or contains(text(), 'Mac')]"));
            js.executeScript("arguments[0].click();", macLink);
        }

        // Assert Heading
        WebElement macHeading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='content']//h2[text()='Mac']"))
        );
        Assert.assertEquals(macHeading.getText(), "Mac", "Mac category heading mismatch.");
        Reporter.log("Navigated to Mac category successfully.", true);
    }

    @Test(priority = 1, description = "Lab 3: Add Mac desktop to cart after sorting")
    public void testDesktopMacAddToCart() {
        Reporter.log("--- Executing Lab 3 Flow ---", true);
        driver.get(baseUrl);

        // 1. Desktops -> Mac
        navigateToMacCategory();

        // 2. Sort by Name (A - Z)
        WebElement sortDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-sort")));
        new Select(sortDropdown).selectByVisibleText("Name (A - Z)");

        // 3. Add to Cart
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//div[@class='product-thumb']//button[contains(@onclick, 'cart.add')])[1]")
        ));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        addToCartBtn.click();

        // 4. Verify notification
        WebElement successAlert = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success"))
        );
        Assert.assertTrue(successAlert.getText().contains("Success: You have added"), 
            "Success notification missing.");
        Reporter.log("Lab 3 Flow passed.", true);
    }

    @Test(priority = 2, description = "Lab 4: Mac flow and search with descriptions")
    public void testDesktopMacAndSearchMonitors() {
        Reporter.log("--- Executing Lab 4 Flow ---", true);
        driver.get(baseUrl);

        // 1. Verify Title
        Assert.assertEquals(driver.getTitle(), "Your Store", "Title mismatch.");

        // 2. Desktops -> Mac
        navigateToMacCategory();

        // 3. Sort By Name (A - Z)
        WebElement sortDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-sort")));
        new Select(sortDropdown).selectByVisibleText("Name (A - Z)");

        // 4. Add to Cart
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//div[@class='product-thumb']//button[contains(@onclick, 'cart.add')])[1]")
        ));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        addToCartBtn.click();

        // 5. Search 'Monitors'
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));
        searchInput.clear();
        searchInput.sendKeys("Monitors");
        driver.findElement(By.cssSelector("button.btn-default")).click();

        // 6. Clear and refill Search Criteria
        WebElement criteriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-search")));
        criteriaInput.clear();
        criteriaInput.sendKeys("Monitors");

        // 7. Check 'Search in product descriptions'
        WebElement descCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("description")));
        if (!descCheckbox.isSelected()) {
            descCheckbox.click();
        }
        Assert.assertTrue(descCheckbox.isSelected(), "Description checkbox is not checked.");

        // 8. Click Search Button
        driver.findElement(By.id("button-search")).click();

        // 9. Verify results heading
        WebElement searchResultsHeading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Products meeting the search criteria') or text()='Search - Monitors'] | //div[@id='content']/h1"))
        );
        Assert.assertTrue(searchResultsHeading.isDisplayed(), "Search result header not displayed.");
        Reporter.log("Lab 4 Flow passed.", true);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        Reporter.log("Browser session closed cleanly.", true);
    }
}