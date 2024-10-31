package org.konnect.rest.retry;

import java.util.Arrays;
import java.util.Objects;

public interface StopPolicy {

    enum StopPolicyResult {
        CONTINUE, MAX_RETRY_EXHAUSTED, MAX_RETRY_TIME_EXHAUSTED
    }

    /**
     * Determines if retries should stop based on attempt count and total elapsed time.
     *
     * @param attemptCount the number of attempts made so far
     * @param elapsedTimeMillis total time elapsed since the first attempt
     * @return StopPolicyResult : CONTINUE if retry should continue, stop otherwise
     */
    StopPolicyResult shouldStop(int attemptCount, long elapsedTimeMillis);

    /**
     * Stop after a maximum number of retries.
     */
    static StopPolicy maxRetries(int maxAttempts) {
        return (attemptCount, elapsedTimeMillis) ->
                attemptCount >= maxAttempts
                        ? StopPolicyResult.MAX_RETRY_EXHAUSTED
                        : StopPolicyResult.CONTINUE;
    }

    /**
     * Stop after a certain total duration.
     */
    static StopPolicy maxElapsedTime(long maxElapsedTimeMillis) {
        return (attemptCount, elapsedTimeMillis) ->
                elapsedTimeMillis >= maxElapsedTimeMillis
                ? StopPolicyResult.MAX_RETRY_TIME_EXHAUSTED
                : StopPolicyResult.CONTINUE;
    }

    /**
     * Composite StopPolicy that stops if any of the provided policies should stop.
     */
    static StopPolicy combined(StopPolicy... policies) {
        return (attemptCount, elapsedTimeMillis) ->
                Arrays.stream(policies)
                        .filter(Objects::nonNull)
                        .map(p -> p.shouldStop(attemptCount, elapsedTimeMillis))
                        .filter(r -> r != StopPolicyResult.CONTINUE)
                        .findFirst()
                        .orElse(StopPolicyResult.CONTINUE);
    }
}

