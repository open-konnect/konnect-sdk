package org.konnect.rest.retry;

import lombok.Getter;

@Getter
public class RetryException extends Exception {

    private int attempts;

    public RetryException(int attempts, String retryMessage, Exception cause) {
        super(retryMessage + " caused by " + cause.getMessage(), cause);
        this.attempts = attempts;
    }
}
