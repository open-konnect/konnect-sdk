package org.konnect.auth.oauth;

public interface CallbackUrlBuilder {
    String buildCallbackUrl(String resourceId);

    static CallbackUrlBuilder localHostCallbacks() {
        return ((resourceId) -> String.format("http://localhost:8080/oauth/%s/callback", resourceId));
    }
}
