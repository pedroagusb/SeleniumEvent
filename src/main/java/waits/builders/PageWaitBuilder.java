package waits.builders;

import logging.Logging;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import waits.core.WaitBuilder;
import waits.metrics.WaitMetrics;
import utils.Constants;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Fluent builder for creating page-level waits.
 * Provides a chainable API for waiting on page states and navigation conditions.
 * <p>
 * Unlike ElementWaitBuilder which focuses on individual WebElements, PageWaitBuilder
 * handles waits that affect the entire page state, navigation, and JavaScript execution.
 * <p>
 * Usage examples:
 * waitForPage().toLoad().withTimeout(30);
 * waitForPage().urlToContain("/dashboard");
 * waitForPage().titleToContain("Home Page");
 */
public class PageWaitBuilder implements WaitBuilder<PageWaitBuilder>, Logging {

    // Core dependencies - injected through constructor
    private final WebDriver driver;
    private final WaitMetrics metrics;

    // Configuration state - built through fluent methods
    private Duration timeout;
    private Duration pollingInterval;
    private String customMessage;
    private Set<Class<? extends Exception>> ignoredExceptions;

    // Wait execution state
    private WebDriverWait webDriverWait;

    /**
     * Constructor - typically called by WaitManager, not directly by users
     *
     * @param driver WebDriver instance to use for page-level waits
     * @param defaultWait Pre-configured WebDriverWait with default settings
     * @param metrics Metrics collector for performance monitoring
     */
    public PageWaitBuilder(WebDriver driver, WebDriverWait defaultWait, WaitMetrics metrics) {
        this.driver = driver;
        this.webDriverWait = defaultWait;
        this.metrics = metrics;

        // Initialize with default values from Constants
        this.timeout = Constants.getPageTimeout(); // Page operations typically take longer
        this.pollingInterval = Constants.getPollingInterval();
        this.ignoredExceptions = new HashSet<>();

        log().debug("PageWaitBuilder created for driver: {}", driver.getClass().getSimpleName());
    }

    // ========== PAGE LOADING CONDITIONS - Most common page-level waits ==========

