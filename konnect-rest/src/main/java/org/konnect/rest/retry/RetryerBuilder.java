package org.konnect.rest.retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

public class RetryerBuilder {

    /** Defaults **/
    private static final StopPolicy DEFAULT_STOP_POLICY = StopPolicy.maxRetries(3);
    private static final WaitPolicy DEFAULT_WAIT_POLICY = WaitPolicy.fixed(1000); // default fixed wait of 1 second
    private static final AttemptTimePolicy DEFAULT_TIMEOUT_POLICY = AttemptTimePolicy.noTimeout(); // default no timeout for each attempt

    private WaitPolicy waitPolicy = WaitPolicy.fixed(1000); // default fixed wait of 1 second
    private AttemptTimePolicy attemptTimePolicy = AttemptTimePolicy.noTimeout(); // default no timeout for each attempt

    private StopPolicy maxRetries;
    private StopPolicy maxElapsedTime;
    private final List<Class<? extends Throwable>> exceptionTypes = new ArrayList<>();
    private final List<Predicate<Throwable>> predicateList = new ArrayList<>();


    /*********************** Retry Policies *************************/
    @SafeVarargs
    public final RetryerBuilder retryIfException(Class<? extends Throwable>... exceptions) {
        exceptionTypes.addAll(Arrays.asList(exceptions));
        return this;
    }

    @SafeVarargs
    public final RetryerBuilder retryIfExceptionPredicates(Predicate<Throwable>... predicates) {
        predicateList.addAll(Arrays.asList(predicates));
        return this;
    }

    /********************** Wait Policies ************************/
    // Method to set WaitPolicy
    public final RetryerBuilder retryAfterFixedDelay(long delay) {
        this.waitPolicy = WaitPolicy.fixed(delay);
        return this;
    }

    // Convenience method for exponential backoff with jitter
    public final RetryerBuilder retryAfterExponentialBackoffDelay(long initialDelay, double multiplier, long maxDelay, boolean jitter) {
        this.waitPolicy = WaitPolicy.exponentialBackoff(initialDelay, multiplier, maxDelay, jitter);
        return this;
    }

    // Method to set AttemptTimePolicy
    public final RetryerBuilder stopRetryAttemptIfTimeout(Duration timeout) {
        this.attemptTimePolicy = AttemptTimePolicy.of(timeout);
        return this;
    }



    /********************** Stop Policies ************************/
    // Convenience method for max retries
    public final RetryerBuilder stopAfterMaxRetries(int maxRetries) {
        this.maxRetries = StopPolicy.maxRetries(maxRetries);
        return this;
    }

    // Convenience method for max elapsed time
    public final RetryerBuilder stopAfterMaxElapsedTime(long maxElapsedTimeMillis) {
        this.maxElapsedTime = StopPolicy.maxElapsedTime(maxElapsedTimeMillis);
        return this;
    }

    // Build method to create a Retryer instance
    public final Retryer build() {
        // Apply defaults if not provided
        if (attemptTimePolicy == null) attemptTimePolicy = DEFAULT_TIMEOUT_POLICY;
        if (waitPolicy == null) waitPolicy = DEFAULT_WAIT_POLICY;

        final StopPolicy maxRetryPolicy = maxRetries != null ? maxRetries : DEFAULT_STOP_POLICY; // default
        final StopPolicy stopPolicy = maxElapsedTime != null
                ? StopPolicy.combined(maxRetries, maxElapsedTime)
                : maxRetryPolicy;

        if (exceptionTypes.isEmpty() && predicateList.isEmpty()) exceptionTypes.add(Exception.class);

        // By Default retry on TimeoutException if a timeout policy is attached.
        if (attemptTimePolicy != null) exceptionTypes.add(TimeoutException.class);

        final RetryPolicy exceptionPolicy = RetryPolicy.withExceptionClasses(exceptionTypes);
        final RetryPolicy predicatePolicy = predicateList.isEmpty() ? null : RetryPolicy.withExceptionPredicates(predicateList);

        final RetryPolicy mergedRetryPolicy = predicatePolicy == null ? exceptionPolicy : RetryPolicy.combined(exceptionPolicy, predicatePolicy);
        return new Retryer(mergedRetryPolicy, waitPolicy, stopPolicy, attemptTimePolicy);
    }
}
