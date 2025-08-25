package pages.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage{
    @FindBy(css = "input[type='email'], input[name='email']" )
    private WebElement emailField;

    public LoginPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);
        log().info("LoginPage initialized");
    }

    public LoginPage verifyLoginPageDisplayed(){
        verifyPageLoaded();
        waitFor(emailField).toBeVisible().withTimeout(15);
        log().info("Login page verified successfully");
        return this;
    }

    public HomePage loginWithCredentials(String email, String password) {
        // TODO: Implement when ready to handle authentication
        throw new UnsupportedOperationException("Login functionality pending implementation");
    }
}
