package com.group8;
import java.net.*;
import java.net.http.*;
import org.json.JSONObject;


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

    // accept (header), owner, repo, sha + STATES
    // STATES: error, failure, pending, success
    public boolean setCommitStatus(String sha, String state) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/statuses/%s", owner, repo, sha); 
            HttpClient client = HttpClient.newBuilder().build();

            // JSON
            JsonObject obj = new JsonObject();
            obj.addProperty("state", "success");
            String jsonString = obj.toString();

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").header("authorization", "bearer " + token)
                .header("content-type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonString)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(response.statusCode());
            if (response.statusCode() == 201) {
                System.out.println("STATUS WAS SET");
                return true;
            } else {
                System.out.println("STATUS WAS NOT SET");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }  

    public static void main(String[] args) {
        StatusToGithub status = new StatusToGithub("DD2480-2026-Group-8", "ci-Assignment-2");
        boolean url = status.setCommitStatus("cbc1e57873979dbd97ccd532a01c85e138592f1a", "success");
        System.out.println(url);
    }
}