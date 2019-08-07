package org.cloudbus.foggatewaylib;

public class FilteredBulkTrigger<T extends Data> extends BulkTrigger<T> {
    private long requestID;
    private BulkTrigger<T> trigger;

    public long getRequestID() {
        return requestID;
    }

    public FilteredBulkTrigger(long requestID, BulkTrigger<T> trigger) {
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
