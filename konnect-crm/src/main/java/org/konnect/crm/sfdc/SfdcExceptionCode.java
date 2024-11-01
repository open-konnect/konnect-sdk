package org.konnect.crm.sfdc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.konnect.rest.RestApiException;
import org.konnect.utils.string.StringUtils;

/**
 * See https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/errorcodes.htm
 */
@Getter
@RequiredArgsConstructor
public enum SfdcExceptionCode {

    REQUEST_INVALID(400, "The request couldn't be understood, usually because the JSON or XML body contains an error"),

    SOQL_ERROR(400, "Invalid SOQL query"),
    SOQL_INVALID_FIELD(400, "Invalid field in SOQL query"),

    EXPIRED_CREDENTIALS(401, "The session ID or OAuth token used has expired or is invalid."),

    SFDC_NOT_CONNECTED(401, "Salesforce instance is not connected yet"),

    INSUFFICIENT_PRIVILEGES(403, "The request has been refused. Verify that the logged-in user has appropriate permissions."),

    NOT_FOUND(404, "The requested resource couldn't be found. Check the URI for errors, and verify that there are no sharing issues."),

    CONFLICT(409, "The requested action can't be performed because the data is in a state of conflict."),

    SFDC_LIMIT_BREACHED(429, "You've exceeded API request limits in your org"),

    SFDC_INTERNAL_SERVER_ERROR(500, "Internal server error from Salesforce"),

    INTERNAL_SERVER_ERROR(500, "Internal server error from Mindtickle"),

    REQ_TIMED_OUT(503, "Request to the underlying gateway timed out.");


    private final int code;
    private final String message;

    public static SfdcExceptionCode getSfdcExceptionCode(String sfdcErrorCode) {
        if (StringUtils.isNotBlank(sfdcErrorCode)) {
            switch (sfdcErrorCode) {
                case "INVALID_FIELD":
                    return SOQL_INVALID_FIELD;
            }
        }
        return null;
    }

    public static SfdcExceptionCode getSfdcExceptionCode(RestApiException e, String sfdcErrorCode) {
        SfdcExceptionCode exceptionCode = getSfdcExceptionCode(sfdcErrorCode);
        if (exceptionCode != null) {
            return exceptionCode;
        }

        // Fallback to http status code
        switch (e.getStatusCode()) {
            case 400:
                return REQUEST_INVALID;
            case 401:
                return EXPIRED_CREDENTIALS;
            case 403:
                // Limit exceeded is returned as 403 from Salesforce
                if (e.getMessage().contains("REQUEST_LIMIT_EXCEEDED")) {
                    return SFDC_LIMIT_BREACHED;
                } else {
                    return INSUFFICIENT_PRIVILEGES;
                }
            case 404:
                return NOT_FOUND;
            case 409:
                return CONFLICT;
            case 504:
                return REQ_TIMED_OUT;
            default:
                if (e.getStatusCode() < 500) {
                    return REQUEST_INVALID;
                }
                return SFDC_INTERNAL_SERVER_ERROR;
        }
    }
}
