package org.konnect.rest;

public interface RestClient {

    <T> RestResponse<T> call(RestRequest request, Class<T> responseType) throws RestApiException;
}
