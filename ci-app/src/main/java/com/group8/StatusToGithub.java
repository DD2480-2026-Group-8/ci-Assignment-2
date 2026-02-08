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
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

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
    
        String url = status.getCommitStatus("louisazhang", "ci-Assignment-2", "TESTING"); // TEST
        System.out.println(url);
    }
}