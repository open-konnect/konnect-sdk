package org.konnect.rest.retry;

import lombok.Getter;

import java.time.Duration;
import java.util.concurrent.*;


@Getter
public class Retryer {

    private final RetryPolicy retryPolicy;
    private final WaitPolicy waitPolicy;
    private final StopPolicy stopPolicy;
    private final AttemptTimePolicy attemptTimePolicy;
    private final ExecutorService executorService;

    // Constructor
    public Retryer(RetryPolicy retryPolicy, WaitPolicy waitPolicy, StopPolicy stopPolicy, AttemptTimePolicy attemptTimePolicy) {
        this.retryPolicy = retryPolicy;
        this.waitPolicy = waitPolicy;
        this.stopPolicy = stopPolicy;
        this.attemptTimePolicy = attemptTimePolicy;
        this.executorService = Executors.newSingleThreadExecutor(); // for timeouts
    }

    // Execute method that retries the operation based on policies
    public <T> T execute(Callable<T> operation) throws Exception {
        int attempts = 0;
        long startTime = System.currentTimeMillis();

        while (true) {
            attempts++;
            try {
                Duration attemptTimeout = attemptTimePolicy.getTimeout();
                return executeWithTimeout(operation, attemptTimeout); // Execute with timeout
            } catch (Exception e) {
                if (!retryPolicy.shouldRetry(attempts, e)) {
                    throw new RetryException(attempts, "Retry stopped due to retry conditions not met", e);
                }

                long elapsedTime = System.currentTimeMillis() - startTime;
                StopPolicy.StopPolicyResult stopPolicyResult = stopPolicy.shouldStop(attempts, elapsedTime);
                if (stopPolicyResult != StopPolicy.StopPolicyResult.CONTINUE) {
                    throw new RetryException(attempts, String.format("Retry stopped due to %s", stopPolicyResult.name()), e);
                }

                Thread.sleep(waitPolicy.computeWaitTime(attempts)); // Wait before retrying
            }
        }
    }

    // Executes the operation with a timeout
    private <T> T executeWithTimeout(Callable<T> operation, Duration timeout) throws Exception {
        if (timeout == null) {
            return operation.call();
        }

        Future<T> future = executorService.submit(operation); // Submit the task to executor
        final long timeoutMillis = timeout.toMillis();
        try {
            // Attempt to get the result within the timeout
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // Cancel the task if it times out
            throw new TimeoutException("Attempt timed out after " + timeoutMillis + " ms");
        } catch (ExecutionException e) {
            // Unwrap the underlying exception
            throw (e.getCause() instanceof Exception) ? (Exception) e.getCause() : e;
        } finally {
            future.cancel(true); // Ensure task is canceled if interrupted or failed
        }
    }

    // Shutdown the executor service when no longer needed
    public void shutdown() {
        executorService.shutdown();
    }
}
