package org.konnect.crm.sfdc;

import lombok.Getter;
import lombok.Setter;
import org.konnect.utils.string.StringUtils;

@Getter
public class SfdcAuthConfig {
    private String tenantId;
    @Setter private String accessToken;
    private String refreshToken;
    private String instanceUrl;
    private String loginEndPoint;
    private String clientId;
    private String clientSecret;

    public boolean isValid() {
        return !StringUtils.isAnyBlank(accessToken, refreshToken, instanceUrl, loginEndPoint, clientId, clientSecret);
    }
}