    /**
     * Wait for the page to finish loading completely.
     * This includes waiting for all resources, scripts, and the document ready state.
     *
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder toLoad() {
        String conditionName = "page to load completely";

        // Create a custom condition that checks multiple aspects of page loading
        Supplier<ExpectedCondition<Boolean>> pageLoadCondition = () -> new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                // Check if document.readyState is 'complete'
                JavascriptExecutor js = (JavascriptExecutor) driver;
                String readyState = (String) js.executeScript("return document.readyState");

                // Also check if jQuery is done loading (if jQuery is present)
                Boolean jQueryComplete = (Boolean) js.executeScript(
                        "return typeof jQuery !== 'undefined' ? jQuery.active === 0 : true"
                );

                // Page is loaded when document is complete AND jQuery is not active
                boolean isLoaded = "complete".equals(readyState) && jQueryComplete;

                log().debug("Page load check - readyState: {}, jQueryActive: {}, isLoaded: {}",
                        readyState, !jQueryComplete, isLoaded);

                return isLoaded;
            }

            @Override
            public String toString() {
                return "page to be fully loaded (document.readyState = 'complete' and no active jQuery requests)";
            }
        };

        executeWait(pageLoadCondition, conditionName);
        return this;
    }

    /**
     * Wait for the page to have no active JavaScript execution.
     * Useful for SPA applications where content loads asynchronously.
     *
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder forJavaScriptToComplete() {
        String conditionName = "JavaScript execution to complete";

        Supplier<ExpectedCondition<Boolean>> jsCompleteCondition = () -> new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                JavascriptExecutor js = (JavascriptExecutor) driver;

                // Check multiple indicators that JavaScript has finished executing
                Boolean documentReady = (Boolean) js.executeScript("return document.readyState === 'complete'");
                Boolean jQueryReady = (Boolean) js.executeScript(
                        "return typeof jQuery !== 'undefined' ? jQuery.active === 0 : true"
                );
                Boolean angularReady = (Boolean) js.executeScript(
                        "return typeof angular !== 'undefined' ? angular.element(document).injector().get('$http').pendingRequests.length === 0 : true"
                );

                boolean allReady = documentReady && jQueryReady && angularReady;

                log().debug("JS completion check - document: {}, jQuery: {}, Angular: {}, allReady: {}",
                        documentReady, jQueryReady, angularReady, allReady);

                return allReady;
            }

            @Override
            public String toString() {
                return "all JavaScript frameworks to complete execution";
            }
        };

        executeWait(jsCompleteCondition, conditionName);
        return this;
    }

    // ========== URL AND NAVIGATION CONDITIONS ==========

    /**
     * Wait for URL to contain a specific substring
     *
     * @param urlSubstring Expected substring in the current URL
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder urlToContain(String urlSubstring) {
        String conditionName = "URL to contain '" + urlSubstring + "'";
        executeWait(() -> ExpectedConditions.urlContains(urlSubstring), conditionName);
        return this;
    }

    /**
     * Wait for URL to match exactly
     *
     * @param expectedUrl Expected exact URL
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder urlToBe(String expectedUrl) {
        String conditionName = "URL to be '" + expectedUrl + "'";
        executeWait(() -> ExpectedConditions.urlToBe(expectedUrl), conditionName);
        return this;
    }

    /**
     * Wait for URL to match a regex pattern
     *
     * @param urlPattern Regex pattern that the URL should match
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder urlToMatch(String urlPattern) {
        String conditionName = "URL to match pattern '" + urlPattern + "'";
        executeWait(() -> ExpectedConditions.urlMatches(urlPattern), conditionName);
        return this;
    }

    // ========== TITLE CONDITIONS ==========

    /**
     * Wait for page title to contain specific text
     *
     * @param titleSubstring Expected substring in the page title
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder titleToContain(String titleSubstring) {
        String conditionName = "page title to contain '" + titleSubstring + "'";
        executeWait(() -> ExpectedConditions.titleContains(titleSubstring), conditionName);
        return this;
    }

    /**
     * Wait for page title to match exactly
     *
     * @param expectedTitle Expected exact page title
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder titleToBe(String expectedTitle) {
        String conditionName = "page title to be '" + expectedTitle + "'";
        executeWait(() -> ExpectedConditions.titleIs(expectedTitle), conditionName);
        return this;
    }

    // ========== WINDOW AND FRAME CONDITIONS ==========

    /**
     * Wait for a specific number of browser windows to be present
     *
     * @param expectedWindowCount Expected number of windows
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder numberOfWindowsToBe(int expectedWindowCount) {
        String conditionName = "number of windows to be " + expectedWindowCount;
        executeWait(() -> ExpectedConditions.numberOfWindowsToBe(expectedWindowCount), conditionName);
        return this;
    }

    /**
     * Wait for a new window to appear (useful after clicking links that open new tabs)
     *
     * @param currentWindowCount Current number of windows before the action
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder newWindowToAppear(int currentWindowCount) {
        String conditionName = "new window to appear";
        int expectedCount = currentWindowCount + 1;
        executeWait(() -> ExpectedConditions.numberOfWindowsToBe(expectedCount), conditionName);
        return this;
    }

    // ========== ALERT CONDITIONS ==========

    /**
     * Wait for a JavaScript alert to be present
     *
     * @return this builder for potential further chaining
     */
    public PageWaitBuilder alertToBePresent() {
        String conditionName = "alert to be present";
        executeWait(() -> ExpectedConditions.alertIsPresent(), conditionName);
        return this;
    }

    // ========== CONFIGURATION METHODS - These modify wait behavior ==========

    @Override
    public PageWaitBuilder withTimeout(int seconds) {
        return withTimeout(Duration.ofSeconds(seconds));
    }

    @Override
    public PageWaitBuilder withTimeout(Duration duration) {
        this.timeout = duration;
        log().debug("Page wait timeout configured: {}s", duration.getSeconds());
        return this;
    }

