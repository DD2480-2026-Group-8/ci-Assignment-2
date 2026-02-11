package com.group8;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONObject;

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

        response.setContentType("text/html;charset=utf-8");
        if (baseRequest != null) {
            baseRequest.setHandled(true);
        }

        String method = request.getMethod();
        String path = request.getRequestURI();

        System.out.printf("Received %s %s%n", method, path);

        try (PrintWriter writer = response.getWriter()) {
            if ("GET".equalsIgnoreCase(method)) {
                if ("/".equals(path)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    writer.println(
                            "CI Server is up and running. Go to <a href=\"/builds\">/builds</a> to see the build history."
                    );
                    return;
                } else if ("/builds".equals(path)) {
                    writer.println(historyManager.getBuildList());
                    return;
                }else if (path.startsWith("/build/")) {
                    String buildId = path.substring(7);
                    writer.println(historyManager.getBuildDetail(buildId));
                    return;
                }
            }
            if ("POST".equalsIgnoreCase(method) && "/webhook".equals(path)) {
                try {
                    // 1. Body
                    String body = request.getReader().lines().collect(Collectors.joining("\n"));

                    // 2. parse JSON
                    JSONObject payload = new JSONObject(body);
                    String ref = payload.optString("ref", "");
                    // 2. SHA
                    String sha = payload.optString("after", "");
                    // 3. clone URL

                    String cloneURL = payload.getJSONObject("repository").getString("clone_url"); // web addres for cloning
                    System.out.println("Incoming push on ref: " + ref);

                    // 3. check branch (run CI for assessment and main)
                    if ("refs/heads/assessment".equals(ref) || "refs/heads/main".equals(ref)) {
                        System.out.println("Assessment or main branch detected! Triggering CI process...");

                        response.setStatus(HttpServletResponse.SC_OK);
                        writer.println("CI started for " + ref + ".");

                        // trigger the actual CI !!!
                        CIrunner.triggerCI(cloneURL, ref, sha);
                    } else {
                        System.out.println("Not an assessment/main branch. Ignore.");
                        response.setStatus(HttpServletResponse.SC_OK);
                        writer.println("Not assessment/main branch, nothing to do.");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to process webhook: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writer.println("Error parsing JSON");
                }
                return;
            }
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writer.println("404 Not Found");
        }
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
