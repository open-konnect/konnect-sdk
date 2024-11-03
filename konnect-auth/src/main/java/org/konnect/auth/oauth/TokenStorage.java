package org.konnect.auth.oauth;


/**
 * This interface will be implemented by the client to handle token storage
 * and retrieval for multi-tenant and multi-resource environments.
 */
public interface TokenStorage {
    ClientToken retrieveToken(String tenantId, String resourceId);
    void storeToken(String tenantId, String resourceId, ClientToken token);
    void removeToken(String tenantId, String resourceId);
}
