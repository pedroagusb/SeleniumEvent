package waits.builders;

import logging.Logging;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * Fluent builder for creating element-specific waits.
 * Provides a chainable API for waiting on WebElement conditions.
 * <p>
 * Usage example:
 * waitFor(loginButton).toBeClickable().withTimeout(10);
 */
public class ElementWaitBuilder implements WaitBuilder<ElementWaitBuilder>, Logging {

    // Core dependencies - injected through constructor
    private final WebDriver driver;
    private final WebElement element;
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
     * @param driver WebDriver instance to use for waiting
     * @param element WebElement to wait for
     * @param defaultWait Pre-configured WebDriverWait with default settings
     * @param metrics Metrics collector for performance monitoring
     */
    public ElementWaitBuilder(WebDriver driver, WebElement element, WebDriverWait defaultWait, WaitMetrics metrics) {
        this.driver = driver;
        this.element = element;
        this.webDriverWait = defaultWait;
        this.metrics = metrics;

        // Initialize with default values from Constants
        this.timeout = Constants.getElementTimeout();
        this.pollingInterval = Constants.getPollingInterval();
        this.ignoredExceptions = new HashSet<>();

        log().debug("ElementWaitBuilder created for element: {}", element.getTagName());
    }

    // ========== CONDITION METHODS - These execute the wait immediately ==========

    /**
     * Wait for element to become clickable (visible and enabled)
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toBeClickable() {
        String conditionName = "element to be clickable";
        executeWait(() -> ExpectedConditions.elementToBeClickable(element), conditionName);
        return this;
    }

    /**
     * Wait for element to become visible
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toBeVisible() {
        String conditionName = "element to be visible";
        executeWait(() -> ExpectedConditions.visibilityOf(element), conditionName);
        return this;
    }

    /**
     * Wait for element to become invisible
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toBeInvisible() {
        String conditionName = "element to be invisible";
        executeWait(() -> ExpectedConditions.invisibilityOf(element), conditionName);
        return this;
    }

    /**
     * Wait for element to contain specific text
     * @param text Expected text content
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toHaveText(String text) {
        String conditionName = "element to have text '" + text + "'";
        executeWait(() -> ExpectedConditions.textToBePresentInElement(element, text), conditionName);
        return this;
    }

    /**
     * Wait for element to contain partial text
     * @param partialText Expected partial text content
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toContainText(String partialText) {
        String conditionName = "element to contain text '" + partialText + "'";
        executeWait(() -> ExpectedConditions.textToBePresentInElement(element, partialText), conditionName);
        return this;
    }

    /**
     * Wait for element to have specific attribute value
     * @param attribute Attribute name
     * @param value Expected attribute value
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toHaveAttribute(String attribute, String value) {
        String conditionName = "element to have attribute '" + attribute + "' with value '" + value + "'";
        executeWait(() -> ExpectedConditions.attributeToBe(element, attribute, value), conditionName);
        return this;
    }

    /**
     * Wait for element to be selected (for checkboxes, radio buttons, options)
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toBeSelected() {
        String conditionName = "element to be selected";
        executeWait(() -> ExpectedConditions.elementToBeSelected(element), conditionName);
        return this;
    }

    /**
     * Wait for element to become stale (no longer attached to DOM)
     * @return this builder for potential further chaining
     */
    public ElementWaitBuilder toBeStale() {
        String conditionName = "element to become stale";
        executeWait(() -> ExpectedConditions.stalenessOf(element), conditionName);
        return this;
    }

    // ========== CONFIGURATION METHODS - These modify wait behavior ==========

    @Override
    public ElementWaitBuilder withTimeout(int seconds) {
        return withTimeout(Duration.ofSeconds(seconds));
    }

    @Override
    public ElementWaitBuilder withTimeout(Duration duration) {
        this.timeout = duration;
        log().debug("Timeout configured: {}s", duration.getSeconds());
        return this;
    }

    @Override
    public ElementWaitBuilder withPollingInterval(Duration duration) {
        this.pollingInterval = duration;
        log().debug("Polling interval configured: {}ms", duration.toMillis());
        return this;
    }

    @Override
    public ElementWaitBuilder withMessage(String message) {
        this.customMessage = message;
        log().debug("Custom error message configured: {}", message);
        return this;
    }

    @Override
    @SafeVarargs
    public final ElementWaitBuilder ignoring(Class<? extends Exception>... exceptionTypes) {
        this.ignoredExceptions.addAll(Arrays.asList(exceptionTypes));
        log().debug("Configured to ignore exceptions: {}", Arrays.toString(exceptionTypes));
        return this;
    }

    // ========== PRIVATE EXECUTION LOGIC ==========

    /**
     * Core method that executes the actual wait operation.
     * This is where Selenium's WebDriverWait gets configured and executed.
     *
     * @param conditionSupplier Supplier that provides the ExpectedCondition
     * @param conditionName Human-readable name for logging and metrics
     */
    private void executeWait(Supplier<ExpectedCondition<?>> conditionSupplier, String conditionName) {
        // Record start time for metrics
        Instant startTime = Instant.now();

        try {
            log().debug("Starting wait for: {} (timeout: {}s, polling: {}ms)",
                    conditionName, timeout.getSeconds(), pollingInterval.toMillis());

            // Create a fresh WebDriverWait with current configuration
            WebDriverWait configuredWait = new WebDriverWait(driver, timeout);
            configuredWait.pollingEvery(pollingInterval);

            // Apply ignored exceptions if any were specified
            if (!ignoredExceptions.isEmpty()) {
                configuredWait.ignoreAll(ignoredExceptions);
            }

            // Execute the actual wait
            configuredWait.until(conditionSupplier.get());

            // Calculate duration for metrics
            Duration waitDuration = Duration.between(startTime, Instant.now());

            log().debug("Wait completed successfully for: {} (took: {}ms)",
                    conditionName, waitDuration.toMillis());

            // Record successful wait in metrics
            metrics.recordSuccessfulWait(conditionName, waitDuration);

        } catch (TimeoutException e) {
            Duration waitDuration = Duration.between(startTime, Instant.now());

            // Create meaningful error message
            String errorMessage = buildTimeoutErrorMessage(conditionName, e);

            log().warn("Wait timed out for: {} (after: {}ms) - {}",
                    conditionName, waitDuration.toMillis(), errorMessage);

            // Record failed wait in metrics
            metrics.recordFailedWait(conditionName, waitDuration, e.getMessage());

            // Re-throw with enhanced message
            throw new TimeoutException(errorMessage, e);

        } catch (Exception e) {
            Duration waitDuration = Duration.between(startTime, Instant.now());

            log().error("Unexpected error during wait for: {} (after: {}ms)",
                    conditionName, waitDuration.toMillis(), e);

            // Record failed wait in metrics
            metrics.recordFailedWait(conditionName, waitDuration, e.getMessage());

            // Re-throw original exception
            throw e;
        }
    }

    /**
     * Build a comprehensive error message for timeout exceptions
     */
    private String buildTimeoutErrorMessage(String conditionName, TimeoutException originalException) {
        StringBuilder message = new StringBuilder();

        // Use custom message if provided, otherwise create default
        if (customMessage != null && !customMessage.trim().isEmpty()) {
            message.append(customMessage);
        } else {
            message.append("Timed out waiting for ").append(conditionName);
        }

        // Add technical details
        message.append(" (element: ").append(element.getTagName());
        message.append(", timeout: ").append(timeout.getSeconds()).append("s");
        message.append(", polling: ").append(pollingInterval.toMillis()).append("ms)");

        return message.toString();
    }
}