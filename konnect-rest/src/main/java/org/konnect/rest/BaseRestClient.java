package org.konnect.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.konnect.utils.json.JsonUtils;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;


@Slf4j
public class BaseRestClient implements RestClient {
private static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);

    private HttpClient client;

    public BaseRestClient() {
        this(DEFAULT_CONNECT_TIMEOUT);
    }

    public BaseRestClient(Duration connectionTimeout) {
        Duration customTimeout = Objects.requireNonNullElse(connectionTimeout, DEFAULT_CONNECT_TIMEOUT);
        this.client = HttpClient.newBuilder().connectTimeout(customTimeout).build();
    }

    @Override
    public <T> RestResponse<T> call(RestRequest request, TypeReference<T> responseType) throws RestApiException {
        try {
            log.debug("Attempting Http request {}", request.loggableRequest());
            HttpResponse<String> response = client.send(request.buildHttpRequest(), HttpResponse.BodyHandlers.ofString());
            handleFailure(request, response);
            RestResponse.RestResponseBuilder<T> responseBuilder = RestResponse.builder();
            responseBuilder.responseCode(response.statusCode());
            responseBuilder.isSuccess(true);
            if (response.headers() != null) responseBuilder.responseHeaders(response.headers().map());
            if (request.getRequestConfig() != null && request.getRequestConfig().isIgnoreResponseBody()) {
                responseBuilder.responseBody(null);
            } else {
                T responseBody;
                // Check if the response body is empty and return null if so
                if (response.body() != null && !response.body().isEmpty()) {
                    // Special handling for String type
                    if (responseType.getType().equals(String.class)) {
                        responseBody = (T) response.body();
                    } else {
                        responseBody = JsonUtils.instance().readValue(response.body(), responseType);
                    }
                    responseBuilder.responseBody(responseBody);
                }
            }
            log.debug("Http request successful with status code {} : {}", response.statusCode(), request.loggableRequest());
            return responseBuilder.build();
        } catch (Exception e) {
            String message = String.format("Http request failed with exception. Message %s | Request %s", e.getMessage(), request.loggableRequest());
            log.error(message, e);
            throw new RestApiException(500, e.getMessage(), e);
        }
    }



    private void handleFailure(RestRequest request, HttpResponse<String> response) throws RestApiException {
        if (response == null) {
            log.error(String.format("Http request failed with null response %s", request.loggableRequest()));
            throw new RestApiException(500, "Http request failed with null response");
        }
        if (response.statusCode() > 399 ) {
            String errorResponse = response.body();
            log.error(String.format("Http request failed with status code: %s | Request: %s | Response: %s",
                    response.statusCode(), request.loggableRequest(), errorResponse));
            throw new RestApiException(response.statusCode(), errorResponse);
        }
    }
}
