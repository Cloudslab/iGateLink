package org.cloudbus.foggatewaylib;

import java.util.List;

public interface DataStoreObserver<T extends Data> {
    void onDataStored(DataStore<T> dataStore, List<T> data);
}
