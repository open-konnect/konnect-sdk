package org.konnect.auth.oauth;

import lombok.Builder;
import org.konnect.auth.oauth.app.OAuthAppConfig;
import org.konnect.auth.oauth.app.OAuthAppConfigProvider;
import org.konnect.auth.oauth.app.OAuthAppConfigProviderLocal;
import org.konnect.auth.oauth.resource.OAuthResource;
import org.konnect.auth.oauth.resource.ResourceType;
import org.konnect.auth.oauth.session.LocalSessionManager;
import org.konnect.auth.oauth.session.OAuthSessionData;
import org.konnect.auth.oauth.session.SessionManager;
import org.konnect.rest.*;
import org.konnect.utils.json.JsonUtils;

public class OAuthService {

    private OAuthAppConfigProvider appConfigProvider;
    private SessionManager<OAuthSessionData> sessionManager;
    private BaseOAuthResource baseOAuthResource;
    private TokenStorage tokenStorage;
    private RestClient restClient;

    public OAuthService() {
        this.appConfigProvider = new OAuthAppConfigProviderLocal();
        this.sessionManager = new LocalSessionManager<OAuthSessionData>();
        this.baseOAuthResource = new BaseOAuthResource();
        this.restClient = new RetryableRestClient();
        this.tokenStorage = new TokenStorageLocal();
    }

    public String authorizeUrl(ResourceType resourceType, String tenantId) {

        OAuthResource resource = ResourceType.findResource(resourceType);
        OAuthAppConfig authConfig = appConfigProvider.fetchConfig(resource.resourceId());

        String state = resource.resourceId() + "_" + tenantId;

        OAuthSessionData sessionData = new OAuthSessionData();
        sessionData.setTenantId(tenantId);
        sessionData.setResourceId(resource.resourceId());
        sessionData.setState(state);

        String authUrl = baseOAuthResource.authorizeUrl(resource, authConfig, sessionData);

        sessionManager.save(state, sessionData);

        return authUrl;
    }

    public void exchangeTokenRequest(ResourceType resourceType, String state, String authCode) {
        OAuthSessionData sessionData = sessionManager.fetch(state).get();
        sessionData.setAuthCode(authCode);

        OAuthResource resource = ResourceType.findResource(resourceType);
        OAuthAppConfig authConfig = appConfigProvider.fetchConfig(resource.resourceId());

        RestRequest request = baseOAuthResource.exchangeTokenRequest(resource, authConfig, sessionData);
        try {
            RestResponse<String> response = restClient.call(request, String.class);
            System.out.println("Token exchanged raw " + JsonUtils.convertToJsonString(response));
            ClientToken token = baseOAuthResource.parseToken(resource, response);
            System.out.println("Token exchanged " + JsonUtils.convertToJsonString(token));
            tokenStorage.storeToken(sessionData.getTenantId(), sessionData.getResourceId(), token);
        } catch (RestApiException e) {
            throw new RuntimeException(e);
        }
    }

    public ClientToken fetchToken(ResourceType resourceType, String tenantId) {
        OAuthResource resource = ResourceType.findResource(resourceType);
        OAuthAppConfig authConfig = appConfigProvider.fetchConfig(resource.resourceId());
        ClientToken currToken = tokenStorage.retrieveToken(tenantId, resource.resourceId());
        if (!currToken.hasExpired()) {
            return currToken;
        }

        RestRequest tokenRenewRequest = baseOAuthResource.renewTokenRequest(resource, authConfig, currToken);
        try {
            RestResponse<String> response = restClient.call(tokenRenewRequest, String.class);
            ClientToken renewedToken = baseOAuthResource.parseToken(resource, response);
            tokenStorage.storeToken(tenantId, resource.resourceId(), renewedToken);
            return renewedToken;
        } catch (RestApiException e) {
            throw new RuntimeException(e);
        }
    }


}
