package com.manjrasoft.aneka;

public class ApplicationStatus extends StringType {
    public final static String STATUS_UNSUBMITTED = "Unsubmitted";
    public final static String STATUS_SUBMITTED = "Submitted";
    public final static String STATUS_RUNNING = "Running";
    public final static String STATUS_FINISHED = "Finished";
    public final static String STATUS_ERROR = "Error";
    public final static String STATUS_UNKNOWN = "Unknown";
    public final static String STATUS_PAUSED = "Paused";

    public ApplicationStatus() {
        super("http://www.manjrasoft.com/Aneka/v2.0/WebServices", "ApplicationStatus");
    }

}
