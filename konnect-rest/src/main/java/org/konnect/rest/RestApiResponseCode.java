package org.konnect.rest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RestApiResponseCode {

    SUCCESS(false),
    REDIRECT(false),
    INVALID_REQUEST(false),
    UNAUTHORIZED(false),
    NOT_FOUND(false),
    TIMEOUT(true),
    CONFLICT(false),
    TOO_MANY_REQUESTS(false),
    INTERNAL_SERVER_ERROR(true);

    @Getter
    private final boolean isRetryable;

    public static RestApiResponseCode getErrorCode(int statusCode) {
        if (statusCode <= 299) return SUCCESS;
        if (statusCode <= 399) return REDIRECT;
        // Specific 4xx codes
        if (statusCode == 401 || statusCode == 403) return UNAUTHORIZED;
        if (statusCode == 404 || statusCode == 405) return NOT_FOUND;
        if (statusCode == 408 || statusCode == 504) return TIMEOUT;
        if (statusCode == 409) return CONFLICT;
        if (statusCode == 429) return TOO_MANY_REQUESTS;
        // Default 4xx code
        if (statusCode <= 499) return INVALID_REQUEST;

        // Default 5xx code
        return INTERNAL_SERVER_ERROR;
    }
}
