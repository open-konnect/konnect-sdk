package org.konnect.crm.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilterGroup {

    public enum FilterCondition {
        AND, OR
    }

    private FilterCondition condition;

    // List of filters will be applied in AND fashion
    private List<Filter> filters;
}
