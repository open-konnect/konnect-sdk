package org.konnect.rest.retry;

import java.util.function.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public interface RetryPolicy {
    /**
     * Determines whether a retry should be attempted based on the thrown exception.
     *
     * @param attemptCount the number of attempts made so far
     * @param exception the exception encountered
     * @return true if a retry should be attempted, false otherwise
     */
    boolean shouldRetry(int attemptCount, Throwable exception);

    static RetryPolicy withExceptionClasses(List<Class<? extends Throwable>> exceptionClasses) {
        return (attemptCount, exception) -> {
            // Check if exception is of a retryable type
            return exceptionClasses.stream().anyMatch(e -> e.isInstance(exception));
        };
    }

    static RetryPolicy withExceptionPredicates(List<Predicate<Throwable>> predicates) {
        return (attemptCount, exception) -> {
            // Check if any predicate evaluates to true for the exception
            return predicates.stream().anyMatch(p -> p.test(exception));
        };
    }

    static RetryPolicy combined(RetryPolicy... policies) {
        return (attemptCount, exception) ->
                Arrays.stream(policies).anyMatch(p -> p != null && p.shouldRetry(attemptCount, exception));
    }
}


