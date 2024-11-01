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

@Getter
@Setter
@Jacksonized
@SuperBuilder
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnnualRecurringRevenue extends CrmBaseData {

    private String name;
    private String opportunityAccountId;
    private String opportunityName;
    private String opportunityOwnerId;
    private String opportunityType;
    private String opportunityStageName;
    private Long opportunityClosedAt;

    @Override
    public CrmDataType getDataType() {
        return CrmDataType.ANNUAL_RECURRING_REVENUE;
    }
}
