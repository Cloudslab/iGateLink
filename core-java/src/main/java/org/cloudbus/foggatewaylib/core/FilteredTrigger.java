package org.cloudbus.foggatewaylib.core;

/**
 * Simple implementation of a Trigger that filters data based on the request id before triggering.
 * This class wraps another {@link Trigger} which will be called if incoming data has the
 * required id.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link Trigger} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
public class FilteredTrigger<T extends Data> extends Trigger<T> {

    /**
     * The request id used for filtering incoming data.
     */
    private long requestID;

    /**
     * Wrapped normal trigger.
     */
    private Trigger<T> trigger;

    /**
     * @return the request id used for filtering incoming data.
     */
    public long getRequestID() {
        return requestID;
    }

    /**
     * Constructor that wraps another trigger.
     *
     * @param requestID the request id that will be used for filtering incoming data.
     * @param trigger the trigger to be wrapped
     * @see Trigger
     */
    public FilteredTrigger(long requestID, Trigger<T> trigger) {
        super(trigger.getDataType());
        this.requestID = requestID;
        this.trigger = trigger;
    }

    /**
     * Callback called after data is being added to the {@link Store}. If data has the same request
     * id as the one set in this instance, the {@link Trigger#onDataStored(Store, Data[])} callback
     * of the wrapped {@link Trigger} will be called.
     *
     * @param store reference to the {@link Store} the {@code data} is being added to.
     * @param data the data that has been added to the {@code store}.
     */
    @Override
    public void onDataStored(Store<T> store, T... data) {
        if (data[0].getRequestID() == requestID)
            trigger.onDataStored(store, data);
    }

    /**
     * Calls {@link Trigger#attach(ExecutionManager)} on the wrapped {@link Trigger}.
     */
    @Override
    public void onAttach() {
        trigger.attach(getExecutionManager());
    }

    /**
     * Calls {@link Trigger#detach()} on the wrapped {@link Trigger}.
     */
    @Override
    public void onDetach() {
        trigger.detach();
    }
}
