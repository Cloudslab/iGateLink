package org.cloudbus.foggatewaylib;

/**
 * Data representing a progress update.
 *
 * @see Provider#publishProgresses(long, ProgressData...)
 * @see Provider#publishProgress(long, int, String)
 *
 * @author Riccardo Mancini
 */
public class ProgressData extends Data {

    /**
     * Name of the publisher of this progress update. It must be unique.
     */
    private String publisher;

    /**
     * Positive number from 0 to 99 if request is being processed, negative if an error occurred,
     * 100 if the execution of the request is complete.
     */
    private int progress;

    /**
     * Additional human-readable message attached to the progress update.
     */
    private String message;

    public ProgressData(String publisher, long requestID, int progress, String message){
        super();
        setRequestID(requestID);
        this.publisher = publisher;
        this.progress = progress;
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }

    public String getPublisher() {
        return publisher;
    }
}
