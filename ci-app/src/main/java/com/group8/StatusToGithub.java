package com.group8;
import java.net.*;
import java.net.http.*;

public class StatusToGithub {
    private final String token;
    private final String owner;
    private final String repo;

    public StatusToGithub(String owner, String repo) {
        this.owner = owner;
        this.repo = repo;
        this.token = System.getenv("GH_TOKEN");
    }
    // accept (header), owner, repo, ref

    public String getCommitStatus(String ref) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/commits/%s/status", owner, repo, ref);
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

    // public boolean setCommitStatus(String owner, String repo, )    

    public static void main(String[] args) {
        StatusToGithub status = new StatusToGithub("DD2480-2026-Group-8", "ci-Assignment-2");

        String url = status.getCommitStatus("lz-test-commit"); // TEST
        System.out.println(url);
    }
}