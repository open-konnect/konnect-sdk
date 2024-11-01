package org.konnect.crm.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Setter
@Jacksonized
@Builder
public class DataFetchRequest {
    private String dataType;
    private List<String> selectedAttributes;
    private AssociationCriteria associationCriteria;
    private FilterCriteria filterCriteria;

}
