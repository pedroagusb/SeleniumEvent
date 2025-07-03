package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignUpPage extends BasePage{
    @FindBy(css = "")
    private WebElement signUpElement;

    public SignUpPage(WebDriver driver){
        super(driver);
        PageFactory.initElements(driver, this);

        log().info("SignUpPage initialized");
    }

    public SignUpPage verifySignUpPageDisplayed(){
        verifyPageLoaded();
        waitFor(signUpElement).toBeVisible().withTimeout(15);

        log().info("SignUp page verified successfully");
        return this;
    }
}
