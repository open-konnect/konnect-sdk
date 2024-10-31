package org.konnect.rest.retry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

public class RetryerTest {

    private Retryer retryer;

    @AfterEach
    public void tearDown() {
        if (retryer != null) {
            retryer.shutdown();
        }
    }

    @Test
    public void testSuccessfulExecution_noRetries() throws Exception {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(3)
                .retryAfterFixedDelay(100)
                .build();

        Callable<String> operation = () -> "Success";
        String result = retryer.execute(operation);

        assertEquals("Success", result);
    }

    @Test
    public void testRetryOnExceptionRetryable() throws Exception {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(3)
                .retryAfterFixedDelay(100)
                .retryIfException(RuntimeException.class)
                .build();

        Callable<String> operation = new Callable<String>() {
            int attempts = 0;

            @Override
            public String call() throws Exception {
                if (attempts++ < 2) {
                    throw new RuntimeException("Retryable failure");
                }
                return "Success";
            }
        };

        String result = retryer.execute(operation);
        assertEquals("Success", result);
    }

    @Test
    public void testRetryStopsAfterMaxAttempts() {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(2)
                .retryAfterFixedDelay(100)
                .retryIfException(RuntimeException.class)
                .build();

        Callable<String> operation = () -> {
            throw new RuntimeException("Always fails");
        };

        RetryException exception = assertThrows(RetryException.class, () -> retryer.execute(operation));
        assertEquals(2, exception.getAttempts());
        assertTrue(exception.getMessage().contains(StopPolicy.StopPolicyResult.MAX_RETRY_EXHAUSTED.name()));
    }

    @Test
    public void testStopAfterMaxElapsedTime() {
        retryer = new RetryerBuilder()
                .stopAfterMaxElapsedTime(200)
                .retryAfterFixedDelay(100)
                .retryIfException(RuntimeException.class)
                .build();

        Callable<String> operation = () -> {
            throw new RuntimeException("Always fails");
        };

        RetryException exception = assertThrows(RetryException.class, () -> retryer.execute(operation));
        assertTrue(exception.getMessage().contains(StopPolicy.StopPolicyResult.MAX_RETRY_TIME_EXHAUSTED.name()));
    }

    @Test
    public void testExecutionWithTimeoutExceeded() {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(3)
                .retryAfterFixedDelay(100)
                .stopRetryAttemptIfTimeout(Duration.ofMillis(100))
                .retryIfException(RuntimeException.class)
                .build();

        Callable<String> operation = () -> {
            Thread.sleep(200);
            return "Success";
        };

        assertThrows(RetryException.class, () -> retryer.execute(operation));
    }

    @Test
    public void testExponentialBackoffWithJitter() throws Exception {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(3)
                .retryAfterExponentialBackoffDelay(50, 2.0, 1000, true)
                .retryIfException(RuntimeException.class)
                .build();

        Callable<String> operation = new Callable<String>() {
            int attempts = 0;

            @Override
            public String call() throws Exception {
                if (attempts++ < 2) {
                    throw new RuntimeException("Retryable failure");
                }
                return "Success";
            }
        };

        String result = retryer.execute(operation);
        assertEquals("Success", result);
    }

    @Test
    public void testRetryPolicyBasedOnPredicate() throws Exception {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(3)
                .retryAfterFixedDelay(100)
                .retryIfExceptionPredicates(
                        ex -> ex instanceof IllegalArgumentException,
                        ex -> ex.getMessage().contains("Retryable")
                )
                .build();

        Callable<String> operation = new Callable<String>() {
            int attempts = 0;

            @Override
            public String call() throws Exception {
                if (attempts++ < 2) {
                    throw new IllegalArgumentException("Retryable error");
                }
                return "Success";
            }
        };

        String result = retryer.execute(operation);
        assertEquals("Success", result);
    }

    @Test
    public void testNonRetryableExceptionDoesNotRetry() {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(3)
                .retryAfterFixedDelay(100)
                .retryIfException(IllegalArgumentException.class)
                .build();

        Callable<String> operation = () -> {
            throw new RuntimeException("Non-retryable error");
        };

        RetryException exception = assertThrows(RetryException.class, () -> retryer.execute(operation));
        assertEquals(1, exception.getAttempts());
        assertTrue(exception.getMessage().contains("Retry stopped due to retry conditions not met"));
    }

    @Test
    public void testShutdownAfterExecution() throws Exception {
        retryer = new RetryerBuilder()
                .stopAfterMaxRetries(1)
                .retryAfterFixedDelay(100)
                .retryIfException(RuntimeException.class)
                .build();

        Callable<String> operation = () -> "Success";

        String result = retryer.execute(operation);
        assertEquals("Success", result);

        retryer.shutdown();
        assertTrue(retryer.getExecutorService().isShutdown());
    }
}


