package org.cloudbus.fogappcamerademo;

import org.cloudbus.foggatewaylib.Data;
import org.cloudbus.foggatewaylib.SequentialDataProvider;


public class DummyProvider<T extends Data> extends SequentialDataProvider<T, T> {

    public DummyProvider(Class<T> type) {
        super(type, type);
    }

    @Override
    public T[] getData(ProgressPublisher progressPublisher, long requestID, T... input){
        progressPublisher.publish(1, "Done");
        return input;
    }
}
