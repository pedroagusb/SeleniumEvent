package driver;

import static io.github.bonigarcia.wdm.config.DriverManagerType.CHROME;

import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.Constants;

public class ChromeDriverManager extends DriverManager implements Logging {

    @Override
    protected WebDriver createDriver() {
        log().info("Initializing Chrome Driver");
        WebDriverManager.getInstance(CHROME).setup();

        return new ChromeDriver(getChromeOptions());
    }

    private ChromeOptions getChromeOptions() {
        // A few valid Options for Chrome, showcase purpose.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-features=EnableEphemeralFlashPermission");
        options.addArguments("--disable-infobars");

        // Headless configuration
        if (Constants.isHeadless()) {
            log().info("Running Chrome in headless mode");
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-plugins");
            options.addArguments("--remote-debugging-port=9222"); // For debugging if necessary
        } else {
            log().info("Running Chrome in normal mode");
        }

        return options;
    }

}