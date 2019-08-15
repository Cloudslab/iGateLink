package org.cloudbus.foggatewaylib.core;

import java.util.concurrent.Executors;

/**
 * {@link AsyncProvider} that uses a dedicated {@link Executors#newFixedThreadPool(int)} for
 * scheduling its tasks.
 * As a result, tasks will be executed in parallel, up to a maximum of {@code nThreads}, as
 * set in {@link #ThreadPoolProvider(int, Class, Class)}. Exceeding tasks will be queued waiting
 * for the running tasks to complete their execution.
 *
 * @param <T> type of the input data
 * @param <S> type of the output data
 * @see Executors#newFixedThreadPool(int)
 * @see SequentialProvider
 *
 * @author Riccardo Mancini
 */
public abstract class ThreadPoolProvider<T extends Data, S extends Data>
        extends AsyncProvider<T, S> {

    private int nThreads;

    public ThreadPoolProvider(int nThreads, Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        this.nThreads = nThreads;
        setExecutor(Executors.newFixedThreadPool(nThreads));
    }

    /**
     * Returns {@code true} if there are fewer tasks that don't have completed execution than there
     * are threads.
     */
    public boolean isFree(){
        return getPendingTasks() < nThreads;
    }

}
