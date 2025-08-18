package testrunner;

import driver.DriverFactory;
import driver.DriverManager;
import listeners.TestMethodListener;
import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

@Listeners(TestMethodListener.class)
public abstract class MobileBaseTest implements Logging {

    protected static DriverManager driverManager;

    @BeforeMethod
    protected void setup(@Optional String driverName) {
        driverManager = DriverFactory.MOBILE.getDriverManager();
    }

    @AfterMethod
    protected void cleanUp() {
        driverManager.quitDriver();
    }

    protected WebDriver getDriver() {
        return driverManager.getDriver();
    }

}
