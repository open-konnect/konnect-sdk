package org.konnect.rest.retry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.konnect.rest.RestApiException;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RetryBuilderTest {

    private RetryerBuilder retryerBuilder;

    @BeforeEach
    public void setUp() {
        retryerBuilder = new RetryerBuilder();
    }

    @Test
    public void testRetryIfException_addsExceptions() {
        Retryer retryer = new RetryerBuilder().retryIfException(SocketTimeoutException.class).build();
        assertTrue(retryer.getRetryPolicy().shouldRetry(1, new SocketTimeoutException()));
        assertFalse(retryer.getRetryPolicy().shouldRetry(1, new NullPointerException()));
    }

    @Test
    public void testRetryIfExceptionPredicates_addsPredicates() {
        Predicate<Throwable> retryableException = e -> (e instanceof RestApiException) && ((RestApiException) e).isRetryable();
        Predicate<Throwable> customPredicate = e -> "ShouldRetry".equals(e.getMessage());

        Retryer retryer = new RetryerBuilder().retryIfExceptionPredicates(retryableException, customPredicate).build();

        RetryPolicy retryPolicy = retryer.getRetryPolicy();

        assertTrue(retryPolicy.shouldRetry(1, new RestApiException(503, "Internal Server Error")));
        assertTrue(retryPolicy.shouldRetry(1, new Exception("ShouldRetry")));
        assertFalse(retryPolicy.shouldRetry(1, new Exception("DontRetry")));
    }

    @Test
    public void testRetryIfExceptionOrPredicates_addsExceptionAndPredicates() {
        Predicate<Throwable> retryableException = e -> (e instanceof RestApiException) && ((RestApiException) e).isRetryable();
        Predicate<Throwable> customPredicate = e -> "ShouldRetry".equals(e.getMessage());

        Retryer retryer = new RetryerBuilder()
                .retryIfException(SocketTimeoutException.class)
                .retryIfExceptionPredicates(retryableException, customPredicate).build();

        RetryPolicy retryPolicy = retryer.getRetryPolicy();

        assertTrue(retryer.getRetryPolicy().shouldRetry(1, new SocketTimeoutException()));
        assertFalse(retryer.getRetryPolicy().shouldRetry(1, new NullPointerException()));

        assertTrue(retryPolicy.shouldRetry(1, new RestApiException(503, "Internal Server Error")));
        assertTrue(retryPolicy.shouldRetry(1, new Exception("ShouldRetry")));
        assertFalse(retryPolicy.shouldRetry(1, new Exception("DontRetry")));
    }

    @Test
    public void testRetryAfterFixedDelay_setsFixedDelayPolicy() {
        Retryer retryer = new RetryerBuilder().retryAfterFixedDelay(100).build();
        assertEquals(100, retryer.getWaitPolicy().computeWaitTime(1));
    }

    @Test
    public void testRetryAfterExponentialBackoffDelay_setsExponentialBackoffPolicy() {
        long initialDelay = 100;
        double multiplier = 2.0;
        long maxDelay = 5000;
        boolean jitter = true;

        // exp backoffs : 100, 200, 400, 800, 1600...
        // Jitter is [0 to backoff/2]

        int[] backOffDelays = new int[]{0, 100, 200, 400, 800};
        int[] maxJitters = new int[]{0, 50, 100, 200, 400};
        Retryer retryer = new RetryerBuilder()
                .retryAfterExponentialBackoffDelay(initialDelay, multiplier, maxDelay, jitter).build();
        WaitPolicy waitPolicy = retryer.getWaitPolicy();
        IntStream.range(1, 3).forEachOrdered(k -> {
            long delayForAttempt = waitPolicy.computeWaitTime(k);
            assertTrue(delayForAttempt < backOffDelays[k] + maxJitters[k] && delayForAttempt > backOffDelays[k-1] + maxJitters[k-1]);
        });
    }

    @Test
    public void testStopAfterMaxRetries_setsMaxRetriesPolicy() {
        int maxRetries = 2;
        Retryer retryer = new RetryerBuilder().stopAfterMaxRetries(maxRetries).build();
        StopPolicy stopPolicy = retryer.getStopPolicy();

        assertEquals(StopPolicy.StopPolicyResult.CONTINUE, stopPolicy.shouldStop(maxRetries - 1, 0));
        assertEquals(StopPolicy.StopPolicyResult.MAX_RETRY_EXHAUSTED, stopPolicy.shouldStop(maxRetries, 0));
    }

    @Test
    public void testStopAfterMaxElapsedTime_setsMaxElapsedTimePolicy() {
        long maxElapsedTimeMillis = 1000;
        Retryer retryer = new RetryerBuilder().stopAfterMaxElapsedTime(maxElapsedTimeMillis).build();
        StopPolicy stopPolicy = retryer.getStopPolicy();

        assertEquals(StopPolicy.StopPolicyResult.CONTINUE, stopPolicy.shouldStop(1000, maxElapsedTimeMillis - 1));
        assertEquals(StopPolicy.StopPolicyResult.MAX_RETRY_TIME_EXHAUSTED, stopPolicy.shouldStop(1000, maxElapsedTimeMillis));
    }

    @Test
    public void testBuild_combinedStopPolicies() {
        int maxRetries = 2;
        long maxElapsedTimeMillis = 1000;

        Retryer retryer = new RetryerBuilder().stopAfterMaxRetries(maxRetries)
                .stopAfterMaxElapsedTime(maxElapsedTimeMillis).build();

        StopPolicy stopPolicy = retryer.getStopPolicy();

        assertEquals(StopPolicy.StopPolicyResult.CONTINUE, stopPolicy.shouldStop(maxRetries - 1, maxElapsedTimeMillis - 1));
        assertEquals(StopPolicy.StopPolicyResult.MAX_RETRY_EXHAUSTED, stopPolicy.shouldStop(maxRetries, maxElapsedTimeMillis - 1));
        assertEquals(StopPolicy.StopPolicyResult.MAX_RETRY_TIME_EXHAUSTED, stopPolicy.shouldStop(maxRetries - 1, maxElapsedTimeMillis));
    }

    @Test
    public void testStopRetryAttemptIfTimeout_setsAttemptTimePolicy() {
        Duration timeout = Duration.ofSeconds(5);
        Retryer retryer = new RetryerBuilder().stopRetryAttemptIfTimeout(timeout).build();
        AttemptTimePolicy attemptTimePolicy = retryer.getAttemptTimePolicy();
        assertEquals(timeout, attemptTimePolicy.getTimeout());
    }

}

