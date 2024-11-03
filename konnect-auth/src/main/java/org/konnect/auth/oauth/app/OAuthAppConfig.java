package org.konnect.auth.oauth.app;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuthAppConfig {

    private final String resourceId;
    private final String clientId;
    private final String clientSecret;
    private final String scopes;
    private final String redirectUrl;

}
