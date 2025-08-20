package pages.mobile;

import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class AppSectionPage extends MobileBasePage {

    public AppSectionPage(WebDriver driver){
        super(driver);

        PageFactory.initElements(driver, this);

        log().info("App section page initialized");
    }


}
