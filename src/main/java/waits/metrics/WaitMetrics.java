package waits.metrics;

import logging.Logging;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collects and analyzes performance metrics for wait operations.
 * Provides insights into wait times, success rates, and performance patterns.
 * <p>
 * This class is thread-safe and can be used across multiple test threads
 * to aggregate metrics from parallel test execution.
 */
public class WaitMetrics implements Logging {

    // Thread-safe counters for basic statistics
    private final AtomicInteger totalWaitsAttempted = new AtomicInteger(0);
    private final AtomicInteger totalWaitsSuccessful = new AtomicInteger(0);
    private final AtomicInteger totalWaitsFailed = new AtomicInteger(0);

    // Thread-safe accumulator for total time spent waiting
    private final AtomicLong totalWaitTimeMillis = new AtomicLong(0);

    // Thread-safe maps for detailed metrics by condition type
    private final Map<String, AtomicInteger> successfulWaitsByCondition = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> failedWaitsByCondition = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> totalTimeByCondition = new ConcurrentHashMap<>();

    // Store individual wait records for detailed analysis
    // Using Collections.synchronizedList for thread safety
    private final List<WaitRecord> waitRecords = Collections.synchronizedList(new ArrayList<>());

    // Configuration for how much detail to keep
    private final int maxRecordsToKeep;
    private final boolean enableDetailedRecords;

    /**
     * Create WaitMetrics with default configuration
     * Keeps last 1000 wait records for detailed analysis
     */
    public WaitMetrics() {
        this(1000, true);
    }

    /**
     * Create WaitMetrics with custom configuration
     *
     * @param maxRecordsToKeep Maximum number of detailed records to keep in memory
     * @param enableDetailedRecords Whether to store individual wait records
     */
    public WaitMetrics(int maxRecordsToKeep, boolean enableDetailedRecords) {
        this.maxRecordsToKeep = maxRecordsToKeep;
        this.enableDetailedRecords = enableDetailedRecords;
        log().debug("WaitMetrics initialized - maxRecords: {}, detailedRecords: {}",
                maxRecordsToKeep, enableDetailedRecords);
    }

    /**
     * Record a successful wait operation
     *
     * @param conditionName Human-readable name of the condition that was waited for
     * @param duration How long the wait took to complete
     */
    public void recordSuccessfulWait(String conditionName, Duration duration) {
        // Update overall counters
        totalWaitsAttempted.incrementAndGet();
        totalWaitsSuccessful.incrementAndGet();
        totalWaitTimeMillis.addAndGet(duration.toMillis());

        // Update condition-specific counters
        successfulWaitsByCondition.computeIfAbsent(conditionName, k -> new AtomicInteger(0)).incrementAndGet();
        totalTimeByCondition.computeIfAbsent(conditionName, k -> new AtomicLong(0)).addAndGet(duration.toMillis());

        // Store detailed record if enabled
        if (enableDetailedRecords) {
            WaitRecord record = new WaitRecord(
                    Instant.now(),
                    conditionName,
                    duration,
                    true,
                    null, // No error message for successful waits
                    Thread.currentThread().getName()
            );

            addWaitRecord(record);
        }

        log().debug("Recorded successful wait: {} took {}ms", conditionName, duration.toMillis());
    }

    /**
     * Record a failed wait operation
     *
     * @param conditionName Human-readable name of the condition that failed
     * @param duration How long we waited before timing out
     * @param errorMessage Error message from the timeout exception
     */
    public void recordFailedWait(String conditionName, Duration duration, String errorMessage) {
        // Update overall counters
        totalWaitsAttempted.incrementAndGet();
        totalWaitsFailed.incrementAndGet();
        totalWaitTimeMillis.addAndGet(duration.toMillis());

        // Update condition-specific counters
        failedWaitsByCondition.computeIfAbsent(conditionName, k -> new AtomicInteger(0)).incrementAndGet();
        totalTimeByCondition.computeIfAbsent(conditionName, k -> new AtomicLong(0)).addAndGet(duration.toMillis());

        // Store detailed record if enabled
        if (enableDetailedRecords) {
            WaitRecord record = new WaitRecord(
                    Instant.now(),
                    conditionName,
                    duration,
                    false,
                    errorMessage,
                    Thread.currentThread().getName()
            );

            addWaitRecord(record);
        }

        log().warn("Recorded failed wait: {} failed after {}ms - {}",
                conditionName, duration.toMillis(), errorMessage);
    }

