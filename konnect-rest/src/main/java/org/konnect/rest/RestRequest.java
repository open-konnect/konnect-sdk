package org.konnect.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import org.konnect.rest.util.JsonHelper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Getter
public class RestRequest {

    private String uri;
    private String api;
    private HttpMethod httpMethod;
    private HttpAccept accept;
    private Map<String, String> queryParams;
    private Map<String, String> headers;
    private Object requestBody;
    private RequestConfig requestConfig;

    private RestRequest(Builder builder) {
        this.uri = builder.uri;
        this.api = builder.api;
        this.httpMethod = builder.httpMethod;
        this.accept = builder.accept;
        this.queryParams = Collections.unmodifiableMap(builder.queryParams);
        this.headers = Collections.unmodifiableMap(builder.headers);
        this.requestBody = builder.requestBody;
        this.requestConfig = builder.requestConfig;
    }

    public String loggableRequest() {
        String bodyStr = "null";
        if (requestBody != null) {
            try {
                bodyStr = JsonHelper.instance().writeValueAsString(requestBody);
            } catch (JsonProcessingException e) {
                // ignore
            }
        }
        return String.format("RestRequest %s -> %s%s with body %s", httpMethod, uri, api, bodyStr);
    }

    protected HttpRequest buildHttpRequest() {
        final HttpRequest.Builder builder = HttpRequest.newBuilder().uri(buildUri());

        if (this.requestConfig != null && this.requestConfig.getTimeout() != null) {
            builder.timeout(this.requestConfig.getTimeout());
        }

        final String httpAccept = this.accept != null ? this.accept.getVal() : HttpAccept.ANY.getVal();
        builder.header("Accept", httpAccept);
        if (this.headers != null) {
            this.headers.entrySet().forEach(e -> builder.header(e.getKey(), e.getValue()));
        }

        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
        if (this.requestBody != null) {
            final String json = JsonHelper.convertToJsonString(this.requestBody);
            publisher = HttpRequest.BodyPublishers.ofString(json);
        }

        builder.method(this.httpMethod.name(), publisher);
        return builder.build();
    }

    private URI buildUri() {
        StringBuilder sb = new StringBuilder();
        if (!this.uri.startsWith("http")) {
            sb.append("https://");
        }
        sb.append(this.uri);
        if (this.uri.endsWith("/")) sb.deleteCharAt(sb.length() - 1);
        if (this.api.charAt(0) != '/') sb.append("/");
        sb.append(this.api);

        if (this.queryParams != null) {
            StringJoiner joiner = new StringJoiner("&");
            this.queryParams.entrySet().forEach(e -> {
                String key = URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8);
                String value = URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8);
                joiner.add(key + "=" + value);
            });
            sb.append("?").append(joiner.toString());
        }
        return URI.create(sb.toString());
    }


    public static class Builder {
        private String uri;
        private String api;
        private HttpMethod httpMethod;
        private HttpAccept accept;
        private Map<String, String> queryParams = new HashMap<>();
        private Map<String, String> headers = new HashMap<>();
        private Object requestBody;
        private RequestConfig requestConfig;

        public Builder withUri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder withApi(String api) {
            this.api = api;
            return this;
        }

        public Builder withHttpMethod(HttpMethod method) {
            this.httpMethod = method;
            return this;
        }

        public Builder withAccept(HttpAccept accept) {
            this.accept = accept;
            return this;
        }

        public Builder withQueryParam(String key, String val) {
            queryParams.put(key, val);
            return this;
        }

        public Builder withHeader(String key, String val) {
            headers.put(key, val);
            return this;
        }

        public Builder withRequestConfig(RequestConfig requestConfig) {
            this.requestConfig = requestConfig;
            return this;
        }

        public Builder withBody(Object body) {
            this.requestBody = body;
            return this;
        }

        public RestRequest build() {
            return new RestRequest(this);
        }
    }
}
