package org.cloudbus.foggatewaylib;

public abstract class Trigger<T extends Data> extends BulkTrigger<T> {

    public Trigger(Class<T> dataType) {
        super(dataType);
    }

    public abstract void onNewData(Store<T> store, T data);

    @Override
    public void onNewData(Store<T> store, T... data) {
        for (T d:data)
            onNewData(store, d);
    }
}
