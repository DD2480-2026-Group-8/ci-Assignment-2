package com.group8;

import static org.junit.jupiter.api.Assertions.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
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
                JSONObject obj = new JSONObject();
                obj.put("state", "success");
                String jsonString = obj.toString();

                @SuppressWarnings("unchecked")
                HttpResponse<T> mockResponse = (HttpResponse<T>) new MockHttpResponse(201, jsonString);
                return mockResponse;
            }
        };

        StatusToGithub statusToGithub = new StatusToGithub(TEST_OWNER, TEST_REPO, testWrapper);
        boolean result = statusToGithub.setCommitStatus("abc123", "success");

        assertTrue(result);
    }

    /**
     * Test that getCommitStatus returns the correct status (success)
     */
    @Test
    void testGetCommitStatusReturnsSuccess() throws Exception {
        HttpClientWrapper testWrapper = new HttpClientWrapper() {
            @Override
            public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception {
                JSONObject obj = new JSONObject();
                obj.put("state", "success");
                String jsonString = obj.toString();

                @SuppressWarnings("unchecked")
                HttpResponse<T> mockResponse = (HttpResponse<T>) new MockHttpResponse(200, jsonString);
                return mockResponse;
            }
        };

        StatusToGithub statusToGithub = new StatusToGithub(TEST_OWNER, TEST_REPO, testWrapper);
        String result = statusToGithub.getCommitStatus("abc123");

        assertEquals("success", result);
    }


    /**
     * Test that getCommitStatus returns the correct status (failure)
     */
    @Test
    void testGetCommitStatusReturnsFailure() throws Exception {
        HttpClientWrapper testWrapper = new HttpClientWrapper() {
            @Override
            public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception {
                JSONObject obj = new JSONObject();
                obj.put("state", "failure");
                String jsonString = obj.toString();

                @SuppressWarnings("unchecked")
                HttpResponse<T> mockResponse = (HttpResponse<T>) new MockHttpResponse(200, jsonString);
                return mockResponse;
            }
        };

        StatusToGithub statusToGithub = new StatusToGithub(TEST_OWNER, TEST_REPO, testWrapper);
        String result = statusToGithub.getCommitStatus("abc123");

        assertEquals("failure", result);
    }

    /**
     * Test that getCommitStatus returns the correct status (pending)
     */
    @Test
    void testGetCommitStatusReturnsPending() throws Exception {
        HttpClientWrapper testWrapper = new HttpClientWrapper() {
            @Override
            public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws Exception {
                JSONObject obj = new JSONObject();
                obj.put("state", "success");
                String jsonString = obj.toString();

                @SuppressWarnings("unchecked")
                HttpResponse<T> mockResponse = (HttpResponse<T>) new MockHttpResponse(400, jsonString); // 400 --> returns pending
                return mockResponse;
            }
        };

        StatusToGithub statusToGithub = new StatusToGithub(TEST_OWNER, TEST_REPO, testWrapper);
        String result = statusToGithub.getCommitStatus("abc123");

        assertEquals("pending", result);
    }


    /**
     * Simple mock implementation of HttpResponse for testing.
     */
    private static class MockHttpResponse implements HttpResponse<String> {
        private final int statusCode;
        public final String body;

        public MockHttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        @Override
        public int statusCode() {
            return statusCode;
        }

        @Override
        public String body() {
            return body;
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
