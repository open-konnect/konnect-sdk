package org.konnect.crm.sfdc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;
import org.konnect.auth.AuthConfig;
import org.konnect.crm.sfdc.models.SfQueryResult;
import org.konnect.crm.sfdc.models.meta.DescribeSObject;
import org.konnect.rest.*;
import org.konnect.utils.json.JsonUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class SfdcClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2); // 2 seconds connection timeout
    private static final Duration API_TIMEOUT = Duration.ofSeconds(30); // 30 seconds api timeout

    private final RestClient restClient;
    private final SfdcAuthHelper authHelper;
    private final String baseUrl;
    private final String servicesAPI;
    private final Map<String, String> headers;

    private static final RequestConfig DEFAULT_REQ_CONFIG = RequestConfig.builder().timeout(API_TIMEOUT).build();

    public SfdcClient(final AuthConfig authConfig, final String apiVersion) {
        this.restClient = new RetryableRestClient(CONNECT_TIMEOUT, null);
        this.authHelper = new SfdcAuthHelper(authConfig);
        this.baseUrl = authHelper.getAuthConfig().getInstanceUrl();
        this.servicesAPI = "/services/data/" + apiVersion;
        this.headers = Map.of(
                "Authorization", "Bearer " + authHelper.getAuthConfig().getAccessToken(),
                "Accept", "application/json");
    }


    public DescribeSObject describeObject(final String objectName) throws SfdcException {
        final String api = servicesAPI + "/sobjects/" + objectName + "/describe";
        final RestRequest request = buildRequest(api, null);
        return callSalesforce(request, new TypeReference<DescribeSObject>() {});
    }

    public <T> SfQueryResult<T> query(String query, Class<T> clazz) throws SfdcException {
        final String api = servicesAPI + "/query/";
        Map<String, String> params = buildQueryParams(query);
        final RestRequest request = buildRequest(api, params);
        return querySalesforce(request, clazz);
    }

    public <T> SfQueryResult<T> queryAll(String query, Class<T> clazz) throws SfdcException {
        final String api = servicesAPI + "/queryAll/";
        Map<String, String> params = buildQueryParams(query);
        final RestRequest request = buildRequest(api, params);
        return querySalesforce(request, clazz);
    }

    public <T> SfQueryResult<T> queryNext(String nextRecordsUrl, Class<T> clazz) throws SfdcException {
        final String api = nextRecordsUrl;
        final RestRequest request = buildRequest(api, null);
        return querySalesforce(request, clazz);
    }

    private Map<String, String> buildQueryParams(final String query) {
        String encodedQuery = query;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Failed to encode query: " + query, e);
        }
        return Map.of("q", encodedQuery);
    }

    private <T> SfQueryResult<T> querySalesforce(RestRequest request, Class<T> clazz) throws SfdcException {
        JsonNode sfResult = callSalesforce(request, new TypeReference<JsonNode>() {});
        SfQueryResult<T> queryResult = new SfQueryResult<T>();
        queryResult.setDone(sfResult.get("done").booleanValue());
        queryResult.setTotalSize(sfResult.get("totalSize").intValue());
        if (sfResult.get("nextRecordsUrl") != null) {
            queryResult.setNextRecordsUrl(sfResult.get("nextRecordsUrl").textValue());
        }
        List<T> records = new ArrayList<T>();
        for (JsonNode elem : sfResult.get("records")) {
            try {
                records.add(JsonUtils.instance().readValue(normalizeCompositeResponse(elem).traverse(), clazz));
            } catch (IOException e) {
                log.error(String.format("Failed to parse query result due to %s. Raw result = %s", e.getMessage(), sfResult.toString()), e);
                throw new SfdcException(SfdcExceptionCode.INTERNAL_SERVER_ERROR, "Failed to parse query result", e);
            }
        }
        queryResult.setRecords(records);
        return queryResult;
    }


    // Calls Salesforce API and attempts to renew token if it has expired
    private <T> T callSalesforce(RestRequest request, TypeReference<T> cls) throws SfdcException {
        try {
            return callSalesforceApi(request, cls);
        } catch (SfdcException e) {
            if (e.getCode() == SfdcExceptionCode.EXPIRED_CREDENTIALS) {
                // lets try to renew token
                renewTokenIfPossible();
                return callSalesforceApi(request, cls);
            }
            log.error("Salesforce API failed with error: {}", e.getMessage());
            throw e;
        }
    }

    // Simply calls Salesforce API and returns the response
    private <T> T callSalesforceApi(RestRequest request, TypeReference<T> cls) throws SfdcException {
        try {
            log.info("Calling Salesforce API for tenant {} | API {} | ", baseUrl, request.getApi());
            final RestResponse<T> response = restClient.call(request, cls);
            return response.getResponseBody();
        } catch (RestApiException e) {
            log.error("Failed to call Salesforce API: {}", e.getMessage(), e);
            throw new SfdcException(e);
        }
    }

    private void renewTokenIfPossible() throws SfdcException {
        try {
            RestRequest tokenRenewReq = authHelper.buildTokenRenewRequest();
            RestResponse<Map<String, String>> tokenRenewResp = restClient.call(tokenRenewReq, new TypeReference<>() {});
            authHelper.updateToken(tokenRenewResp);
        } catch (RestApiException e) {
            log.error("Failed to renew token: {}", e.getMessage());
            throw new SfdcException(e);
        }
    }

    private RestRequest buildRequest(final String apiPath, final Map<String, String> queryParams) {
        return new RestRequest.Builder()
                .withHttpMethod(HttpMethod.GET)
                .withUri(baseUrl)
                .withApi(apiPath)
                .withQueryParam(queryParams)
                .withHeader(headers)
                .withAccept(HttpAccept.JSON)
                .withRequestConfig(DEFAULT_REQ_CONFIG)
                .build();
    }

    private JsonNode normalizeCompositeResponse(JsonNode node){
        Iterator<Map.Entry<String, JsonNode>> elements = node.fields();
        ObjectNode newNode = JsonNodeFactory.instance.objectNode();
        Map.Entry<String, JsonNode> currNode;
        while(elements.hasNext()) {
            currNode = elements.next();
            String key = currNode.getKey();
            JsonNode val = null;
            if (currNode.getValue().isObject() && currNode.getValue().has("records")) {
                val = currNode.getValue().get("records");
            } else {
                val = currNode.getValue();
            }
            newNode.set(key, val);
        }
        return newNode;
    }

}
