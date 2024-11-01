package org.konnect.crm.sfdc.models.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PickListEntry {
    private String value;
    private Boolean active;
    private String label;
    private Boolean defaultValue;
    private byte[] validFor;
}
