package org.cloudbus.foggatewaylib.core;

import java.util.concurrent.Executors;

/**
 * {@link AsyncProvider} that uses a dedicated {@link Executors#newSingleThreadExecutor()} for
 * scheduling its tasks.
 * As a result its tasks will be executed one after the other.
 *
 * @param <T> type of the input data
 * @param <S> type of the output data
 * @see Executors#newSingleThreadExecutor()
 * @see ThreadPoolProvider
 *
 * @author Riccardo Mancini
 */
public abstract class SequentialProvider<T extends Data, S extends Data>
        extends AsyncProvider<T, S> {

    public SequentialProvider(Class<T> inputType, Class<S> outputType) {
        super(inputType, outputType);
        setExecutor(Executors.newSingleThreadExecutor());
    }

    /**
     * Returns {@code true} if there are no tasks that don't have completed execution.
     */
    public boolean isFree(){
        return getPendingTasks() == 0;
    }
}
