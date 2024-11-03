package org.konnect.auth;


import lombok.Getter;
import lombok.Setter;
import org.konnect.utils.string.StringUtils;

@Getter
@Setter
public class AuthConfig {
    private String tenantId;
    private AuthType authType;
    private String accessToken;
    private String refreshToken;
    private String instanceUrl;
    private String loginEndPoint;
    private String clientId;
    private String clientSecret;

    public boolean isValid() {
        boolean requiredFieldsMissing = false;
        if (authType == AuthType.OAUTH2) {
            requiredFieldsMissing = StringUtils.isAnyBlank(accessToken, refreshToken, instanceUrl, loginEndPoint, clientId, clientSecret);
        }
        return !requiredFieldsMissing;
    }
}
