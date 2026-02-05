package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);

                System.out.printf("Received %s %s%n", request.getMethod(), target);

                // TODO: plug in CI logic here:
                // 1. Parse webhook payload (repo/branch/SHA)
                // 2. Clone or update repository
                // 3. Run build/tests
                // 4. Report status back to GitHub

                response.getWriter().println("CI job done");
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
