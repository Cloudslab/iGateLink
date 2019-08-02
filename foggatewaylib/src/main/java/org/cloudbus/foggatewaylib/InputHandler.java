package org.cloudbus.foggatewaylib;

import android.os.Bundle;

public abstract class InputHandler< T extends Data, S extends DataStore<T> > extends ForegroundService{
    protected S inStore;

    protected abstract S initInStore();

    @Override
    protected void init(Bundle extras) {
        inStore = initInStore();
    }

}
