package com.group8;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * build record representing the result of a single CI build
 *
 * @param commit    Git commit SHA that triggered this build
 * @param status    Build result status, expected values: "SUCCESS" or "FAILURE"
 * @param timestamp Build time in milliseconds since epoch
 * @param log       Log message
 */
public record BuildRecord(String commit, String status, long timestamp, String log) {

    /**
     * Converts this build record into JSON for persistence
     */
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        json.put("commit", this.commit);
        json.put("status", this.status);
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(this.timestamp));
        json.put("date", dateStr);
        json.put("log", this.log);
        return json;
    }
}
