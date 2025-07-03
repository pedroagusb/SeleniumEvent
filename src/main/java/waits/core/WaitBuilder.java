package waits.core;

import java.time.Duration;

/**
 * Base interface for all wait builders providing common configuration methods.
 * @param <T> The builder type for method chaining
 */
public interface WaitBuilder<T> {

    /**
     * Set custom timeout in seconds
     * @param seconds timeout duration
     * @return builder instance for method chaining
     */
    T withTimeout(int seconds);

    /**
     * Set custom timeout as Duration
     * @param duration timeout duration
     * @return builder instance for method chaining
     */
    T withTimeout(Duration duration);

    /**
     * Set custom polling interval
     * @param duration polling interval
     * @return builder instance for method chaining
     */
    T withPollingInterval(Duration duration);

    /**
     * Set custom error message for timeout exceptions
     * @param message custom message
     * @return builder instance for method chaining
     */
    T withMessage(String message);

    /**
     * Add exception types to ignore during waiting
     * @param exceptionTypes exception classes to ignore
     * @return builder instance for method chaining
     */
    @SuppressWarnings("unchecked")
    T ignoring(Class<? extends Exception>... exceptionTypes);
}