package com.group8;
import java.net.*;
import java.net.http.*;

public class StatusToGithub {
    // private final String githubToken;
    // private final String client;

    // accept (header), owner, repo, ref
    public String getCommitStatus(String owner, String repo, String ref) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/commits/%s/status", owner, repo, ref);
            String token = System.getenv("GH_TOKEN");
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").header("authorization", "bearer " + token).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        StatusToGithub status = new StatusToGithub();

        String url = status.getCommitStatus("DD2480-2026-Group-8", "ci-Assignment-2", "lz-test-commit"); // TEST
        System.out.println(url);
    }
}