    /**
     * Get overall success rate as a percentage
     *
     * @return Success rate between 0.0 and 100.0, or 0.0 if no waits have been attempted
     */
    public double getSuccessRate() {
        int total = totalWaitsAttempted.get();
        if (total == 0) {
            return 0.0;
        }
        return (double) totalWaitsSuccessful.get() / total * 100.0;
    }

    /**
     * Get average wait time across all conditions
     *
     * @return Average wait time, or Duration.ZERO if no waits have been attempted
     */
    public Duration getAverageWaitTime() {
        int total = totalWaitsAttempted.get();
        if (total == 0) {
            return Duration.ZERO;
        }

        long averageMillis = totalWaitTimeMillis.get() / total;
        return Duration.ofMillis(averageMillis);
    }

    /**
     * Get average wait time for a specific condition
     *
     * @param conditionName Name of the condition to analyze
     * @return Average wait time for this condition, or Duration.ZERO if condition not found
     */
    public Duration getAverageWaitTimeForCondition(String conditionName) {
        AtomicLong totalTime = totalTimeByCondition.get(conditionName);
        if (totalTime == null) {
            return Duration.ZERO;
        }

        // Calculate total attempts for this condition (successful + failed)
        int successfulCount = successfulWaitsByCondition.getOrDefault(conditionName, new AtomicInteger(0)).get();
        int failedCount = failedWaitsByCondition.getOrDefault(conditionName, new AtomicInteger(0)).get();
        int totalCount = successfulCount + failedCount;

        if (totalCount == 0) {
            return Duration.ZERO;
        }

        long averageMillis = totalTime.get() / totalCount;
        return Duration.ofMillis(averageMillis);
    }

    /**
     * Get success rate for a specific condition
     *
     * @param conditionName Name of the condition to analyze
     * @return Success rate between 0.0 and 100.0, or 0.0 if condition not found
     */
    public double getSuccessRateForCondition(String conditionName) {
        int successfulCount = successfulWaitsByCondition.getOrDefault(conditionName, new AtomicInteger(0)).get();
        int failedCount = failedWaitsByCondition.getOrDefault(conditionName, new AtomicInteger(0)).get();
        int totalCount = successfulCount + failedCount;

        if (totalCount == 0) {
            return 0.0;
        }

        return (double) successfulCount / totalCount * 100.0;
    }

    /**
     * Get list of all condition names that have been recorded
     *
     * @return Set of condition names, sorted alphabetically
     */
    public Set<String> getAllConditionNames() {
        Set<String> allConditions = new TreeSet<>(); // TreeSet for automatic sorting
        allConditions.addAll(successfulWaitsByCondition.keySet());
        allConditions.addAll(failedWaitsByCondition.keySet());
        return allConditions;
    }

    /**
     * Get the slowest waits recorded
     *
     * @param limit Maximum number of records to return
     * @return List of slowest wait records, sorted by duration (slowest first)
     */
    public List<WaitRecord> getSlowestWaits(int limit) {
        if (!enableDetailedRecords) {
            log().warn("Cannot provide slowest waits - detailed records are disabled");
            return Collections.emptyList();
        }

        return waitRecords.stream()
                .sorted((r1, r2) -> r2.getDuration().compareTo(r1.getDuration())) // Sort descending by duration
                .limit(limit)
                .collect(ArrayList::new, (list, record) -> list.add(record), (list1, list2) -> list1.addAll(list2));
    }

