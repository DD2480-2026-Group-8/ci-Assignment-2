package com.group8;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.group8.ContinuousIntegrationServer;

class ContinuousIntegrationServerTest {

    private ContinuousIntegrationServer ciServer;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        ciServer = new ContinuousIntegrationServer();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    /**
     * Test that the server responds with 200 OK and a friendly message on the root path.
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
}

