package org.konnect.auth.oauth;

import org.konnect.auth.oauth.resource.ResourceType;
import org.konnect.utils.json.JsonUtils;

public class Test {

    public static void main(String[] args) {
        //OAuthService authService = new OAuthService();
        //String url = authService.authorizeUrl(ResourceType.Salesforce, "tenant-1");
        //System.out.println("Go to => " + url);

        ClientToken token = new ClientToken();
        token.setAccessToken("test");
        token.setTokenType("abc");

        System.out.println(JsonUtils.convertToJsonString(token));

    }
}
