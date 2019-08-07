package org.cloudbus.foggatewaylib;

import java.util.concurrent.Executors;

public abstract class SequentialProvider<T extends Data, S extends Data>
        extends AsyncProvider<T, S> {

    public SequentialProvider(Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        setExecutor(Executors.newSingleThreadExecutor());
    }
}
