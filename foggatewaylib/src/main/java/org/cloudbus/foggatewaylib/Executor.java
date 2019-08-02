package org.cloudbus.foggatewaylib;

import android.os.Bundle;

public abstract class Executor<T extends Data, S extends Data> extends ForegroundService{
    protected DataStore<T> inStore;
    protected DataStore<S> outStore;

    protected abstract DataStore<T> initInStore();
    protected abstract DataStore<S> initOutStore();

    @Override
    protected void init(Bundle extras) {
        inStore = initInStore();
        outStore = initOutStore();
    }
}
