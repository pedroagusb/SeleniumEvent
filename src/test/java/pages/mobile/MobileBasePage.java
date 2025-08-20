package pages.mobile;

import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import waits.builders.ElementWaitBuilder;
import waits.builders.PageWaitBuilder;
import waits.core.WaitManager;

import java.util.function.Supplier;

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

    protected WebDriver getDriver(){ return driver; }

    protected ElementWaitBuilder waitFor(WebElement element) {
        return waitManager.waitFor(element);
    }
}
