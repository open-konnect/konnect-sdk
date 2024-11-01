package org.konnect.crm.sfdc.models.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DescribeSObject {

    private String name;
    private String label;
    private Boolean custom;
    private String keyPrefix;
    private String labelPlural;
    private Boolean layoutable;
    private Boolean activateable;
    private Boolean updateable;
    private Map<String, String> urls;
    private Boolean createable;
    private Boolean deletable;
    private Boolean feedEnabled;
    private Boolean queryable;
    private Boolean replicateable;
    private Boolean retrieveable;
    private Boolean undeletable;
    private Boolean triggerable;
    private Boolean mergeable;
    private Boolean deprecatedAndHidden;
    private Boolean customSetting;
    private Boolean searchable;

    private List<Field> fields;
    private List<ChildEntity> childRelationships;
}
