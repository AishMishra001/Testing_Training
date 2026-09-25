package com.opencart.test;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class OpenCartTestNGLab8 {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;
    private JavascriptExecutor js;
    private final String baseUrl = "https://naveenautomationlabs.com/opencart/";

    @BeforeClass
    public void setUp() {
        Reporter.log("Initializing Firefox WebDriver session...", true);
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
        Reporter.log("Browser launched and maximized.", true);
    }

    /**
     * Lab 3 Flow:
     * Open URL -> Go to Desktops -> Click Mac -> Sort By Name(A-Z) -> Click Add to Cart
     */
    @Test(priority = 1, description = "Lab 3: Verify adding Mac desktop to cart after sorting")
    public void testDesktopMacAddToCart() {
        Reporter.log("--- Starting Lab 3 Test Flow ---", true);
        
        // 1. Open the URL on Firefox
        driver.get(baseUrl);
        Reporter.log("Navigated to: " + baseUrl, true);

        // 2. Go to 'Desktops' tab and click 'Mac'
        WebElement desktopsMenu = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Desktops']"))
        );
        actions.moveToElement(desktopsMenu).perform();
        Reporter.log("Hovered over 'Desktops' menu tab.", true);

        WebElement macOption = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Mac')]"))
        );
        macOption.click();
        Reporter.log("Clicked on 'Mac' link from dropdown.", true);

        // Verify 'Mac' heading presence
        WebElement macHeading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='content']//h2[text()='Mac']"))
        );
        Assert.assertEquals(macHeading.getText(), "Mac", "Heading mismatch: 'Mac' was expected.");
        Reporter.log("Assertion PASSED: Mac category heading is displayed.", true);

        // 3. Select 'Name (A - Z)' from the 'Sort By' dropdown
        WebElement sortDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-sort")));
        Select selectSort = new Select(sortDropdown);
        selectSort.selectByVisibleText("Name (A - Z)");
        Reporter.log("Selected 'Name (A - Z)' from Sort By dropdown.", true);

        // 4. Click on 'Add to Cart' button for the product
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//div[@class='product-thumb']//button[contains(@onclick, 'cart.add')])[1]")
        ));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        addToCartBtn.click();
        Reporter.log("Clicked 'Add to Cart' button for the first Mac item.", true);

        // Verify Success Alert
        WebElement successAlert = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success"))
        );
        Assert.assertTrue(successAlert.getText().contains("Success: You have added"), 
            "Expected success notification not displayed.");
        Reporter.log("Assertion PASSED: Item successfully added to cart notification verified.", true);
    }

    /**
     * Lab 4 Flow:
     * Verify Title -> Desktops -> Mac -> Verify Heading -> Sort By Name(A-Z) -> 
     * Add to Cart -> Search Monitors with Description Checkbox -> Verify Results
     */
    @Test(priority = 2, description = "Lab 4: Verify search flow with description filter after Mac navigation")
    public void testDesktopMacAndSearchMonitors() {
        Reporter.log("--- Starting Lab 4 Test Flow ---", true);

        // 1. Open the URL and Verify Title
        driver.get(baseUrl);
        String pageTitle = driver.getTitle();
        Reporter.log("Current Page Title: " + pageTitle, true);
        Assert.assertEquals(pageTitle, "Your Store", "Home page title assertion failed.");
        Reporter.log("Assertion PASSED: Home page title verified.", true);

        // 2. Go to 'Desktops' tab -> Click 'Mac'
        WebElement desktopsMenu = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Desktops']"))
        );
        actions.moveToElement(desktopsMenu).perform();

        WebElement macOption = wait.until(
            ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Mac')]"))
        );
        macOption.click();

        // 3. Verify 'Mac' heading
        WebElement macHeading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='content']//h2[text()='Mac']"))
        );
        Assert.assertTrue(macHeading.isDisplayed(), "Mac heading is not visible.");
        Assert.assertEquals(macHeading.getText(), "Mac", "Mac heading text mismatch.");
        Reporter.log("Assertion PASSED: 'Mac' heading validated.", true);

        // 4. Select 'Name (A - Z)' from 'Sort By' dropdown
        WebElement sortDropdown = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-sort")));
        new Select(sortDropdown).selectByVisibleText("Name (A - Z)");
        Reporter.log("Sorted products by 'Name (A - Z)'.", true);

        // 5. Click on 'Add to Cart' button
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//div[@class='product-thumb']//button[contains(@onclick, 'cart.add')])[1]")
        ));
        js.executeScript("arguments[0].scrollIntoView(true);", addToCartBtn);
        addToCartBtn.click();
        Reporter.log("Clicked 'Add to Cart'.", true);

        // 6. Enter 'Monitors' in 'Search' text box (value updated from Mobile as per Lab 4)
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("search")));
        searchInput.clear();
        searchInput.sendKeys("Monitors");
        Reporter.log("Entered 'Monitors' in search field.", true);

        // 7. Click Search button
        WebElement searchBtn = driver.findElement(By.cssSelector("button.btn-default"));
        searchBtn.click();
        Reporter.log("Clicked initial search button.", true);

        // 8. Clear text from 'Search Criteria' text box
        WebElement searchCriteriaInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-search")));
        searchCriteriaInput.clear();
        searchCriteriaInput.sendKeys("Monitors");
        Reporter.log("Cleared and re-populated 'Search Criteria' text box with 'Monitors'.", true);

        // 9. Click on 'Search in product descriptions' checkbox
        WebElement searchDescriptionCheckbox = wait.until(ExpectedConditions.elementToBeClickable(By.id("description")));
        if (!searchDescriptionCheckbox.isSelected()) {
            searchDescriptionCheckbox.click();
        }
        Assert.assertTrue(searchDescriptionCheckbox.isSelected(), "Description checkbox is not checked.");
        Reporter.log("Assertion PASSED: 'Search in product descriptions' checkbox checked.", true);

        // 10. Click on 'Search' button under criteria
        WebElement criteriaSearchBtn = driver.findElement(By.id("button-search"));
        criteriaSearchBtn.click();
        Reporter.log("Submitted search with description criteria.", true);

        // 11. Verify search results heading
        WebElement searchHeading = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Products meeting the search criteria') or text()='Search - Monitors'] | //div[@id='content']/h1"))
        );
        Assert.assertTrue(searchHeading.isDisplayed(), "Search results heading is not displayed.");
        Reporter.log("Assertion PASSED: Search results page confirmed.", true);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        Reporter.log("Tearing down WebDriver session...", true);
        if (driver != null) {
            driver.quit();
        }
        Reporter.log("Browser closed successfully.", true);
    }
}