package org.konnect.auth.oauth.app;

public interface OAuthAppConfigProvider {

    OAuthAppConfig fetchConfig(final String resourceId);
}
