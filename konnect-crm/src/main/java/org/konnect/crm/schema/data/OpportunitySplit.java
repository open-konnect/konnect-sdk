package org.konnect.crm.schema.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import org.konnect.crm.schema.CrmBaseData;
import org.konnect.crm.schema.CrmDataType;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpportunitySplit extends CrmBaseData {

    private String split;
    private Double splitAmount;
    private String splitOwnerId;
    private String opportunityId;

    @Override
    public CrmDataType getDataType() {
        return CrmDataType.OPPORTUNITY_SPLIT;
    }
}
