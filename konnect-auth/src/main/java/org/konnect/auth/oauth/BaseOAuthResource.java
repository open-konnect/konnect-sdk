package org.konnect.auth.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.konnect.auth.oauth.app.OAuthAppConfig;
import org.konnect.auth.oauth.resource.OAuthResource;
import org.konnect.auth.oauth.session.OAuthSessionData;
import org.konnect.rest.HttpAccept;
import org.konnect.rest.HttpMethod;
import org.konnect.rest.RestRequest;
import org.konnect.rest.RestResponse;
import org.konnect.utils.json.JsonUtils;
import org.konnect.utils.string.StringUtils;

import java.time.Instant;
import java.util.Map;

public class BaseOAuthResource {
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String GRANT_TYPE_AUTH_CODE = "authorization_code";

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String REDIRECT_URI_KEY = "redirect_uri";


    public String authorizeUrl(OAuthResource resource, OAuthAppConfig authConfig, OAuthSessionData sessionData) {

        RestRequest.Builder builder = new RestRequest.Builder()
                .withUri(resource.authorizationUrl())
                .withQueryParam("response_type", "code")
                .withQueryParam(CLIENT_ID_KEY, authConfig.getClientId())
                .withQueryParam("scope", authConfig.getScopes())
                .withQueryParam(REDIRECT_URI_KEY, authConfig.getRedirectUrl())
                .withQueryParam("state", sessionData.getState());

        if (PkceHelper.Algorithms.NONE != resource.codeChallengeAlgorithm()) {
            String codeVerifier = PkceHelper.generateCodeVerifier();
            String codeChallenge = PkceHelper.generateCodeChallenge(codeVerifier, resource.codeChallengeAlgorithm());
            builder.withQueryParam("code_challenge", codeChallenge);
            builder.withQueryParam("code_challenge_method", "S256");
            System.out.println("codeVerifier=> " + codeVerifier);
            sessionData.setCodeVerifier(codeVerifier);
        }
        return builder.build().buildUri().toString();
    }

    public RestRequest exchangeTokenRequest(OAuthResource resource, OAuthAppConfig authConfig,
                                            OAuthSessionData sessionData) {
        String redirectUrl = authConfig.getRedirectUrl();
        RestRequest.Builder builder =
                new RestRequest.Builder()
                        .withUri(resource.tokenExchangeUrl())
                        .withHttpMethod(HttpMethod.POST)
                        .withAccept(HttpAccept.JSON)
                        .withQueryParam(GRANT_TYPE_KEY, GRANT_TYPE_AUTH_CODE)
                        .withQueryParam("code", sessionData.getAuthCode())
                        .withQueryParam(CLIENT_ID_KEY, authConfig.getClientId())
                        .withQueryParam(CLIENT_SECRET_KEY, authConfig.getClientSecret())
                        .withQueryParam(REDIRECT_URI_KEY, redirectUrl);
        if (StringUtils.isNotBlank(sessionData.getCodeVerifier())) {
            builder.withQueryParam("code_verifier", sessionData.getCodeVerifier());
        }

        return builder.build();
    }

    public ClientToken parseToken(OAuthResource resource, RestResponse<String> tokenExchangeResp) {
        JsonNode respJson = JsonUtils.instance().convertValue(tokenExchangeResp.getResponseBody(), JsonNode.class);
        final String accessToken = respJson.get(ACCESS_TOKEN_KEY).asText();
        final String refreshToken = respJson.get(REFRESH_TOKEN_KEY).asText();
        final String tokenType = respJson.get("token_type").asText();
        final int expiresIn = respJson.get("expires_in").asInt();
        Instant expiresAt = Instant.now().plusSeconds(expiresIn);

        ClientToken clientToken = new ClientToken();
        clientToken.setAccessToken(accessToken);
        clientToken.setRefreshToken(refreshToken);
        clientToken.setTokenType(tokenType);
        clientToken.setExpiresAt(expiresAt);

        resource.decorateClientToken(clientToken, respJson);

        return clientToken;
    }

    public RestRequest renewTokenRequest(OAuthResource resource, OAuthAppConfig authConfig, ClientToken token) {
        final Map<String, String> params =
                Map.of(
                        GRANT_TYPE_KEY, REFRESH_TOKEN_KEY,
                        CLIENT_ID_KEY, authConfig.getClientId(),
                        CLIENT_SECRET_KEY, authConfig.getClientSecret(),
                        REFRESH_TOKEN_KEY, token.getRefreshToken());

        return new RestRequest.Builder()
                .withHttpMethod(HttpMethod.POST)
                .withUri(resource.tokenExchangeUrl())
                .withQueryParam(params)
                .withAccept(HttpAccept.JSON)
                .build();
    }
}
