package org.konnect.auth.oauth;

import org.konnect.auth.oauth.resource.ResourceType;

public class Test {

    public static void main(String[] args) {
        OAuthService authService = new OAuthService();
        String url = authService.authorizeUrl(ResourceType.Salesforce, "tenant-1");
        System.out.println("Go to => " + url);
    }
}
