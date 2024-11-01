package org.konnect.crm.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Filter {

    public enum Operator {
        EQ, NEQ, GT, LT, GTE, LTE, IN, NOT_IN, LIKE, NOT_LIKE, IS_NULL, IS_NOT_NULL
    }

    private String propertyName;
    private Operator operator;
    private List<String> values;
}
