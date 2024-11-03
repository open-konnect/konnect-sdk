package org.konnect.auth.oauth.resource;

import org.konnect.utils.string.StringUtils;

public enum ResourceType {
    Salesforce,
    Salesforce_sandbox,
    LinkedIn,
    Slack;

    public static ResourceType findResourceType(String resourceId) {
        for (ResourceType type : ResourceType.values()) {
            if (StringUtils.equalsIgnoreCase(type.name(), resourceId)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Resource name unknown " + resourceId);
    }

    public static OAuthResource findResource(String resourceId) {
        ResourceType type = findResourceType(resourceId);
        return findResource(type);
    }

    public static OAuthResource findResource(ResourceType type) {
        switch (type) {
            case Salesforce:
                return new SalesforceOAuth(SalesforceOAuth.InstanceType.PROD);
            case Salesforce_sandbox:
                return new SalesforceOAuth(SalesforceOAuth.InstanceType.SANDBOX);
        }
        return null;
    }
}
