package org.cloudbus.foggatewaylib.core;

/**
 * {@link Trigger} implementation that calls
 * {@link ExecutionManager#runProvider(String, long, Data...)} for every new {@link Data}.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link Trigger} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
public class RunProviderTrigger<T extends Data> extends Trigger<T> {
    private String providerKey;

    /**
     * @param providerKey the key of the provider that needs to be called
     * @param dataType the type of {@link Data} in the {@link Store} that this {@link Trigger} is
     *                 observing.
     * @see ExecutionManager#addProvider(String, String, Provider)
     */
    public RunProviderTrigger(String providerKey, Class<T> dataType) {
        super(dataType);
        this.providerKey = providerKey;
    }

    /**
     * @see Trigger#onDataStored(Store, Data[])
     * @see ExecutionManager#runProvider(String, long, Data...)
     */
    @Override
    public void onDataStored(Store<T> store, T... data) {
        getExecutionManager().runProvider(providerKey,
                data[0].getRequestID(),
                data);
    }
}
