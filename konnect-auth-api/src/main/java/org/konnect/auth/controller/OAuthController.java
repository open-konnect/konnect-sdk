package org.konnect.auth.controller;

import org.konnect.auth.oauth.ClientToken;
import org.konnect.auth.oauth.OAuthService;
import org.konnect.auth.oauth.resource.ResourceType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private OAuthService oAuthService;

    public OAuthController() {
        this.oAuthService = new OAuthService();
    }

    @GetMapping("/{resource}/{tenantId}/token")
    public ClientToken fetchToken(
            @PathVariable("resource") String resource,
            @PathVariable("tenantId") String tenantId) {
        ResourceType resourceType = ResourceType.findResourceType(resource);
        return oAuthService.fetchToken(resourceType, tenantId);
    }

    @GetMapping("/{resource}/{tenantId}/authorize")
    public String authorize(
            @PathVariable("resource") String resource,
            @PathVariable("tenantId") String tenantId) {
        ResourceType resourceType = ResourceType.findResourceType(resource);
        return oAuthService.authorizeUrl(resourceType, tenantId);
    }


    @GetMapping("/{resource}/callback")
    public void handleCallback(
            @PathVariable("resource") String resource,
            @RequestParam("state") String state,
            @RequestParam("code") String authorizationCode) throws IOException {
        // Exchange authorization code for tokens

        ResourceType resourceType = ResourceType.findResourceType(resource);
        oAuthService.exchangeTokenRequest(resourceType, state, authorizationCode);
    }
}
