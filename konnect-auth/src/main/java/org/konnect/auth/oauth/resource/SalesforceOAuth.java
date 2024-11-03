package org.konnect.auth.oauth.resource;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.konnect.auth.oauth.ClientToken;
import org.konnect.auth.oauth.PkceHelper;

public class SalesforceOAuth implements OAuthResource {

    private static final String PRODUCTION_HOST = "https://login.salesforce.com";
    private static final String SANDBOX_HOST = "https://test.salesforce.com";
    private static final String TOKEN_API = "/services/oauth2/token";
    private static final String AUTH_API = "/services/oauth2/authorize";

    @RequiredArgsConstructor
    public enum InstanceType {
        PROD(PRODUCTION_HOST),
        SANDBOX(SANDBOX_HOST);

        @Getter private final String host;
    }

    private final String authUrl;
    private final String tokenUrl;

    public SalesforceOAuth() {
        this(InstanceType.PROD);
    }

    public SalesforceOAuth(InstanceType instanceType) {
        this.authUrl = instanceType.getHost() + AUTH_API;
        this.tokenUrl = instanceType.getHost() + TOKEN_API;
    }

    @Override
    public String resourceId() {
        return "salesforce";
    }

    @Override
    public String authorizationUrl() {
        return authUrl;
    }

    @Override
    public String tokenExchangeUrl() {
        return tokenUrl;
    }

    @Override
    public void decorateClientToken(ClientToken clientToken, JsonNode rawResp) {
        String instanceUrl = rawResp.get("instance_url").asText();
        clientToken.setResourceUrl(instanceUrl);
    }

    @Override
    public PkceHelper.Algorithms codeChallengeAlgorithm() {
        return PkceHelper.Algorithms.SHA_256;
    }
}
