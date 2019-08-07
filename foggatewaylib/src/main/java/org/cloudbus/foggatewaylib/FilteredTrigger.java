package org.cloudbus.foggatewaylib;

public class FilteredTrigger<T extends Data> extends Trigger<T> {
    private long requestID;
    private Trigger<T> trigger;

    public long getRequestID() {
        return requestID;
    }

    public FilteredTrigger(long requestID, Trigger<T> trigger) {
        super(trigger.getDataType());
        this.requestID = requestID;
        this.trigger = trigger;
    }

    @Override
    public void onNewData(Store<T> store, T... data) {
        if (data[0].getRequestID() == requestID)
            trigger.onNewData(store, data);
    }
}
