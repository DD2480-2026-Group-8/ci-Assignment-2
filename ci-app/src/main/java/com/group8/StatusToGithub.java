public class StatusToGithub {
    // private final String githubToken;
    // private final String client;

    // accept (header), owner, repo, ref
    public String getCommitStatus(String owner, String repo, String ref) {
        String url = String.format(" https://api.github.com/repos/%1/%2/commits/%3/status", owner, repo, ref);
        return url;
    }
}