package com.group8;

import java.net.*;
import java.net.http.*;
import org.json.JSONObject;
import org.json.JSONParserConfiguration;

public class StatusToGithub {
    private final String token;
    private final String owner;
    private final String repo;
    private final HttpClientWrapper client;

    public StatusToGithub(String owner, String repo) {
        this(owner, repo, new HttpClientWrapperImpl());
    }

    // Constructor for testing - allows injection of HttpClientWrapper
    StatusToGithub(String owner, String repo, HttpClientWrapper client) {
        this.owner = owner;
        this.repo = repo;
        this.client = client;
        this.token = System.getenv("GH_TOKEN");
    }

    /*
        Parameters: sha to GitHub commit
        Returns: commit status. pending for anything without a commit status yet
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
                return "pending";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
        Parameters: sha, state (error, failure, pending, success)
        Returns: True for successfully setting commit status, False for any errors or failures
     */
    public boolean setCommitStatus(String sha, String state) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/statuses/%s", owner, repo, sha);

            // JSON
            JSONObject obj = new JSONObject();
            obj.put("state", state);
            String jsonString = obj.toString();

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

        // boolean url = status.setCommitStatus("cbc1e57873979dbd97ccd532a01c85e138592f1a", "success");
        System.out.println(url);
    }
}