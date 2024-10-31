package org.konnect.rest;

import lombok.Getter;

@Getter
public class RestApiException extends Exception {

    private final int statusCode;
    private final RestApiResponseCode responseCode;
    private boolean isRetryable;
    private String responseBody;


    public RestApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.responseCode = RestApiResponseCode.getErrorCode(statusCode);
        this.isRetryable = responseCode.isRetryable();
    }

    public RestApiException(int statusCode, String message, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseCode = RestApiResponseCode.getErrorCode(statusCode);
        this.isRetryable = responseCode.isRetryable();
        this.responseBody = responseBody;
    }

    public RestApiException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseCode = RestApiResponseCode.getErrorCode(statusCode);
        this.isRetryable = responseCode.isRetryable();
    }
}
