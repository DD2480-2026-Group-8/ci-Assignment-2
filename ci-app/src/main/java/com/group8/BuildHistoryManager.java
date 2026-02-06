package com.group8;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;


/**
 * Manages the persistence and retrieval of build history
 */
public class BuildHistoryManager {
    private static final String HISTORY_DIR = "./build_history";

    public BuildHistoryManager() {
        // make sure that dir exists
        File dir = new File(HISTORY_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Saves a build record to a JSON file.
     * @param record The build record
     */
    public void saveBuild(BuildRecord record) {
        JSONObject json = record.toJSONObject();

        // format: build_id.json
        File file = new File(HISTORY_DIR, "build_" + record.getTimestamp() + ".json");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(json.toString(4));
        } catch (Exception e) {
            System.err.println("Failed to save build history: " + e.getMessage());
        }
    }
}