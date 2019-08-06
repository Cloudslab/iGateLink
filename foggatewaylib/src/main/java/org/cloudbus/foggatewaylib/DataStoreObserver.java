package org.cloudbus.foggatewaylib;

public interface DataStoreObserver<T extends Data> {
    void onDataStored(DataStore<T> dataStore, T... data);
}
