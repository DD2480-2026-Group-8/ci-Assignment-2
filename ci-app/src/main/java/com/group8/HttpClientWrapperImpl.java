package com.group8;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Production implementation using Java's HttpClient.
 */
public class HttpClientWrapperImpl implements HttpClientWrapper {
    private final HttpClient client = HttpClient.newBuilder().build();

    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception {
        return client.send(request, responseBodyHandler);
    }
}
