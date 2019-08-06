package org.cloudbus.foggatewaylib;

public class ProgressData extends Data {

    private int progress;
    private String message;

    public ProgressData(long requestID, int progress, String message){
        super();
        setRequestID(requestID);
        this.progress = progress;
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }
}
