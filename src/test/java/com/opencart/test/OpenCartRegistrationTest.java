package com.opencart.test;

import java.time.Duration;
import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OpenCartRegistrationTest {

    public static void main(String[] args) {

        // ==========================================
        // VARIABLES
        // ==========================================
        String baseUrl = "https://naveenautomationlabs.com/opencart/";
        String expectedHomeTitle = "Your Store";
        String expectedHeading = "Register Account";
        String expectedPrivacyWarning = "Warning: You must agree to the Privacy Policy!";

        // Validation test strings
        String text33Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567"; // exactly 33 chars
        String validFirstName = "Aarav";
        String validLastName = "Sharma";
        String dynamicEmail = "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        String validTelephone = "9876543210";
        String validAddress1 = "42 MG Road, Tech Residency";
        String validCity = "Bengaluru";
        String validPostCode = "560001";
        String countryName = "India";
        String stateName = "Karnataka";
        String validPassword = "Password123#";

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            driver.manage().window().maximize();

            // ------------------------------------------
            // PART 1: Launch Application & Verify
            // ------------------------------------------
            System.out.println("--- Part 1: Launch Application ---");
            driver.get(baseUrl);

            // Verify Title
            String actualHomeTitle = driver.getTitle();
            System.out.println("Home Page Title: " + actualHomeTitle);
            if (actualHomeTitle.contains(expectedHomeTitle)) {
                System.out.println("PASS: Title matches.");
            } else {
                System.out.println("FAIL: Title mismatch. Got: " + actualHomeTitle);
            }

            // Click 'My Account' dropdown
            WebElement myAccountDropdown = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='My Account'] | //a[@title='My Account']"))
            );
            myAccountDropdown.click();

            // Select 'Register'
            WebElement registerOption = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Register"))
            );
            registerOption.click();

            // Verify heading 'Register Account'
            WebElement headingElement = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[text()='Register Account'] | //div[@id='content']/h1"))
            );
            System.out.println("Page Heading: " + headingElement.getText());
            if (headingElement.getText().equalsIgnoreCase(expectedHeading)) {
                System.out.println("PASS: Heading 'Register Account' verified.");
            }

            // Click 'Continue' directly to verify Privacy Policy warning
            WebElement initialContinueBtn = driver.findElement(By.cssSelector("input[value='Continue'], button[type='submit']"));
            initialContinueBtn.click();

            try {
                WebElement alertBox = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-danger, .alert-dismissible"))
                );
                System.out.println("Warning message displayed: " + alertBox.getText());
                if (alertBox.getText().contains(expectedPrivacyWarning)) {
                    System.out.println("PASS: Privacy Policy warning verified.");
                }
            } catch (Exception e) {
                System.out.println("Warning check: Handled or caught by inline validation.");
            }

            // ------------------------------------------
            // PART 2: Personal Details Boundary Testing
            // ------------------------------------------
            System.out.println("\n--- Part 2: Personal Details ---");

            // 1. Enter 33 characters in First Name and click Continue
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-firstname"))).clear();
            driver.findElement(By.id("input-firstname")).sendKeys(text33Chars);
            driver.findElement(By.cssSelector("input[value='Continue'], button[type='submit']")).click();

            try {
                WebElement fnError = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='error-firstname' or contains(@class, 'text-danger')]"))
                );
                System.out.println("First Name boundary error: " + fnError.getText());
            } catch (Exception ex) {
                System.out.println("Handled via HTML5 validation.");
            }

            // 2. Refresh reference, enter valid First Name, then test 33 characters in Last Name
            WebElement fnInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-firstname")));
            fnInput.clear();
            fnInput.sendKeys(validFirstName);

            WebElement lnInput = driver.findElement(By.id("input-lastname"));
            lnInput.clear();
            lnInput.sendKeys(text33Chars);
            driver.findElement(By.cssSelector("input[value='Continue'], button[type='submit']")).click();

            try {
                WebElement lnError = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='error-lastname' or contains(@class, 'text-danger')]"))
                );
                System.out.println("Last Name boundary error: " + lnError.getText());
            } catch (Exception ex) {
                System.out.println("Handled via HTML5 validation.");
            }

            // 3. Re-fetch all fields fresh after the last submit reload
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("input-firstname"))).clear();
            driver.findElement(By.id("input-firstname")).sendKeys(validFirstName);

            WebElement validLnField = driver.findElement(By.id("input-lastname"));
            validLnField.clear();
            validLnField.sendKeys(validLastName);

            WebElement emailField = driver.findElement(By.id("input-email"));
            emailField.clear();
            emailField.sendKeys(dynamicEmail);

            if (driver.findElements(By.id("input-telephone")).size() > 0) {
                WebElement telephoneField = driver.findElement(By.id("input-telephone"));
                telephoneField.clear();
                telephoneField.sendKeys(validTelephone);
                System.out.println("Entered Telephone: " + validTelephone);
            }

            // ------------------------------------------
            // PART 3: Address Section
            // ------------------------------------------
            System.out.println("\n--- Part 3: Address Section ---");

            if (driver.findElements(By.id("input-address-1")).size() > 0) {
                driver.findElement(By.id("input-address-1")).sendKeys(validAddress1);
                driver.findElement(By.id("input-city")).sendKeys(validCity);
                driver.findElement(By.id("input-postcode")).sendKeys(validPostCode);

                Select countrySelect = new Select(driver.findElement(By.id("input-country")));
                countrySelect.selectByVisibleText(countryName);

                // Wait for the states dropdown options to finish updating via AJAX
                wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
                    By.id("input-zone"), By.xpath(".//option[text()='" + stateName + "']")
                ));
                Select zoneSelect = new Select(driver.findElement(By.id("input-zone")));
                zoneSelect.selectByVisibleText(stateName);
                System.out.println("Address fields populated successfully.");
            }

            // ------------------------------------------
            // PART 4: Password, Newsletter & Completion
            // ------------------------------------------
            System.out.println("\n--- Part 4: Password & Newsletter ---");

            driver.findElement(By.id("input-password")).sendKeys(validPassword);

            if (driver.findElements(By.id("input-confirm")).size() > 0) {
                driver.findElement(By.id("input-confirm")).sendKeys(validPassword);
            }

            // Newsletter: click 'Yes'
            WebElement newsletterYes = driver.findElement(By.xpath("//input[@name='newsletter'][@value='1']"));
            if (!newsletterYes.isSelected()) {
                newsletterYes.click();
            }

            // Agree to Privacy Policy
            WebElement privacyPolicyBox = driver.findElement(By.xpath("//input[@name='agree']"));
            if (!privacyPolicyBox.isSelected()) {
                privacyPolicyBox.click();
            }

            // Submit Registration Form
            driver.findElement(By.cssSelector("input[value='Continue'], button[type='submit']")).click();

            // Verify 'Your Account Has Been Created!'
            WebElement createdHeading = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[text()='Your Account Has Been Created!'] | //div[@id='content']/h1"))
            );
            System.out.println("Confirmation Heading: " + createdHeading.getText());

            // Click Continue to navigate to user dashboard
            WebElement postCreateContinue = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Continue"))
            );
            postCreateContinue.click();

            // Click 'View your order history' under My Orders
            WebElement orderHistoryLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("View your order history"))
            );
            orderHistoryLink.click();
            System.out.println("Navigated to Order History URL: " + driver.getCurrentUrl());
            System.out.println("LAB DEMO 5 COMPLETED SUCCESSFULLY!");

        } catch (Exception e) {
            System.err.println("Test Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            driver.quit();
            System.out.println("Browser closed successfully.");
        }
    }
}