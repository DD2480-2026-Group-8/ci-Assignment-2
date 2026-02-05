package com.group8;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * ContinuousIntegrationServer acts as a simple webhook endpoint.
 * For now it just responds with a basic message; later you'll plug in
 * cloning, building, testing, and GitHub status updates here.
 */
public class ContinuousIntegrationServer extends AbstractHandler {

    @Override
    public void handle(
            String target,
            Request baseRequest,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/plain;charset=utf-8");
        if (baseRequest != null) {
            baseRequest.setHandled(true);
        }

        String method = request.getMethod();
        String path = request.getRequestURI();

        System.out.printf("Received %s %s%n", method, path);

        try (PrintWriter writer = response.getWriter()) {
            if ("GET".equalsIgnoreCase(method) && "/".equals(path)) {
                response.setStatus(HttpServletResponse.SC_OK);
                writer.println("CI Server is up and running");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                writer.println("404 Not Found");
            }
        }

        // TODO: plug in CI logic here:
        // 1. Parse webhook payload (repo/branch/SHA)
        // 2. Clone or update repository
        // 3. Run build/tests
        // 4. Report status back to GitHub
    }

        /**
         * Starts the CI server on port 8080.
         */
        public static void main(String[] args) throws Exception {
                int port = 8080;
                Server server = new Server(port);
                server.setHandler(new ContinuousIntegrationServer());

                System.out.println("ContinuousIntegrationServer starting on http://localhost:" + port);
                server.start();
                server.join();
        }
}
