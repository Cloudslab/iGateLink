package org.cloudbus.foggatewaylib;

import java.util.concurrent.Executors;

public abstract class ThreadPoolDataProvider<T extends Data, S extends Data>
        extends AsyncProvider<T, S> {

    public ThreadPoolDataProvider(int nThreads, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        setExecutor(Executors.newFixedThreadPool(nThreads));
    }

}
