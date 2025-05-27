package driver;

import static io.github.bonigarcia.wdm.config.DriverManagerType.FIREFOX;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.Constants;

public class FirefoxDriverManager extends DriverManager {
    @Override
    protected WebDriver createDriver() {
        log().info("Initializing Firefox Driver");
        WebDriverManager.getInstance(FIREFOX).setup();


        return new FirefoxDriver(getFirefoxOptions());
    }

    private FirefoxOptions getFirefoxOptions() {
        // A few valid Options for Firefox, showcase purpose.
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-features=EnableEphemeralFlashPermission");
        options.addArguments("--disable-infobars");

        // Headless configuration
        if (Constants.isHeadless()) {
            log().info("Running Firefox in headless mode");
            options.addArguments("--headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        } else {
            log().info("Running Firefox in normal mode");
        }

        return options;
    }
}
