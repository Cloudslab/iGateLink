package org.cloudbus.foggatewaylib.core;

/**
 * Extension of a {@link Trigger} that executes the callback on every incoming data inside
 * {@link #onDataStored(Store, Data[])}, instead of calling it just once with an array of
 * {@link Data}.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link Trigger} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
public abstract class IndividualTrigger<T extends Data> extends Trigger<T> {

    public IndividualTrigger(Class<T> dataType) {
        super(dataType);
    }

    /**
     * Callback called after data is being added to the {@link Store}.
     * It will be called once for every data added.
     *
     * @param store reference to the {@link Store} the {@code data} is being added to.
     * @param data the individual data that has been added to the {@code store}.
     * @see #onDataStored(Store, Data[])
     */
    public abstract void onNewData(Store<T> store, T data);

    /**
     * Callback called after data is being added to the {@link Store}.
     * It will be call {@link #onNewData(Store, Data)} for every new data.
     *
     * @param store reference to the {@link Store} the {@code data} is being added to.
     * @param data the  data that has been added to the {@code store}.
     * @see #onNewData(Store, Data)
     */
    @Override
    public void onDataStored(Store<T> store, T... data) {
        for (T d:data)
            onNewData(store, d);
    }
}
