package com.group8;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;


/**
 * Manages the persistence and retrieval of build history
 */
public class BuildHistoryManager {
    private static final String HISTORY_DIR =
            System.getProperty("user.home") + "/ci-build-history";

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
            writer.write(json.toString());
        } catch (Exception e) {
            System.err.println("Failed to save build history: " + e.getMessage());
        }
    }

    public String getBuildList() {
        File folder = new File(HISTORY_DIR);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return "No builds found.";
        }

        Arrays.sort(files, (f1, f2) -> {
            long id1 = Long.parseLong(
                    f1.getName().substring(6, f1.getName().length() - 5)
            );
            long id2 = Long.parseLong(
                    f2.getName().substring(6, f2.getName().length() - 5)
            );
            return Long.compare(id2, id1);
        });

        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            String filename = f.getName();
            String buildId = filename.substring(0, filename.length() - 5);

            sb.append("<a href=\"/build/")
                    .append(buildId, 6, filename.length() - 5)
                    .append("\">")
                    .append(buildId)
                    .append("</a><br>");
        }
        return sb.toString();
    }

    public String getBuildDetail(String id) {
        File f = new File(HISTORY_DIR, "build_" + id + ".json");
        if (!f.exists()) {
            return "<p>Error: Build " + id + " not found.</p>";
        }

        try {
            String content = Files.readString(f.toPath());

            JSONObject json = new JSONObject(content);

            return "<h2>Build " + id + "</h2>" +
                    "<ul>" +
                    "<li><b>Date:</b> " + json.optString("date") + "</li>" +
                    "<li><b>Status:</b> " + json.optString("status") + "</li>" +
                    "<li><b>Commit:</b> " + json.optString("commit") + "</li>" +
                    "<li><b>Log:</b><pre>" +
                    json.optString("log") +
                    "</pre></li>" +
                    "</ul>" +
                    "<p><a href=\"/builds\">Back to build list</a></p>" +
                    "</body></html>";

        } catch (Exception e) {
            return "<p>Error reading build file: " + e.getMessage() + "</p>";
        }
    }
}