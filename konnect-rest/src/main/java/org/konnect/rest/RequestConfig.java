package org.konnect.rest;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder
public class RequestConfig {

    private String requestId;
    private Duration timeout;
    private boolean ignoreResponseBody;
}
