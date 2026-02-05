// Completed, Failure, In Progress, Pending

public enum CommitStatus {
    SUCCESS("success"),
    FAILURE("failure"),
    IN_PROGRESS("in progress");

    private final String status;

    CommitStatus(String status) {
        this.status = status;
    }
}