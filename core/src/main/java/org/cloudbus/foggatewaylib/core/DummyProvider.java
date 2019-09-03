package org.cloudbus.foggatewaylib.core;

/**
 * A simple dummy provider that copies the input to the output and publishes a progress with the
 * given value and message.
 *
 * @param <T> type of data in input and output
 *
 * @author Riccardo Mancini
 */
public class DummyProvider<T extends Data> extends SequentialProvider<T, T> {
    private int progress;
    private String message;

    public DummyProvider(int progress, String message, Class<T> type) {
        super(type, type);
        this.progress = progress;
        this.message = message;
    }

    @Override
    public T[] getData(ProgressPublisher progressPublisher, long requestID, T... input){
        progressPublisher.publish(progress, message);
        return input;
    }
}
