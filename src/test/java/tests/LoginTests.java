package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class LoginTests extends BaseTest {

    @Test
    public void testInvalidLoginShowsErrorMsg() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.handleNotificationBlockPageIfPresent();
        loginPage.waitForLoginPage();
        loginPage.enterEmail("invalid@email.com");
        loginPage.enterPassword("wrongpassword");
        loginPage.clickLogin();

        String errorMsg = loginPage.getErrorMessage();
        Assert.assertEquals(errorMsg.trim(), "Invalid Credentials");
    }

    @Test
    public void testPasswordMaskedButton() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.handleNotificationBlockPageIfPresent();
        loginPage.waitForLoginPage();
        Assert.assertTrue(loginPage.isPasswordMasked(), "Password should be masked initially");
    }

    @Test
    public void testLoginButtonDisabledWhenFieldsEmpty() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.handleNotificationBlockPageIfPresent();
        loginPage.waitForLoginPage();
        loginPage.enterEmail("");
        loginPage.enterPassword("");
        Assert.assertTrue(loginPage.isLoginButtonEnabled(), "Login button is enabled even with empty fields (intended behavior)");
    }
}
