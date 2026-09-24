package com.opencart.test;

import java.time.Duration;
import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OpenCartShoppingWorkflowTest {

    public static void main(String[] args) {

        String baseUrl = "https://naveenautomationlabs.com/opencart/";
        
        // Generate a fresh user for this run to guarantee valid authentication
        String testEmail = "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@testlab.com";
        String testPassword = "Password123#";

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        Actions actions = new Actions(driver);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            driver.manage().window().maximize();

            // =========================================================
            // SETUP: Create account first to ensure valid credentials
            // =========================================================
            System.out.println("--- Registering dedicated user for workflow ---");
            driver.get(baseUrl + "index.php?route=account/register");

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-firstname"))).sendKeys("Aarav");
            driver.findElement(By.id("input-lastname")).sendKeys("Mishra");
            driver.findElement(By.id("input-email")).sendKeys(testEmail);
            driver.findElement(By.id("input-telephone")).sendKeys("9876543210");
            driver.findElement(By.id("input-password")).sendKeys(testPassword);
            driver.findElement(By.id("input-confirm")).sendKeys(testPassword);
            
            WebElement agreeBox = driver.findElement(By.name("agree"));
            if (!agreeBox.isSelected()) {
                agreeBox.click();
            }
            driver.findElement(By.cssSelector("input[value='Continue']")).click();
            
            // Wait until account is created and navigate to home
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[text()='Your Account Has Been Created!']")));
            System.out.println("User created: " + testEmail);

            // =========================================================
            // LAB 2 STEPS BEGIN
            // =========================================================
            
            // Step 2 & 3: Components -> Monitors
            System.out.println("--- Navigating to Components -> Monitors ---");
            WebElement componentsTab = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[text()='Components']"))
            );
            actions.moveToElement(componentsTab).perform();

            WebElement monitorsOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(), 'Monitors')]"))
            );
            monitorsOption.click();

            // Step 4: Select 25 from 'Show' dropdown
            System.out.println("--- Setting Show Limit to 25 ---");
            WebElement showDropdown = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("input-limit"))
            );
            Select selectShow = new Select(showDropdown);
            selectShow.selectByVisibleText("25");

            // Step 5: Click on 'Add to cart' for the first item
            System.out.println("--- Click Add to Cart for first item ---");
            WebElement firstAddToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//div[@class='product-thumb']//button[contains(@onclick, 'cart.add')])[1]")
            ));
            js.executeScript("arguments[0].scrollIntoView(true);", firstAddToCartBtn);
            firstAddToCartBtn.click();

            // Step 6 & 7: Specification tab & verify details
            System.out.println("--- Checking Specification Tab ---");
            WebElement specificationTab = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[@href='#tab-specification' or text()='Specification']"))
            );
            js.executeScript("arguments[0].scrollIntoView(true);", specificationTab);
            specificationTab.click();

            WebElement specTable = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("tab-specification"))
            );
            if (specTable.isDisplayed()) {
                System.out.println("PASS: Specification verified.");
            }

            // Step 8 & 9: Add to Wish list & verify success message
            System.out.println("--- Adding to Wish List ---");
            WebElement addToWishlistBtn = driver.findElement(
                By.xpath("//button[@data-original-title='Add to Wish List' or contains(@onclick, 'wishlist.add')]")
            );
            addToWishlistBtn.click();

            WebElement wishlistAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success"))
            );
            System.out.println("Wishlist Message: " + wishlistAlert.getText());
            if (wishlistAlert.getText().contains("wish list!")) {
                System.out.println("PASS: Wish list notification verified.");
            }

            // Step 10, 11 & 12: Search 'Mobile' & description checkbox
            System.out.println("--- Searching 'Mobile' with Description ---");
            WebElement searchBox = driver.findElement(By.name("search"));
            searchBox.clear();
            searchBox.sendKeys("Mobile");

            WebElement searchButton = driver.findElement(By.cssSelector("button.btn-default"));
            searchButton.click();

            WebElement descCheckbox = wait.until(
                ExpectedConditions.elementToBeClickable(By.id("description"))
            );
            if (!descCheckbox.isSelected()) {
                descCheckbox.click();
            }
            driver.findElement(By.id("button-search")).click();

            // Step 13, 14, 15 & 16: HTC Touch HD, Qty 3, Add to Cart & Verify
            System.out.println("--- Handling HTC Touch HD ---");
            WebElement htcLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("HTC Touch HD"))
            );
            htcLink.click();

            WebElement qtyInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-quantity")));
            qtyInput.clear();
            qtyInput.sendKeys("3");

            driver.findElement(By.id("button-cart")).click();

            WebElement cartAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success"))
            );
            System.out.println("Cart Alert: " + cartAlert.getText());
            if (cartAlert.getText().contains("Success: You have added HTC Touch HD to your shopping cart!")) {
                System.out.println("PASS: Cart success message verified.");
            }

            // Step 17 & 18: View cart button adjacent to search & Verify Mobile name
            System.out.println("--- Verifying Cart Dropdown ---");
            WebElement cartDropdownBtn = driver.findElement(By.id("cart-total"));
            cartDropdownBtn.click();

            WebElement viewCartLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//strong[contains(text(), 'View Cart')]"))
            );
            viewCartLink.click();

            WebElement cartItemTitle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='content']//table//a[text()='HTC Touch HD']"))
            );
            System.out.println("Item in Cart Verified: " + cartItemTitle.getText());

            // Step 19: Click Checkout
            System.out.println("--- Clicking Checkout ---");
            WebElement checkoutBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Checkout"))
            );
            checkoutBtn.click();

            // Step 20, 21, 22 & 23: Logout & verify heading
            System.out.println("--- Logging out ---");
            WebElement accountDropdown = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//a[@title='My Account'] | //span[text()='My Account']"))
            );
            accountDropdown.click();

            WebElement logoutOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Logout"))
            );
            logoutOption.click();

            WebElement logoutHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[text()='Account Logout']"))
            );
            System.out.println("PASS: Verified heading: " + logoutHeading.getText());

            WebElement finalContinueBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Continue"))
            );
            finalContinueBtn.click();
            System.out.println("Workflow completed successfully!");

        } catch (Exception e) {
            System.err.println("Test Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}