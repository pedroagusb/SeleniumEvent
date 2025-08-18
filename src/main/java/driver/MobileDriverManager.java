package driver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import logging.Logging;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class MobileDriverManager extends DriverManager implements Logging {

    @Override
    protected WebDriver createDriver(){
        log().info("Initializing Mobile Driver");

        try {
            URL appiumServerUrl = new URL("http://127.0.0.1:4723");

            return new AndroidDriver(appiumServerUrl, getMobileCapabilities());
        } catch (MalformedURLException e) {
            log().error("Error creating Appium driver URL: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize mobile driver", e);
        }


    }

    private UiAutomator2Options getMobileCapabilities(){
        UiAutomator2Options options = new UiAutomator2Options();

        options.setPlatformName("Android");
        options.setDeviceName("Pixel 6 Pro");
        options.setAppPackage("com.android.settings");
        options.setAppActivity("com.android.settings.Settings");
        options.setAutomationName("UiAutomator2");

        return options;
    }
}
