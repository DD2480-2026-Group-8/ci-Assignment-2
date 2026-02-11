package com.group8;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CIrunner {

    // Use processBuilder to run commands
    public static int runner(List<String> cmd, File dir) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(dir);

        pb.inheritIO();
        Process p = pb.start();
        return p.waitFor(); // 0 --> success (all test pass!), not-zero --> failure (at least one test has
                            // failed!)
    }

    public static BuildRecord triggerCI(String cloneUrl, String ref, String sha) {
        System.out.println("Starting CI");

        long ts = System.currentTimeMillis();
        StringBuilder log = new StringBuilder();
        log.append("Starting CI\n");
        log.append("ref=").append(ref).append("\n");
        log.append("sha=").append(sha).append("\n");

        try {
            // 1. Create the parent file where we will store the clones
            File parentFile = new File("ci-parent-builds");
            parentFile.mkdirs(); // check this came into existance

            // create the actual clone file. The files name is based on the sha.
            File cloneDir = new File(parentFile, sha); // parent, new file name
            cloneDir.mkdirs();

            // 2. Do the cloning (in the clone file)
            System.out.println("Cloning... "); // see in console
            log.append("Cloning...\n");

            String token = System.getenv("GH_TOKEN");
            if (token == null || token.isBlank()) {
                throw new IllegalStateException("GH_TOKEN is not set");
            }
            cloneUrl = cloneUrl.replaceFirst(
                    "https://",
                    "https://oauth2:" + token + "@"
            );

            int cloneExit = runner(List.of("git", "clone", cloneUrl, "repo"), cloneDir); // also, makes sure the new file (inside
            // cloneDir) is always called repo
            log.append("git clone exit=").append(cloneExit).append("\n");
            if (cloneExit != 0) {
                return new BuildRecord(sha, "FAILURE", ts, log.toString());
            }

            File testDir = new File(cloneDir, "repo");

            // that can work for many operating systems
            String mvn = System.getProperty("os.name").toLowerCase().contains("win")
                    ? "mvn.cmd"
                    : "mvn";

            // 4. do compile (mvn compile)
            System.out.println("Doing mvn Compile... ");
            log.append("Doing mvn Compile...\n");
            int compileResult = runner(List.of(mvn, "-f", "ci-app/pom.xml", "compile"), testDir); // point at pom
            log.append("mvn compile exit=").append(compileResult).append("\n");
            if (compileResult == 0) {
                System.out.println("Compile is succesfull");
            } else {
                System.out.println("Compile has failed");
                return new BuildRecord(sha, "FAILURE", ts, log.toString());
            }

            // 4. Run tests,
            System.out.println("Running mvn test for " + ref);
            log.append("Running mvn test for ").append(ref).append("\n");
            int finalResult = runner(List.of(mvn, "-f", "ci-app/pom.xml", "test"), testDir); // point at pom
            log.append("mvn test exit=").append(finalResult).append("\n");

            // 5. Result: What happened and for what commit?
            String state;
            String status;
            if (finalResult == 0) {
                System.out.println("For SHA" + sha + " all tests have passed!");
                state = "success";
                status = "SUCCESS";
            } else {
                System.out.println("For SHA " + sha + " at least one test has failed!");
                state = "failure";
                status = "FAILURE";
            }
            log.append("status=").append(status).append("\n");

            // 6. Notify GitHub about the result (P3)
            try {
                StatusToGithub statusClient = new StatusToGithub("DD2480-2026-Group-8", "ci-Assignment-2");
                boolean ok = statusClient.setCommitStatus(sha, state);
                System.out.println("GitHub commit status update (" + state + ") for " + sha + ": " + ok);
                log.append("GitHub commit status update (").append(state).append(") ok=").append(ok).append("\n");
            } catch (Exception e) {
                System.err.println("Failed to update GitHub commit status for " + sha + ": " + e.getMessage());
                e.printStackTrace();
                log.append("Failed to update GitHub commit status: ").append(e.getMessage()).append("\n");
            }

            return new BuildRecord(sha, status, ts, log.toString());

        } catch (Exception e) {
            System.err.println("CI error: " + e.getMessage());
            e.printStackTrace();
            log.append("CI error: ").append(e.getMessage()).append("\n");
            return new BuildRecord(sha, "FAILURE", ts, log.toString());
        }
    }
}