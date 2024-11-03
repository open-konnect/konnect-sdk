package org.konnect.auth.oauth.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthSessionData {
    private String tenantId;
    private String resourceId;
    private String codeVerifier;
    private String state;
    private String authCode;
}
