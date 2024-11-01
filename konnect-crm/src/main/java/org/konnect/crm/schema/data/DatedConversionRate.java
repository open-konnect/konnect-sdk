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
public class DatedConversionRate extends CrmBaseData {
    private String isoCode;
    private Double conversionRate;
    private String createdById;
    private Long startDate;
    private Long nextStartDate;
    private String lastModifiedById;

    @Override
    public CrmDataType getDataType() {
        return CrmDataType.DATED_CONVERSION_RATE;
    }
}
