package org.konnect.auth.oauth;


import java.util.HashMap;
import java.util.Map;

public class TokenStorageLocal implements TokenStorage {

    private Map<String, ClientToken> localMap;

    public TokenStorageLocal() {
        this.localMap = new HashMap<>();
    }

    @Override
    public ClientToken retrieveToken(String tenantId, String resourceId) {
        String key = buildKey(tenantId, resourceId);
        if (localMap.containsKey(key)) {
            ClientToken token = localMap.get(key);
            return token;
        }
        return null;
    }

    @Override
    public void storeToken(String tenantId, String resourceId, ClientToken token) {
        String key = buildKey(tenantId, resourceId);
        localMap.put(key, token);
    }

    @Override
    public void removeToken(String tenantId, String resourceId) {
        String key = buildKey(tenantId, resourceId);
        localMap.remove(key);
    }

    private String buildKey(String tenantId, String resourceId) {
        return String.format("%s_%s", tenantId, resourceId);
    }


}
