package org.konnect.crm.schema.data;

import org.konnect.crm.schema.CrmBaseData;
import org.konnect.crm.schema.CrmDataType;

public class OpportunityHistory extends CrmBaseData {

    private String opportunityId;
    private String createdById;
    private String probability;
    private String amount;
    private String stage;
    private Boolean deleted;
    private String expectedRevenue;
    private String forecastCategory;
    private String currencyIsoCode;
    private Long closedAt;

    @Override
    public CrmDataType getDataType() {
        return CrmDataType.OPPORTUNITY_HISTORY;
    }
}
