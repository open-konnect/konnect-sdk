package org.konnect.crm.schema.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.konnect.crm.schema.CrmBaseData;
import org.konnect.crm.schema.CrmDataType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Opportunity extends CrmBaseData {

    private String opportunityId;
    private String accountId;
    private String accountIndustry;
    private String accountNumberOfEmployees;
    private String accountName;
    private String description;
    private String name;
    private String ownerId;
    private String ownerEmail;
    private String stage;
    private String type;
    private String currencyIsoCode;
    private String amount;
    private String probability;
    private String expectedRevenue;
    private boolean won;
    private String dealValue;
    private List<OpportunityContactRole> contacts;
    private String url;
    private String closedAt;
    private boolean closed;
    private boolean deleted;
    private String leadSource;
    private Boolean isSplit;
    private String createdById;


    @Override
    public CrmDataType getDataType() {
        return CrmDataType.OPPORTUNITY;
    }
}
