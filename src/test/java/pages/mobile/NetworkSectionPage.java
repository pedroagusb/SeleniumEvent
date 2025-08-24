package pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class NetworkSectionPage extends MobileBasePage{

    @FindBy(xpath = "//android.widget.FrameLayout[@content-desc='Network & internet']")
    private WebElement networkTitle;

    @FindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private WebElement backButton;

    @FindBy(xpath = "//androidx.recyclerview.widget.RecyclerView[@resource-id='com.android.settings:id/recycler_view']/android.widget.LinearLayout[1]/android.widget.RelativeLayout")
    private WebElement wifiSection;

    @FindBy(xpath = "//android.widget.TextView[contains(@text,'Mobile')]")
    private WebElement mobileDataSection;

    public NetworkSectionPage(WebDriver driver){
        super(driver);

        PageFactory.initElements(driver, this);

        log().info("Network section page initialized");
    }

    public NetworkSectionPage verifyNetworkSectionLoaded(){
        log().info("Verifying complete load of Network Page");

        waitFor(networkTitle).toBeVisible().withTimeout(5);
        waitFor(wifiSection).toBeVisible().withTimeout(5);

        return this;
    }

    public SettingsPage navigateBackToSettings(){
        log().info("Navigating to Setting Page");

        clickNavigationElement(backButton);

        return new SettingsPage(getDriver());
    }

    public boolean isWifiVisible(){
        log().info("Verifying if Wifi is available");

        try {
            return waitFor(wifiSection).toBeVisible().withTimeout(5) != null;
        } catch (Exception e) {
            log().warn("Wifi is not visible");
            return false;
        }
    }

    public boolean isMobileDataVisible(){
        log().info("Verifying if Mobile Data is available");

        try{
            return waitFor(mobileDataSection).toBeVisible().withTimeout(5) != null;
        } catch (Exception e) {
            log().warn("Mobile Data is nos visible");
            return false;
        }
    }
}
