package org.konnect.crm.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterCriteria {

    // Optional Date range filters on created and updated time
    // Most basic usage will be on time based filters.
    private TimeFilter createdTimeFilter;
    private TimeFilter updatedTimeFilter;

    // Whether to include deleted records in the response
    private boolean includeDeletedRecords;

    // Complex filters
    // FilterGroups will be applied in OR fashion
    private List<FilterGroup> filterGroupList;


}
