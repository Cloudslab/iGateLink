package org.cloudbus.foggatewaylib;

public interface StoreObserver<T extends Data> {
    void onDataStored(Store<T> store, T... data);
}