    /**
     * Get recent failed waits for debugging
     *
     * @param limit Maximum number of records to return
     * @return List of recent failed waits, sorted by time (most recent first)
     */
    public List<WaitRecord> getRecentFailedWaits(int limit) {
        if (!enableDetailedRecords) {
            log().warn("Cannot provide recent failed waits - detailed records are disabled");
            return Collections.emptyList();
        }

        return waitRecords.stream()
                .filter(record -> !record.isSuccessful()) // Only failed waits
                .sorted((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp())) // Sort by time, newest first
                .limit(limit)
                .collect(ArrayList::new, (list, record) -> list.add(record), (list1, list2) -> list1.addAll(list2));
    }

    /**
     * Generate a comprehensive metrics summary report
     *
     * @return Multi-line string containing formatted metrics summary
     */
    public String generateSummaryReport() {
        StringBuilder report = new StringBuilder();

        report.append("=== Wait Metrics Summary ===%n");
        report.append(String.format("Total waits attempted: %d%n", totalWaitsAttempted.get()));
        report.append(String.format("Successful waits: %d%n", totalWaitsSuccessful.get()));
        report.append(String.format("Failed waits: %d%n", totalWaitsFailed.get()));
        report.append(String.format("Overall success rate: %.2f%%%n", getSuccessRate()));
        report.append(String.format("Average wait time: %dms%n", getAverageWaitTime().toMillis()));

        if (!getAllConditionNames().isEmpty()) {
            report.append("%n=== By Condition Type ===%n");
            for (String condition : getAllConditionNames()) {
                int successful = successfulWaitsByCondition.getOrDefault(condition, new AtomicInteger(0)).get();
                int failed = failedWaitsByCondition.getOrDefault(condition, new AtomicInteger(0)).get();
                double successRate = getSuccessRateForCondition(condition);
                long avgTime = getAverageWaitTimeForCondition(condition).toMillis();

                report.append(String.format("%s: %d successful, %d failed (%.1f%% success, %dms avg)%n",
                        condition, successful, failed, successRate, avgTime));
            }
        }

        return report.toString();
    }

    /**
     * Clear all collected metrics
     * Useful for resetting metrics between test suites
     */
    public void clear() {
        totalWaitsAttempted.set(0);
        totalWaitsSuccessful.set(0);
        totalWaitsFailed.set(0);
        totalWaitTimeMillis.set(0);

        successfulWaitsByCondition.clear();
        failedWaitsByCondition.clear();
        totalTimeByCondition.clear();
        waitRecords.clear();

        log().debug("All wait metrics cleared");
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Add a wait record, managing the size limit
     */
    private void addWaitRecord(WaitRecord record) {
        waitRecords.add(record);

        // Remove oldest records if we exceed the limit
        // This prevents memory issues during long test runs
        if (waitRecords.size() > maxRecordsToKeep) {
            int excessRecords = waitRecords.size() - maxRecordsToKeep;
            for (int i = 0; i < excessRecords; i++) {
                waitRecords.remove(0); // Remove from beginning (oldest records)
            }

            log().debug("Removed {} old wait records to stay within limit of {}",
                    excessRecords, maxRecordsToKeep);
        }
    }

    // ========== NESTED RECORD CLASS ==========

    /**
     * Immutable record representing a single wait operation
     * Contains all the details needed for detailed analysis
     */
    public static class WaitRecord {
        private final Instant timestamp;
        private final String conditionName;
        private final Duration duration;
        private final boolean successful;
        private final String errorMessage;
        private final String threadName;

        public WaitRecord(Instant timestamp, String conditionName, Duration duration,
                          boolean successful, String errorMessage, String threadName) {
            this.timestamp = timestamp;
            this.conditionName = conditionName;
            this.duration = duration;
            this.successful = successful;
            this.errorMessage = errorMessage;
            this.threadName = threadName;
        }

        // Getters for all fields
        public Instant getTimestamp() { return timestamp; }
        public String getConditionName() { return conditionName; }
        public Duration getDuration() { return duration; }
        public boolean isSuccessful() { return successful; }
        public String getErrorMessage() { return errorMessage; }
        public String getThreadName() { return threadName; }

        @Override
        public String toString() {
            return String.format("WaitRecord{%s, %s, %dms, %s, thread=%s}",
                    timestamp, conditionName, duration.toMillis(),
                    successful ? "SUCCESS" : "FAILED", threadName);
        }
    }
}