package waits.core;

import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.FluentWait;
import utils.Constants;
import waits.builders.ElementWaitBuilder;
import waits.builders.PageWaitBuilder;
import waits.metrics.WaitMetrics;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for all wait operations.
 * Provides factory methods for creating wait builders and manages WebDriver instances.
 */

public class WaitManager implements Logging {

    // Thread-safe cache of WaitManager instances per WebDriver
    private static final ConcurrentHashMap<WebDriver, WaitManager> instances = new ConcurrentHashMap<>();

    private final WebDriver driver;
    private final WaitMetrics metrics;

    /**
     * Private constructor - use getInstance() instead
     */
    private WaitManager(WebDriver driver) {
        this.driver = driver;
        this.metrics = new WaitMetrics();
        log().debug("WaitManager initialized for driver: {}", driver.getClass().getSimpleName());
    }

    /**
     * Get or create WaitManager instance for the given WebDriver.
     * Thread-safe singleton per driver instance.
     *
     * @param driver WebDriver instance
     * @return WaitManager instance for this driver
     */
    public static WaitManager getInstance(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }

        return instances.computeIfAbsent(driver, WaitManager::new);
    }

    /**
     * Remove WaitManager instance when driver is quit.
     * Should be called in cleanup methods.
     *
     * @param driver WebDriver instance being quit
     */
    public static void removeInstance(WebDriver driver) {
        WaitManager removed = instances.remove(driver);
        if (removed != null) {
            removed.log().debug("WaitManager instance removed for driver");
        }
    }

    /**
     * Create an ElementWaitBuilder for the given WebElement
     *
     * @param element WebElement to wait for
     * @return ElementWaitBuilder configured with default settings
     */
    public ElementWaitBuilder waitFor(WebElement element) {
        if (element == null) {
            throw new IllegalArgumentException("WebElement cannot be null");
        }

        log().debug("Creating ElementWaitBuilder for element: {}", element.getTagName());

        return new ElementWaitBuilder(
                driver,
                element,
                createWebDriverWait(Constants.getElementTimeout()),
                metrics
        );
    }

    /**
     * Create a PageWaitBuilder for page-level waits
     *
     * @return PageWaitBuilder configured with default settings
     */
    public PageWaitBuilder waitForPage() {
        log().debug("Creating PageWaitBuilder");

        return new PageWaitBuilder(
                driver,
                createWebDriverWait(Constants.getPageTimeout()),
                metrics
        );
    }

    /**
     * Create a WebDriverWait with standard configuration
     *
     * @param timeout timeout duration
     * @return configured WebDriverWait
     */
    public WebDriverWait createWebDriverWait(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.pollingEvery(Constants.getPollingInterval());

        log().debug("Created WebDriverWait - timeout: {}s, polling: {}ms",
                timeout.getSeconds(), Constants.getPollingInterval().toMillis());

        return wait;
    }

    /**
     * Create a FluentWait with advanced configuration options
     *
     * @param timeout timeout duration
     * @param pollingInterval polling interval
     * @return configured FluentWait
     */
    public FluentWait<WebDriver> createFluentWait(Duration timeout, Duration pollingInterval) {
        FluentWait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval);

        log().debug("Created FluentWait - timeout: {}s, polling: {}ms",
                timeout.getSeconds(), pollingInterval.toMillis());

        return wait;
    }

    /**
     * Get wait metrics for performance monitoring
     *
     * @return WaitMetrics instance
     */
    public WaitMetrics getMetrics() {
        return metrics;
    }

    /**
     * Get the WebDriver instance associated with this WaitManager
     *
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Clear all metrics data
     */
    public void clearMetrics() {
        metrics.clear();
        log().debug("Wait metrics cleared");
    }
}
