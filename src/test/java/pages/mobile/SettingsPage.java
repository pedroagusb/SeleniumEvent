package pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SettingsPage extends MobileBasePage{

    @FindBy(id = "android:id/title")
    private WebElement settingsTitle;

    @FindBy(xpath = "//android.widget.TextView[@text='Network & internet']")
    private WebElement networkSection;

    @FindBy(xpath = "//android.widget.TextView[@text='Apps']")
    private WebElement appsSection;

    public SettingsPage(WebDriver driver){
        super(driver);

        PageFactory.initElements(driver, this);
        log().info("SettingPage initialized");
    }

    public SettingsPage verifySettingsPageLoaded(){
        log().info("Verifying complete load of Settings Page");

        log().debug("Starting page load verification...");

        try {
            Thread.sleep(100); // Small pause for page stabilization
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        waitFor(settingsTitle).toBeVisible().withTimeout(10);
        log().info("Settings elements verified successfully");
        return this;
    }

    public AppSectionPage navigateToAppsSection(){
        log().info("Navigating to App section");

        waitFor(appsSection).toBeClickable().withTimeout(5);
        appsSection.click();

        return new AppSectionPage(getDriver());
    }

    public NetworkSectionPage navigateToNetworkSection(){
        log().info("Navigating to Network section");

        waitFor(networkSection).toBeClickable().withTimeout(5);
        networkSection.click();

        return new NetworkSectionPage(getDriver());
    }

    public String getSettingsTitle(){
        log().info("Getting Settings page title");

        waitFor(settingsTitle).toBeVisible().withTimeout(5);
        return settingsTitle.getText();
    }

    public boolean isNetworkSectionVisible(){
        log().info("Verifying if Network section is available");

        try {
            return waitFor(networkSection).toBeVisible().withTimeout(5) != null;
        } catch (Exception e) {
            log().warn("Network section is not visible");
            return false;
        }
    }

}
