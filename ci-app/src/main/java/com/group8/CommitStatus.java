// Completed, Failure, In Progress, Pending

public enum CommitStatus {
    SUCCESS("success"),
    FAILURE("failure"),
    PENDING("pending");

    private final String status;

    CommitStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}