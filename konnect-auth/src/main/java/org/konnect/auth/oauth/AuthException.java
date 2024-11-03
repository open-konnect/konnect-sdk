package org.konnect.auth.oauth;

public class AuthException extends Exception {
    private final AuthErrorCode errorCode;
    private final String tenantId;
    private final String resourceId;

    public AuthException(AuthErrorCode errorCode, String tenantId, String resourceId, String message) {
        super(message);
        this.errorCode = errorCode;
        this.tenantId = tenantId;
        this.resourceId = resourceId;
    }

    public AuthErrorCode getErrorCode() {
        return errorCode;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getResourceId() {
        return resourceId;
    }
}


