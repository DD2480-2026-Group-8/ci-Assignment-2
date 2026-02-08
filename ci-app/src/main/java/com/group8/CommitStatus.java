package com.group8;

public enum CommitStatus {
    SUCCESS("success"), // latest status for all contexts is success
    FAILURE("failure"), // any contexts report error or failure
    PENDING("pending"); // no statuses or context is pending

    private final String status;

    CommitStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}