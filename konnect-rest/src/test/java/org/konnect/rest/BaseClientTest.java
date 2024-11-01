package org.konnect.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;

public class BaseClientTest {

    private String baseUrl = "https://jsonplaceholder.typicode.com";

    private RestClient baseClient = new BaseRestClient();

    @Test
    public void testGetApi() throws RestApiException {
        RestRequest request = new RestRequest.Builder()
                .withUri(baseUrl)
                .withApi("/posts/1")
                .withHttpMethod(HttpMethod.GET)
                .build();
        RestResponse<String> resp = baseClient.call(request, String.class);
        System.out.println("API resp => " + resp.getResponseBody());
    }
}
