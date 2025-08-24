package pages.mobile;

import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import waits.builders.ElementWaitBuilder;
import waits.core.WaitManager;

public abstract class MobileBasePage implements Logging {

    protected WebDriver driver;
    protected WaitManager waitManager;

    public MobileBasePage(WebDriver driver){
        if(driver == null){
            throw new IllegalArgumentException("WebDriver cannot be null");
        }

        this.driver = driver;
        this.waitManager = WaitManager.getInstance(driver);

        log().info("BasePage initialized for driver: {} ",
                driver.getClass().getSimpleName());
    }

    protected void clickNavigationElement(WebElement mobileElement){
        waitFor(mobileElement).toBeVisible().
                toBeClickable().withTimeout(5);

        mobileElement.click();
    }

    protected WebDriver getDriver(){ return driver; }

    protected ElementWaitBuilder waitFor(WebElement element) {
        return waitManager.waitFor(element);
    }
}
