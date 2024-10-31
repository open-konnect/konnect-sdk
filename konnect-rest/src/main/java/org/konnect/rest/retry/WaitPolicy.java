package org.konnect.rest.retry;

@FunctionalInterface
public interface WaitPolicy {
    /**
     * Calculates the wait time before the next retry.
     *
     * @param attemptCount the number of attempts made so far
     * @return the duration in milliseconds to wait before the next attempt
     */
    long computeWaitTime(int attemptCount);

    /**
     * Static method for exponential backoff with optional jitter.
     */
    static WaitPolicy exponentialBackoff(long initialDelay, double multiplier, long maxDelay, boolean jitter) {
        return attemptCount -> {
            long delay = Math.min((long) (initialDelay * Math.pow(multiplier, attemptCount - 1)), maxDelay);
            return jitter ? delay + (long) (Math.random() * delay / 2) : delay;
        };
    }

    /**
     * Static method for a fixed wait time.
     */
    static WaitPolicy fixed(long delay) {
        return attemptCount -> delay;
    }
}

