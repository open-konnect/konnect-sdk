package org.konnect.crm.sfdc;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.konnect.auth.AuthConfig;
import org.konnect.rest.HttpAccept;
import org.konnect.rest.HttpMethod;
import org.konnect.rest.RestRequest;
import org.konnect.rest.RestResponse;
import org.konnect.utils.json.JsonUtils;
import org.konnect.utils.string.StringUtils;


import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
public class SfdcAuthHelper {

    private static final String TOKEN_RENEW_API = "/services/oauth2/token";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";

    private final AuthConfig authConfig;

    public boolean isTokenRefreshAllowed() {
        return StringUtils.isNoneBlank(authConfig.getRefreshToken(),
                authConfig.getClientId(),
                authConfig.getClientSecret(),
                authConfig.getLoginEndPoint());
    }

    public RestRequest buildTokenRenewRequest() {
        final Map<String, String> params =
                Map.of(
                        "grant_type", REFRESH_TOKEN_KEY,
                        "client_id", this.authConfig.getClientId(),
                        "client_secret", this.authConfig.getClientSecret(),
                        REFRESH_TOKEN_GRANT_TYPE, this.authConfig.getRefreshToken());

        return new RestRequest.Builder()
                .withHttpMethod(HttpMethod.POST)
                .withUri(authConfig.getLoginEndPoint())
                .withApi(TOKEN_RENEW_API)
                .withQueryParam(params)
                .withAccept(HttpAccept.JSON)
                .build();
    }

    public void updateToken(RestResponse<Map<String, String>> response) {
        final String accessToken = response.getResponseBody().get(ACCESS_TOKEN_KEY);
        this.authConfig.setAccessToken(accessToken);
    }

    @Override
    public String toString() {
        return JsonUtils.convertToJsonString(this);
    }
}
