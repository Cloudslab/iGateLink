package com.manjrasoft.aneka;

public class JobStatus extends StringType {
    public static final String STATUS_UNKNOWN = "Unknown";
    public static final String STATUS_QUEUED = "Queued";
    public static final String STATUS_STAGINGIN = "StagingIn";
    public static final String STATUS_RUNNING = "Running";
    public static final String STATUS_STAGINGOUT = "StagingOut";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String STATUS_FAILED = "Failed";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_ABORTED = "Aborted";

    public JobStatus() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "JobStatus");
    }
}
