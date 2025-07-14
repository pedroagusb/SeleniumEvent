package driver;

import logging.Logging;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public abstract class DriverManager implements Logging {
    protected ThreadLocal<WebDriver> drivers = new ThreadLocal<>();

    protected abstract WebDriver createDriver();

    public void quitDriver() {
        if (null != drivers.get()) {
            try {
                drivers.get().quit(); // First quit WebDriver session gracefully
                drivers.remove(); // Remove WebDriver reference from the ThreadLocal variable.
            } catch (Exception e) {
                log().error("Unable to gracefully quit WebDriver. {}", e.getMessage());
            }

        }
    }

    public WebDriver getDriver() {
        if (null == drivers.get()) {
            drivers.set(this.createDriver());
        }
        drivers.get().manage().timeouts().implicitlyWait(Duration.ofSeconds(1));

        return drivers.get();
    }

}