    @Override
    public PageWaitBuilder withPollingInterval(Duration duration) {
        this.pollingInterval = duration;
        log().debug("Page wait polling interval configured: {}ms", duration.toMillis());
        return this;
    }

    @Override
    public PageWaitBuilder withMessage(String message) {
        this.customMessage = message;
        log().debug("Custom error message configured for page wait: {}", message);
        return this;
    }

    @Override
    @SafeVarargs
    public final PageWaitBuilder ignoring(Class<? extends Exception>... exceptionTypes) {
        this.ignoredExceptions.addAll(Arrays.asList(exceptionTypes));
        log().debug("Page wait configured to ignore exceptions: {}", Arrays.toString(exceptionTypes));
        return this;
    }

    // ========== PRIVATE EXECUTION LOGIC ==========

    /**
     * Core method that executes the actual wait operation for page-level conditions.
     * Similar to ElementWaitBuilder.executeWait() but optimized for page operations.
     *
     * @param conditionSupplier Supplier that provides the ExpectedCondition
     * @param conditionName Human-readable name for logging and metrics
     */
    private void executeWait(Supplier<? extends ExpectedCondition<?>> conditionSupplier, String conditionName) {
        // Record start time for metrics
        Instant startTime = Instant.now();

        try {
            log().debug("Starting page wait for: {} (timeout: {}s, polling: {}ms)",
                    conditionName, timeout.getSeconds(), pollingInterval.toMillis());

            // Create a fresh WebDriverWait with current configuration
            // Page waits often need different configurations than element waits
            WebDriverWait configuredWait = new WebDriverWait(driver, timeout);
            configuredWait.pollingEvery(pollingInterval);

            // Apply ignored exceptions if any were specified
            if (!ignoredExceptions.isEmpty()) {
                configuredWait.ignoreAll(ignoredExceptions);
                log().debug("Page wait ignoring exceptions: {}", ignoredExceptions);
            }

            // Execute the actual wait
            configuredWait.until(conditionSupplier.get());

            // Calculate duration for metrics
            Duration waitDuration = Duration.between(startTime, Instant.now());

            log().debug("Page wait completed successfully for: {} (took: {}ms)",
                    conditionName, waitDuration.toMillis());

            // Record successful wait in metrics with "page-" prefix to distinguish from element waits
            metrics.recordSuccessfulWait("page-" + conditionName, waitDuration);

        } catch (TimeoutException e) {
            Duration waitDuration = Duration.between(startTime, Instant.now());

            // Create meaningful error message
            String errorMessage = buildTimeoutErrorMessage(conditionName, e);

            log().warn("Page wait timed out for: {} (after: {}ms) - {}",
                    conditionName, waitDuration.toMillis(), errorMessage);

            // Record failed wait in metrics
            metrics.recordFailedWait("page-" + conditionName, waitDuration, e.getMessage());

            // Re-throw with enhanced message
            throw new TimeoutException(errorMessage, e);

        } catch (Exception e) {
            Duration waitDuration = Duration.between(startTime, Instant.now());

            log().error("Unexpected error during page wait for: {} (after: {}ms)",
                    conditionName, waitDuration.toMillis(), e);

            // Record failed wait in metrics
            metrics.recordFailedWait("page-" + conditionName, waitDuration, e.getMessage());

            // Re-throw original exception
            throw e;
        }
    }

    /**
     * Build a comprehensive error message for timeout exceptions in page operations
     */
    private String buildTimeoutErrorMessage(String conditionName, TimeoutException originalException) {
        StringBuilder message = new StringBuilder();

        // Use custom message if provided, otherwise create default
        if (customMessage != null && !customMessage.trim().isEmpty()) {
            message.append(customMessage);
        } else {
            message.append("Timed out waiting for page ").append(conditionName);
        }

        // Add technical details specific to page operations
        message.append(" (current URL: ").append(driver.getCurrentUrl());
        message.append(", page title: '").append(driver.getTitle()).append("'");
        message.append(", timeout: ").append(timeout.getSeconds()).append("s");
        message.append(", polling: ").append(pollingInterval.toMillis()).append("ms)");

        return message.toString();
    }
}