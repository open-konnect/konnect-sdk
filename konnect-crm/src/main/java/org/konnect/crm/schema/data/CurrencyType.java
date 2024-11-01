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
public class CurrencyType extends CrmBaseData {
    private String isoCode;
    private Double conversionRate;
    private Boolean isActive;
    private Boolean isCorporate;
    private Integer decimalPlaces;
    private String createdById;
    private String lastModifiedById;

    @Override
    public CrmDataType getDataType() {
        return CrmDataType.CURRENCY_TYPE;
    }
}
