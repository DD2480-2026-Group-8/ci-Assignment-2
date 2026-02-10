package com.group8;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Simple wrapper interface for HttpClient to enable testing.
 * HttpClient is final and cannot be mocked directly.
 */
public interface HttpClientWrapper {
    <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception;
}
