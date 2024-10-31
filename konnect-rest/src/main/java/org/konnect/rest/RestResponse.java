package org.konnect.rest;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class RestResponse<T> {

    private final String requestId;
    private final Map<String, List<String>> responseHeaders;
    private final RestRequest request;
    private final T responseBody;
    private final boolean isSuccess;
    private final int responseCode;
    private final String errorMessage;
}

