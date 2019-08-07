package org.cloudbus.foggatewaylib;

public class FilteredBulkDataTrigger<T extends Data> extends BulkDataTrigger<T> {
    private long requestID;
    private BulkDataTrigger<T> trigger;

    public long getRequestID() {
        return requestID;
    }

    public FilteredBulkDataTrigger(long requestID, BulkDataTrigger<T> trigger) {
        super(trigger.getDataType());
        this.requestID = requestID;
        this.trigger = trigger;
    }

    @Override
    public void onNewData(DataStore<T> dataStore, T... data) {
        if (data[0].getRequestID() == requestID)
            trigger.onNewData(dataStore, data);
    }
}
