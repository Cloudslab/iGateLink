package org.cloudbus.foggatewaylib;

public abstract class DataTrigger<T extends Data> extends BulkDataTrigger<T> {

    public DataTrigger(Class<T> dataType) {
        super(dataType);
    }

    public abstract void onNewData(DataStore<T> dataStore, T data);

    @Override
    public void onNewData(DataStore<T> dataStore, T... data) {
        for (T d:data)
            onNewData(dataStore, d);
    }
}
