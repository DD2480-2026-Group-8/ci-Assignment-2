package com.group8;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class BuildHistoryManagerTest {

    @TempDir
    Path tempDir;  // JUnit creates and cleans this automatically

    private BuildHistoryManager manager;

    @BeforeEach
    public void setUp() {
        manager = new BuildHistoryManager(tempDir.toString());
    }

@Test
    public void testEmptyBuildList() {
        String buildList = manager.getBuildList();
        assertEquals("No builds found.", buildList);
    }

    @Test
    public void testSaveAndGetBuildList() throws Exception {
        String commit = "000111";
        String status = "SUCCESS";
        long timestamp = 111111L;
        String log = "Build completed successfully.";

        BuildRecord record = new BuildRecord(commit, status, timestamp, log);
        manager.saveBuild(record);
        String buildList = manager.getBuildList();
        assertTrue(buildList.contains("build_" + timestamp));
    }

    @Test
    public void testGetBuildDetail() throws Exception {
        String commit = "000111";
        String status = "SUCCESS";
        long timestamp = 111111L;
        String log = "Build completed successfully.";

        BuildRecord record = new BuildRecord(commit, status, timestamp, log);
        manager.saveBuild(record);

        String buildDetail = manager.getBuildDetail(String.valueOf(timestamp));
        assertTrue(
            buildDetail.contains(commit), 
            () -> "Expected build detail to contain commit " + commit + " but got: " + buildDetail);
        assertTrue(
            buildDetail.contains(status), 
            () -> "Expected build detail to contain status " + status + " but got: " + buildDetail);
        assertTrue(
            buildDetail.contains(log), 
            () -> "Expected build detail to contain log " + log + " but got: " + buildDetail);
    }

    @Test
    public void testGetBuildDetailInvalidBuild() throws Exception {
        String result = manager.getBuildDetail("000000");
        assertTrue(
            result.contains("Error: Build 000000 not found"),
             () -> "Expected error message for non-existent build, but got: " + result);
    }

    @Test 
    public void testOrderOfBuilds() throws Exception {
        long timestamp1 = 111111L;
        long timestamp2 = 333333L;
        long timestamp3 = 222222L;

    
        BuildRecord record1 = new BuildRecord("commit1", "SUCCESS", timestamp1, "Log 1");
        manager.saveBuild(record1);
        BuildRecord record2 = new BuildRecord("commit2", "FAILURE", timestamp2, "Log 2");
        manager.saveBuild(record2);
        BuildRecord record3 = new BuildRecord("commit3", "FAILURE", timestamp3, "Log 3");
        manager.saveBuild(record3);

        String buildList = manager.getBuildList();
        int index1 = buildList.indexOf("build_" + timestamp1);
        int index2 = buildList.indexOf("build_" + timestamp2);
        int index3 = buildList.indexOf("build_" + timestamp3);

        assertTrue(
            index2 < index3, 
            () -> "Newer build should appear before older build");
        assertTrue(
            index3 < index1, 
            () -> "Newer build should appear before older build");
    }
}