package org.konnect.crm.sfdc.models.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Field {
    private Integer length;
    private String name;
    private String type;
    private String soapType;
    private String defaultValue;
    private String label;
    private Boolean updateable;
    private Boolean calculated;
    private Boolean unique;
    private Boolean nillable;
    private Boolean caseSensitive;
    private String inlineHelpText;
    private Boolean nameField;
    private Boolean externalId;
    private Boolean idLookup;
    private Boolean filterable;
    // soapType;
    private Boolean createable;
    private Boolean deprecatedAndHidden;
    private List<PickListEntry> picklistValues;
    private Boolean autoNumber;
    private Boolean defaultedOnCreate;
    private Boolean groupable;
    private String relationshipName;
    private List<String> referenceTo;
    // relationshipOrder;
    private Boolean restrictedPicklist;
    private Boolean namePointing;
    private Boolean custom;
    private Boolean htmlFormatted;
    private Boolean dependentPicklist;
    private Boolean writeRequiresMasterRead;
    private Boolean sortable;
}
