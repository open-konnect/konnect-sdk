package org.konnect.auth.oauth.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.konnect.auth.oauth.ClientToken;
import org.konnect.auth.oauth.PkceHelper;

public interface OAuthResource {

    String resourceId();

    String authorizationUrl();

    String tokenExchangeUrl();

    void decorateClientToken(ClientToken clientToken, JsonNode rawResp);

    PkceHelper.Algorithms codeChallengeAlgorithm();
}
