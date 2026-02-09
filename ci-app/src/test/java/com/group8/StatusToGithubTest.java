package com.group8;

import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

class StatusToGithubTest {

    private static final String TEST_OWNER = "test-owner";
    private static final String TEST_REPO = "test-repo";

    /**
     * Test that setCommitStatus returns true for successful status code 201.
     */
    @Test
    void testSetCommitStatusReturnsTrueFor201() throws Exception {
        HttpClientWrapper testWrapper = new HttpClientWrapper() {
            @Override
            public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception {
                @SuppressWarnings("unchecked")
                HttpResponse<T> mockResponse = (HttpResponse<T>) new MockHttpResponse(201);
                return mockResponse;
            }
        };

        StatusToGithub statusToGithub = new StatusToGithub(TEST_OWNER, TEST_REPO, testWrapper);
        boolean result = statusToGithub.setCommitStatus("abc123", "success");

        assertTrue(result);
    }

    /**
     * Test that getCommitStatus returns the correct URL.
     */
    @Test
    void testGetCommitStatusReturnsUrl() throws Exception {
        HttpClientWrapper testWrapper = new HttpClientWrapper() {
            @Override
            public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception {
                @SuppressWarnings("unchecked")
                HttpResponse<T> mockResponse = (HttpResponse<T>) new MockHttpResponse(200);
                return mockResponse;
            }
        };

        StatusToGithub statusToGithub = new StatusToGithub(TEST_OWNER, TEST_REPO, testWrapper);
        String result = statusToGithub.getCommitStatus("abc123");

        assertTrue(result.contains("api.github.com/repos/test-owner/test-repo/commits/abc123/status"));
    }

    /**
     * Simple mock implementation of HttpResponse for testing.
     */
    private static class MockHttpResponse implements HttpResponse<String> {
        private final int statusCode;

        public MockHttpResponse(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public String body() {
            return "";
        }

        @Override
        public HttpRequest request() {
            return null;
        }

        @Override
        public java.net.http.HttpHeaders headers() {
            return java.net.http.HttpHeaders.of(java.util.Map.of(), (s1, s2) -> true);
        }

        @Override
        public java.util.Optional<java.net.http.HttpResponse<String>> previousResponse() {
            return java.util.Optional.empty();
        }

        @Override
        public java.net.URI uri() {
            return java.net.URI.create("https://api.github.com");
        }

        @Override
        public java.util.Optional<javax.net.ssl.SSLSession> sslSession() {
            return java.util.Optional.empty();
        }

        @Override
        public java.net.http.HttpClient.Version version() {
            return java.net.http.HttpClient.Version.HTTP_1_1;
        }
    }
}
