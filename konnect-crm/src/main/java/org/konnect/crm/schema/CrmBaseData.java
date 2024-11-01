package org.konnect.crm.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.konnect.utils.json.JsonUtils;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class CrmBaseData {

    public abstract CrmDataType getDataType();

    private String id;
    private String systemModstamp;
    private String lastUpdatedAt;
    private String createdAt;

    @Builder.Default
    private Map<String, String> unmappedValues = new HashMap<>();

    @Override
    public String toString() {
        return JsonUtils.convertToJsonString(this);
    }
}
