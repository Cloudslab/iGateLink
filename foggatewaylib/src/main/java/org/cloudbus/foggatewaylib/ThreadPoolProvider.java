package org.cloudbus.foggatewaylib;

import java.util.concurrent.Executors;

public abstract class ThreadPoolProvider<T extends Data, S extends Data>
        extends AsyncProvider<T, S> {

    public ThreadPoolProvider(int nThreads, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        setExecutor(Executors.newFixedThreadPool(nThreads));
    }

}
