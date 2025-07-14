package pages;

import logging.Logging;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import waits.builders.ElementWaitBuilder;
import waits.builders.PageWaitBuilder;
import waits.core.WaitManager;

/**
 * Base class for all Page Objects in the framework.
 * Provides common functionality and integrates the wait system.
 */
public abstract class BasePage implements Logging {

    protected WebDriver driver;
    private final WaitManager wait;

    /**
     * Constructor for BasePage
     *
     * @param driver WebDriver instance to use for waits and pages
     */
    public BasePage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalArgumentException("WebDriver cannot be null");
        }

        this.driver = driver;
        this.wait = WaitManager.getInstance(driver);

        log().info("BasePage initialized for driver: {} - Current URL: {}",
                driver.getClass().getSimpleName(),
                getCurrentUrlSafely());
    }

    protected ElementWaitBuilder waitFor(WebElement element) {
        return wait.waitFor(element);
    }

    protected PageWaitBuilder waitForPage() {
        return wait.waitForPage();
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    protected String getPageTitle() {
        String title = driver.getTitle();
        log().debug("Retrieved page title: '{}'", title);

        return title;
    }
    protected void navigateTo(String url) {
        String currentUrl = getCurrentUrlSafely();
        log().info("Navigating from '{}' to '{}' ", currentUrl, url);

        long startTime = System.currentTimeMillis();
        driver.navigate().to(url);
        long navigationTime = System.currentTimeMillis() - startTime;

        log().info("Navigation completed in {}ms - New URL: '{}'",
                navigationTime, getCurrentUrlSafely());
    }

    protected void refreshPage(){
        log().debug("Refreshing the current page...");

        driver.navigate().refresh();
    }

    protected WebDriver getDriver() {
        return driver;
    }

    public void verifyPageLoaded() {
        log().debug("Starting page load verification...");

        try{
            wait.waitForPage().toLoad().withTimeout(30);

            String title = getPageTitle();
            String url = getCurrentUrl();

            if (title == null || title.trim().isEmpty()) {
                log().warn("Page loaded but title is empty - URL: {}", url);
            }

            //Additional validation on JavaScript functionality - warning set in logs.
            if (hasJavaScriptErrors()) {
                log().warn("Page loaded but JavaScript errors were detected - URL: {}", url);
            }

            log().info("Page loaded successfully - Title: '{}', URL: '{}'", title, url);
        }
        catch (Exception e){
            log().error("Page load verification failed: {}", e.getMessage());
            throw new RuntimeException("Failed to verify page load: " + e.getMessage(), e);
        }

        log().info("Page loaded successfully");
    }

    protected void takeScreenshot(String screenshotName) {
        try {
            // Implementacion futura de ScreenshotManager
            log().debug("Taking screenshot: {}", screenshotName);
            // TODO: Implementar cuando tengamos ScreenshotManager

        } catch (Exception e) {
            log().warn("Failed to take screenshot '{}': {}", screenshotName, e.getMessage());
        }
    }

    /**
     * Verify that the page has no JavaScript errors
     * @return true if there isn't any error, false if there is.
     */
    protected boolean hasJavaScriptErrors() {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Verify errors in window.onerror
            Object errors = js.executeScript(
                    "return window.jsErrors || []"
            );

            // Also verify if it is possible the navigator console
            String consoleErrors = (String) js.executeScript(
                    "return window.console && window.console.error ? " +
                            "'Console errors detected' : null"
            );

            if (errors != null || consoleErrors != null) {
                log().warn("JavaScript errors detected on page: {}", getCurrentUrl());
                return true;
            }

            return false;

        } catch (Exception e) {
            log().debug("Could not check for JavaScript errors: {}", e.getMessage());
            return false; // It is assumed that there are no errors if it cannot verify.
        }
    }

    private String getCurrentUrlSafely() {
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            log().warn("Could not retrieve current URL: {}", e.getMessage());
            return "URL not available";
        }
    }

    protected void scrollToElement(WebElement element){
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void removeTargetBlank (WebElement element){
        log().debug("Removing target='_blank' attribute from element");
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].removeAttribute('target')", element);
    }
}