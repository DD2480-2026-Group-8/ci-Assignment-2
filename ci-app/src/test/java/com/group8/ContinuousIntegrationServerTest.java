package com.group8;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContinuousIntegrationServerTest {

    /**
     * Test double of the CI server that records invocations of {@link #triggerCI}
     * instead of spawning real CI jobs (git clone + mvn) during unit tests.
     *
     * This prevents recursive Maven invocations on CI (e.g. GitHub Actions) where
     * {@code GH_TOKEN} is set and {@link CIrunner#triggerCI} would otherwise run
     * a full build inside the tests.
     */
    private static class TestContinuousIntegrationServer extends ContinuousIntegrationServer {
        boolean ciTriggered = false;
        String lastRef;

        @Override
        protected BuildRecord triggerCI(String cloneURL, String ref, String sha) {
            this.ciTriggered = true;
            this.lastRef = ref;
            // Do NOT call CIrunner.triggerCI here – unit tests should stay fast and
            // side‑effect free.
            return new BuildRecord(sha, "SUCCESS", System.currentTimeMillis(), "stub build from test");
        }
    }

    private TestContinuousIntegrationServer ciServer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        ciServer = new TestContinuousIntegrationServer();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    /**
     * Test that the server responds with 200 OK and a friendly message on the root
     * path.
     */
    @Test
    void testHandleIndex() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/");

        ciServer.handle("/", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(
                responseWriter.toString().contains("CI Server is up and running"),
                () -> "Expected response to contain 'CI Server is up and running' but got: " + responseWriter);
    }

    /**
     * Test that the server responds with 404 when an unknown path is requested.
     */
    @Test
    void testHandleNotFound() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/invalid");

        ciServer.handle("/invalid", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(
                responseWriter.toString().contains("404"),
                () -> "Expected response to contain '404' but got: " + responseWriter);
    }

    private String loadResource(String path) throws Exception {
        try (var is = getClass().getClassLoader().getResourceAsStream(path)) {
            assert is != null : "Test resource not found: " + path;
            return new String(is.readAllBytes());
        }
    }

    @Test
    void testAssessmentBranchTriggersCI() throws Exception {
        String payload = loadResource("webhook/push-assessment.json");

        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(
                new java.io.BufferedReader(new java.io.StringReader(payload)));
        when(request.getRequestURI()).thenReturn("/webhook");

        ciServer.handle("/", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);

        assertTrue(
                responseWriter.toString().toLowerCase().contains("assessment"),
                () -> "Expected assessment branch handling, but got: " + responseWriter);

        // Ensure CI trigger hook was called with correct ref
        assertTrue(ciServer.ciTriggered, "Expected CI to be triggered for assessment branch");
        assertTrue(
                "refs/heads/assessment".equals(ciServer.lastRef),
                () -> "Expected lastRef to be refs/heads/assessment but was: " + ciServer.lastRef);
    }

    @Test
    void testMainBranchTriggersCI() throws Exception {
        // Reuse assessment payload but change ref to main
        String payload = loadResource("webhook/push-assessment.json")
                .replace("refs/heads/assessment", "refs/heads/main");

        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(
                new java.io.BufferedReader(new java.io.StringReader(payload)));
        when(request.getRequestURI()).thenReturn("/webhook");

        ciServer.handle("/", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);

        assertTrue(
                responseWriter.toString().toLowerCase().contains("refs/heads/main")
                        || responseWriter.toString().toLowerCase().contains("ci started for"),
                () -> "Expected main branch handling, but got: " + responseWriter);

        // Ensure CI trigger hook was called with correct ref
        assertTrue(ciServer.ciTriggered, "Expected CI to be triggered for main branch");
        assertTrue(
                "refs/heads/main".equals(ciServer.lastRef),
                () -> "Expected lastRef to be refs/heads/main but was: " + ciServer.lastRef);
    }

    @Test
    void testNonAssessmentBranchIsIgnored() throws Exception {
        String payload = loadResource("webhook/push-not-assessment.json");

        when(request.getMethod()).thenReturn("POST");
        when(request.getReader()).thenReturn(
                new java.io.BufferedReader(new java.io.StringReader(payload)));
        when(request.getRequestURI()).thenReturn("/webhook");

        ciServer.handle("/", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);

        assertTrue(
                responseWriter.toString().toLowerCase().contains("ignore")
                        || responseWriter.toString().toLowerCase().contains("not assessment"),
                () -> "Expected non-assessment branch to be ignored, but got: " + responseWriter);
    }

    // CI tests:
    void testInvalidPathReturns404() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/invalid");

        ciServer.handle("/invalid", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(
                responseWriter.toString().contains("404"),
                () -> "Expected 404 response but got: " + responseWriter);
    }

    @Test
    void testMalformedJsonReturns400() throws Exception {
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/webhook");

        String badPayload = "{ not valid json ";

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(badPayload)));

        ciServer.handle("/webhook", null, request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
