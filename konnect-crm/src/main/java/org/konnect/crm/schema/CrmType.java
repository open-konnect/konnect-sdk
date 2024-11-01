package org.konnect.crm.schema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CrmType {
    SALESFORCE("crmsync", "sf", "OAUTH2"),
    HUBSPOT("crmsync", "hubspot", "OAUTH2");

    private final String integrationType;
    private final String integrationSourceName;
    private final String authType;
}
