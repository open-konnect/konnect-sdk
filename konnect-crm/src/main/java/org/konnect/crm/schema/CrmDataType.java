package org.konnect.crm.schema;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.konnect.crm.schema.data.*;

/**
 * Supported CRM data Types
 */
@RequiredArgsConstructor
public enum CrmDataType {

    ACCOUNT(Account.class),
    ANNUAL_RECURRING_REVENUE(AnnualRecurringRevenue.class),
    CAMPAIGN(Campaign.class),
    CONTACT(Contact.class),
    CURRENCY_TYPE(CurrencyType.class),
    DATED_CONVERSION_RATE(DatedConversionRate.class),
    FORECASTING_QUOTA(ForecastingQuota.class),
    LEAD(Lead.class),
    OPPORTUNITY(Opportunity.class),
    OPPORTUNITY_CONTACT_ROLE(OpportunityContactRole.class),
    OPPORTUNITY_HISTORY(OpportunityHistory.class),
    OPPORTUNITY_FIELD_HISTORY(OpportunityFieldHistory.class),
    OPPORTUNITY_SPLIT(OpportunitySplit.class),
    PERIOD(Period.class),
    TASK(Task.class),
    USER(User.class),
    UNRECOGNIZED(UnrecognizedData.class);

    @Getter
    private final Class<? extends CrmBaseData> dataClass;

    public static CrmDataType fromString(String dataType) {
        for (CrmDataType crmDataType : CrmDataType.values()) {
            if (crmDataType.name().equalsIgnoreCase(dataType)) {
                return crmDataType;
            }
        }
        return null;
    }

}
