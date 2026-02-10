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
            return p.waitFor(); // 0 --> success (all test pass!), not-zero --> failure (at least one test has failed!)
    }



    public static void triggerCI(String cloneUrl, String ref, String sha) {
        System.out.println("Starting CI");
        try {
            // 1. Create the parent file where we will store the clones
            File parentFile = new File("ci-parent-builds");
            parentFile.mkdirs();   // check this came into existance 

             // create the actual clone file. The files name is based on the sha. 
            File cloneDir = new File(parentFile, sha); // parent, new file name
            cloneDir.mkdirs();


            // 2. Do the cloning (in the clone file)
            System.out.println("Cloning... "); // see in console
            runner(List.of("git", "clone", cloneUrl, "repo"), cloneDir); // also, makes sure the new file (inside cloneDir) is always called repo
            File testDir = new File(cloneDir, "repo");
            
            //that can work for many operating systems
            String mvn = System.getProperty("os.name").toLowerCase().contains("win")
                ? "mvn.cmd"
                : "mvn";


            // 4. do compile (mvn compile)
            System.out.println("Doing mvn Compile... "); 
            int compileResult = runner(List.of(mvn, "-f", "ci-app/pom.xml", "compile"),testDir); // point at pom
            if (compileResult == 0) {
                System.out.println("Compile is succesfull");
            } else {
                System.out.println("Compile has failed");
            }  

            // 4. Run tests, 
            System.out.println("Running mvn test for " + ref);
            int finalResult = runner(List.of(mvn, "-f", "ci-app/pom.xml", "test"),testDir); // point at pom


            // 5. Result: What happened and for what commit?
            if (finalResult == 0) {
                System.out.println("For SHA" + sha + " all tests have passed!");
            } else {
                System.out.println("For SHA " + sha + " at least one test has failed!");
            }


        } catch (Exception e) {
            System.err.println("CI error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}