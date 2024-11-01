package org.konnect.crm.sfdc.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SfQueryResult<T> {
    private int totalSize;
    private boolean done = false;
    private List<T> records;
    private String nextRecordsUrl;
}
