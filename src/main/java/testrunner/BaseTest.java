package testrunner;

import listeners.TestMethodListener;
import logging.Logging;
import org.openqa.selenium.WebDriver;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import driver.DriverFactory;
import driver.DriverManager;
import utils.Constants;

@Listeners(TestMethodListener.class)
public abstract class BaseTest implements Logging{

    protected static DriverManager driverManager;

    @BeforeMethod
    @Parameters({"driverName"})
    protected void setup(@Optional String driverName) {
        String browserToUse = determineBrowser(driverName);

        initializeDriverManager(browserToUse);
        driverManager.getDriver().navigate().to(Constants.getContextUrl());
    }

    /** Separating initialization of DriverManager because it's a static class and can have strange behaviors while running tests in parallel.
     *  The synchronized Keyword is to prevent 2 Threads from calling a static class at the same time.
     *  Read more about synchronization in <a>https://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html</a>
     */
    private synchronized void initializeDriverManager(String driverName) {
        if (null == driverManager) {
            driverManager = DriverFactory.valueOf(driverName).getDriverManager();
        } else {
            driverManager.getDriver();
        }
    }

    private String determineBrowser(String driverNameParam){
        String browser = System.getProperty("browser");
        if(browser != null && !browser.trim().isEmpty()){
            log().info("Browser selected from system property: {}", browser);
            return browser;
        }

        if(driverNameParam != null && driverNameParam.trim().isEmpty()) {
            log().info("Browser selected from driverName: {}", driverNameParam);
            return driverNameParam;
        }

        log().info("Default browser selected");
        return Constants.getBrowser();
    }

    @AfterMethod
    protected void cleanUp() {
        driverManager.quitDriver();
    }

    protected WebDriver getDriver() {
        return driverManager.getDriver();
    }
}