package org.konnect.auth.oauth.app;

import org.konnect.auth.oauth.resource.ResourceType;

import java.util.Map;

public class OAuthAppConfigProviderLocal implements OAuthAppConfigProvider {

    private Map<ResourceType, OAuthAppConfig> store;

    public OAuthAppConfigProviderLocal() {
        OAuthAppConfig salesforce = OAuthAppConfig.builder()
                .clientId("3MVG9k02hQhyUgQDOYIrNy6mvHO_dLyMAxvxrnkQSEtLjWORDL4n_XAl_2kg0m17v8NY_oTpJL8M_zMuTVYWP")
                .clientSecret("36C3AE8854DE33314702F702869F9B7F735DDED652D7C3B51B457A52420FC4A0")
                .resourceId(ResourceType.Salesforce.name())
                .scopes("api refresh_token")
                .redirectUrl("http://localhost:8080/oauth/salesforce/callback")
                .build();

        this.store = Map.of(ResourceType.Salesforce, salesforce);
    }

    @Override
    public OAuthAppConfig fetchConfig(String resourceId) {
        ResourceType resourceType = ResourceType.findResourceType(resourceId);
        return store.get(resourceType);
    }
}
