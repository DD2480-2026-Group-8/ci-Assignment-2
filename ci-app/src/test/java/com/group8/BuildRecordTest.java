package com.group8;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class BuildRecordTest {

    @Test
    public void testToJSONObject() {
        String commit = "000111";
        String status = "SUCCESS";
        long timestamp = System.currentTimeMillis();
        String log = "log";

        BuildRecord buildRecord = new BuildRecord(commit, status, timestamp, log);
        JSONObject json = buildRecord.toJSONObject();

        assertEquals(commit, json.getString("commit"));
        assertEquals(status, json.getString("status"));
        // make it wrong
        //assertEquals(status, "FAILURE");


        String expectedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));
        assertEquals(expectedDate, json.getString("date"));

        assertEquals(log, json.getString("log"));
    }
}
