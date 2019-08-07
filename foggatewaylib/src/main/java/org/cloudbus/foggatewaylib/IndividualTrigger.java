package org.cloudbus.foggatewaylib;

public abstract class IndividualTrigger<T extends Data> extends Trigger<T> {

    public IndividualTrigger(Class<T> dataType) {
        super(dataType);
    }

    public abstract void onNewData(Store<T> store, T data);

    @Override
    public void onNewData(Store<T> store, T... data) {
        for (T d:data)
            onNewData(store, d);
    }
}
