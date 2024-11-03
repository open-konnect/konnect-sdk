package org.konnect.auth.oauth;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ClientToken {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Instant expiresAt;

    private String resourceUrl;

    public boolean hasExpired() {
        return expiresAt.isBefore(Instant.now());
    }
}
