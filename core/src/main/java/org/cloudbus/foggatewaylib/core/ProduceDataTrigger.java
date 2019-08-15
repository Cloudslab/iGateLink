package org.cloudbus.foggatewaylib.core;

/**
 * {@link Trigger} implementation that calls
 * {@link ExecutionManager#produceData(String, long, Data...)} for every new {@link Data}.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link Trigger} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
public class ProduceDataTrigger<T extends Data> extends Trigger<T> {
    private String outputDataKey;

    /**
     * @param outputDataKey the key of the output data that need to be produced
     * @param dataType the type of {@link Data} in the {@link Store} that this {@link Trigger} is
     *                 observing.
     * @see ExecutionManager#addProvider(String, String, Provider)
     * @see ExecutionManager#addStore(String, Store)
     */
    public ProduceDataTrigger(String outputDataKey, Class<T> dataType) {
        super(dataType);
        this.outputDataKey = outputDataKey;
    }

    /**
     * @see Trigger#onDataStored(Store, Data[])
     * @see ExecutionManager#produceData(String, long, Data...)
     */
    @Override
    public void onDataStored(Store<T> store, T... data) {
        getExecutionManager().produceData(outputDataKey,
                data[0].getRequestID(),
                data);
    }
}
