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
public class User extends CrmBaseData {

    private String email;
    private String name;
    private String state;
    private String managerId;
    private Boolean isActive;
    private String title;
    private String username;
    private Boolean forecastEnabled;

    @Override
    public CrmDataType getDataType() {
        return CrmDataType.USER;
    }
}
