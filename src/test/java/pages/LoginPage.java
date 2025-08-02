package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;

import java.time.Duration;
import java.util.List;

public class LoginPage {
    WebDriver driver;

    By userIdField = By.id("formEmail");
    By passwordField = By.id("formPassword");
    By loginButton = By.cssSelector("button[type='submit']");
    By errorMessageLocator = By.cssSelector("p.normal-text[style*='color: red']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterEmail(String email) {
        driver.findElement(userIdField).clear();
        driver.findElement(userIdField).sendKeys(email);
    }

    public void enterPassword(String password) {
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLogin() {
        driver.findElement(loginButton).click();
        try {
            Thread.sleep(1000); // wait for error to appear
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handleNotificationBlockPageIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement reloadBtn = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[contains(text(),'Reload')]")
            ));
            System.out.println("📢 Notification block screen detected. Clicking Reload...");
            reloadBtn.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(userIdField));
            System.out.println("✅ Login form loaded after reload.");
        } catch (Exception e) {
            // Now scan for blocking error text
            List<WebElement> pTags = driver.findElements(By.tagName("p"));
            for (WebElement p : pTags) {
                String text = p.getText().toLowerCase();
                if (text.contains("please allow") && text.contains("notifications")) {
                    System.out.println("⚠️ Notification block still active. Skipping test.");
                    throw new SkipException("Skipped: Notification block screen is still present.");
                }
            }
            System.out.println("✅ No blocking notification message found. Proceeding.");
        }
    }

    public void waitForLoginPage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(userIdField));
    }

    public String getErrorMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));
            return errorMsg.getText();
        } catch (Exception e) {
            // Check for the notification block message
            List<WebElement> pTags = driver.findElements(By.tagName("p"));
            for (WebElement p : pTags) {
                String text = p.getText().toLowerCase();
                if (text.contains("please allow") && text.contains("notifications")) {
                    System.out.println("⚠️ Notification block still active in error message check. Skipping test.");
                    throw new SkipException("Skipped: Notification screen still blocking login.");
                }
            }
            System.out.println("❌ Error message not found. Dumping <p> tags:");
            for (WebElement p : pTags) {
                System.out.println("p: " + p.getText());
            }
            throw e;
        }
    }

    public boolean isLoginButtonEnabled() {
        WebElement button = driver.findElement(loginButton);
        String disabledAttr = button.getAttribute("disabled");
        return (disabledAttr == null || disabledAttr.equals("false"));
    }

    public boolean isPasswordMasked() {
        return driver.findElement(passwordField).getAttribute("type").equals("password");
    }
}
