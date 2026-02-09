package com.group8;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CIrunner {



    // Use processBuilder to run commands 
    public static int runner(List<String> cmd, File dir) throws IOException, InterruptedException {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.directory(dir);
            Process p = pb.start();
            return p.waitFor(); // 0 --> success, >0 --> failure
    }



    public static void triggerCI(String cloneUrl) {
        System.out.println("Starting CI");
        try {
            // 1. Create the parent file for the clones
            File parentFile = new File("ci-parent-builds");
            parentFile.mkdirs();   // check this came into existance 

             // create the actual clone file 
            File cloneDir = new File(parentFile, "cloneFile");
            cloneDir.mkdirs();


            // 2. Do the cloning 
            System.out.println("Cloning... "); // see in console
            runner(List.of("git", "clone", cloneUrl, "cloneFile"),parentFile);


            // 4. Run tests
            String mvn = System.getProperty("os.name").toLowerCase().contains("win")
                ? "mvn.cmd"
                : "mvn";

             System.out.println("Running mvn test...");
            int exitCode = runner(List.of(mvn, "-f", "ci-app/pom.xml", "test"),cloneDir); // point at pom


            // 5. Result:
            if (exitCode == 0) {
                System.out.println("TEST PASSED");
            } else {
                System.out.println("TEST FAILED");
            }

            

        } catch (Exception e) {
            System.err.println("CI error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}