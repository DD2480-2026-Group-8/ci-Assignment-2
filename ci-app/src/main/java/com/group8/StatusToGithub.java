package com.group8;

import java.net.*;
import java.net.http.*;
import org.json.JSONObject;

/**
 * Client for interacting with the GitHub Commit Status API (P3).
 * <p>
 * It reads a personal access token from the {@code GH_TOKEN} environment
 * variable and exposes convenience methods to get and set the status of a
 * commit in a given repository.
 * </p>
 */
public class StatusToGithub {
    private final String token;
    private final String owner;
    private final String repo;
    private final HttpClientWrapper client;

    /**
     * Creates a status client for the given repository using the default
     * {@link HttpClientWrapper} implementation.
     *
     * @param owner repository owner (user or organisation)
     * @param repo  repository name
     */
    public StatusToGithub(String owner, String repo) {
        this(owner, repo, new HttpClientWrapperImpl());
    }

    /**
     * Constructor for testing
     * {@link HttpClientWrapper}.
     *
     * @param owner  repository owner
     * @param repo   repository name
     * @param client HTTP client wrapper used to perform requests
     */
    StatusToGithub(String owner, String repo, HttpClientWrapper client) {
        this.owner = owner;
        this.repo = repo;
        this.client = client;
        this.token = System.getenv("GH_TOKEN");
    }

    /**
     * Retrieves commit status from GitHub for the given SHA.
     *
     * @param sha Git commit SHA to look up
     * @return one of {@code "success"}, {@code "failure"} or {@code "pending"}
     *         depending on the latest combined status; returns {@code "failure"}
     *         if an exception occurs
     */
    public String getCommitStatus(String sha) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/commits/%s/status", owner, repo, sha);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json")
                    .header("authorization", "bearer " + token).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) { // found the commit
                JSONObject obj = new JSONObject(response.body());
                return obj.getString("state");
            } else {
                System.out.println(response.statusCode());
                return "pending";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failure";
        }
    }

    /**
     * Sets the commit status for the given SHA.
     *
     * @param sha   Git commit SHA to annotate
     * @param state desired status ({@code "error"}, {@code "failure"},
     *              {@code "pending"} or {@code "success"})
     * @return {@code true} if GitHub accepted the status (HTTP 201),
     *         {@code false} otherwise or when the state is invalid
     */
    public boolean setCommitStatus(String sha, String state) {
        try {
            if (!(state.equals("error") || state.equals("pending") || state.equals("success")
                    || state.equals("failure"))) { // only valid states
                return false;
            }

            String url = String.format("https://api.github.com/repos/%s/%s/statuses/%s", owner, repo, sha);

            // JSON
            JSONObject obj = new JSONObject();
            obj.put("state", state);
            String jsonString = obj.toString();
            obj.put("context", "CI Server (Group 8)");
            obj.put("description", state.equals("success") ? "Build and tests passed" : "Build or tests failed");

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json")
                    .header("authorization", "bearer " + token)
                    .header("content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonString))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.statusCode());
            if (response.statusCode() == 201) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        StatusToGithub status = new StatusToGithub("DD2480-2026-Group-8", "ci-Assignment-2");
        boolean url = status.setCommitStatus("fcfc6c60da2c1cef506eec5089c3eca07d38900d", "error");
        String commitStatus = status.getCommitStatus("fcfc6c60da2c1cef506eec5089c3eca07d38900d");

        System.out.println(commitStatus);
        System.out.println(url);
    }
}