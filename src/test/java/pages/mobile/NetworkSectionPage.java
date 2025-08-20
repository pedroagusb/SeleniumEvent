package pages.mobile;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class NetworkSectionPage extends MobileBasePage{

    public NetworkSectionPage(WebDriver driver){
        super(driver);

        PageFactory.initElements(driver, this);

        log().info("Network section page initialized");
    }
}
