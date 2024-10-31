package org.konnect.rest.retry;

import java.time.Duration;

public interface AttemptTimePolicy {
    /**
     * Returns the maximum allowed duration for a single attempt.
     *
     * @return the timeout duration in milliseconds for each attempt
     */
    Duration getTimeout();

    /**
     * Static factory method to create an AttemptTimePolicy with a specified timeout.
     */
    static AttemptTimePolicy of(Duration duration) {
        return () -> duration;
    }

    /**
     * No timeout policy.
     */
    static AttemptTimePolicy noTimeout() {
        return () -> null;
    }
}

