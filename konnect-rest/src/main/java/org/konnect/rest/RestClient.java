package org.konnect.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import org.konnect.utils.json.JsonUtils;

public interface RestClient {

    default <T> RestResponse<T> call(RestRequest request, Class<T> responseTypeRef) throws RestApiException {
        return call(request, JsonUtils.toTypeReference(responseTypeRef));
    }

    <T> RestResponse<T> call(RestRequest request, TypeReference<T> responseTypeRef) throws RestApiException;
}
