package com.group8;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @param status SUCCESS / FAILURE
 */
public record BuildRecord(String commit, String status, long timestamp, String log) {

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
