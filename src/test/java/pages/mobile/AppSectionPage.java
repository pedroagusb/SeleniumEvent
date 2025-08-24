package pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class AppSectionPage extends MobileBasePage {

    @FindBy(xpath = "//*[contains(@text,'App')]")
    private WebElement appsTitle;

    @FindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private WebElement backButton;

    // Alternative back button
    @FindBy(id = "android:id/home")
    private WebElement homeButton;

    public AppSectionPage(WebDriver driver){
        super(driver);

        PageFactory.initElements(driver, this);

        log().info("App section page initialized");
    }

    public AppSectionPage verifyAppSectionLoaded(){
        log().info("Verifying complete load of Apps Page");

        waitFor(appsTitle).toBeVisible().withTimeout(5);

        return this;
    }

    public SettingsPage navigateBackToSettings(){
        log().info("Navigating to Setting Page");

        clickNavigationElement(backButton);

        return new SettingsPage(getDriver());
    }


}
