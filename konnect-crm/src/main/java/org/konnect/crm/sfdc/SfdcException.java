package org.konnect.crm.sfdc;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import org.konnect.rest.RestApiException;
import org.konnect.utils.json.JsonUtils;
import org.konnect.utils.string.StringUtils;


public class SfdcException extends Exception {


    @Getter private final SfdcExceptionCode code;
    @Getter private String sfdcErrorMessage;

    public SfdcException(RestApiException e) {
        super(e);
        String sfdcErrorCode = null;
        String sfdcErrorMessage = null;
        if (StringUtils.isNotBlank(e.getResponseBody())) {
            try {
                JsonNode respJson = JsonUtils.instance().readValue(e.getResponseBody(), JsonNode.class);
                System.out.println("SfdcException.SfdcException: respJson = " + respJson);
                if (respJson.isArray()) {
                    sfdcErrorCode = respJson.get(0).get("errorCode").asText();
                    sfdcErrorMessage = respJson.get(0).get("message").asText();
                }
            } catch (Exception ex) {
                // ignore
            }
        }
        this.sfdcErrorMessage = sfdcErrorMessage;
        this.code = SfdcExceptionCode.getSfdcExceptionCode(e, sfdcErrorCode);
    }

    public SfdcException(SfdcExceptionCode code, String message) {
        super(message);
        this.code = code;
    }

    public SfdcException(SfdcExceptionCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }



}
