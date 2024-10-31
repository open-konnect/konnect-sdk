package org.konnect.rest;

import lombok.Getter;

import java.time.Duration;

@Getter
public class RequestConfig {

    private Duration timeout;
    private boolean ignoreResponseBody;
}
