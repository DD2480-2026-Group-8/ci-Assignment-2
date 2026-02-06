package com.group8;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildRecord {
    private final String commit;
    private final String status;   // SUCCESS / FAILURE
    private final long timestamp;
    private final String log;

    public BuildRecord(String commit, String status, long timestamp, String log) {
        this.commit = commit;
        this.status = status;
        this.timestamp = timestamp;
        this.log = log;
    }


    public String getCommit() {
        return commit;
    }

    public String getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getLog() {
        return log;
    }

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
