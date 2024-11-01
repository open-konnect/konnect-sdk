package org.konnect.crm.sfdc.models.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChildEntity {
    private String field;
    private String childSObject;
    private String relationshipName;
    private Boolean deprecatedAndHidden;
    private Boolean cascadeDelete;
}
