package com.group8;
import java.net.*;

public class StatusToGithub {
    // private final String githubToken;
    // private final String client;

    // accept (header), owner, repo, ref
    public String getCommitStatus(String owner, String repo, String ref) {
        String url = String.format(" https://api.github.com/repos/%s/%s/commits/%s/status", owner, repo, ref);
        return url;
    }

    public static void main(String[] args) {
        StatusToGithub status = new StatusToGithub();
    
        String url = status.getCommitStatus("louisazhang", "ci-Assignment-2", "TESTING");
        System.out.println(url);
    }
}