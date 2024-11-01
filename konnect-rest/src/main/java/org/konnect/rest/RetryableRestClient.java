package org.konnect.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.konnect.rest.retry.*;
import org.konnect.utils.json.JsonUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
public class RetryableRestClient implements RestClient {

    private static final Predicate<Throwable> IS_RETRYABLE_EXCEPTION =
            e -> e instanceof RestApiException && ((RestApiException) e).isRetryable();
    private RestClient baseClient;

    private Retryer retryer;

    public RetryableRestClient() {
        this(null, null);
    }

    public RetryableRestClient(Duration connectionTimeout, Retryer customRetryer) {
        this.baseClient = new BaseRestClient(connectionTimeout);
        this.retryer = Objects.requireNonNullElseGet(customRetryer, () -> new RetryerBuilder()
                .retryIfException(IOException.class, SocketTimeoutException.class)
                .retryIfExceptionPredicates(IS_RETRYABLE_EXCEPTION)
                .retryAfterExponentialBackoffDelay(100, 2.0, 5000, true) // Exponential backoff with jitter
                .stopAfterMaxRetries(4) // Stop after 4 retries
                .stopAfterMaxElapsedTime(10000) // Or if total elapsed time exceeds 10 seconds
                .build());

    }

    public RetryableRestClient(Retryer customRetryer) {
        this.baseClient = new BaseRestClient();
        this.retryer = customRetryer;
    }

    @Override
    public <T> RestResponse<T> call(RestRequest request, TypeReference<T> responseType) throws RestApiException {
        final String requestLog = request.loggableRequest();
        try {
            return this.retryer.execute(() -> baseClient.call(request, responseType));
        } catch (Exception e) {
            if (e instanceof RestApiException) {
                log.error(String.format("All retry attempts exhausted while calling %s", requestLog), e);
            } else {
                log.error(String.format("Non retryable error while calling %s", requestLog), e);
            }
            if (e.getCause() instanceof RestApiException) {
                throw (RestApiException) e.getCause(); // throw original exception
            }
            throw new RestApiException(500, e.getMessage(), e);
        }
    }
}
