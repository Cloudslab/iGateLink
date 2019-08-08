package org.cloudbus.foggatewaylib;

/**
 * Simple interface for registering callbacks when a new element is added to a {@link Store}.
 *
 * @param <T> the type of {@link Data} in the {@link Store} that this {@link StoreObserver} is
 *           observing.
 *
 * @author Riccardo Mancini
 */
//TODO consider making this a subclass of Store
public interface StoreObserver<T extends Data> {

    /**
     * Callback called after data is being added to the {@link Store}.
     *
     * @param store reference to the {@link Store} the <code>data</code> is being added to.
     * @param data the data that has been added to the <code>store</code>.
     * @see Store#addObserver(String, StoreObserver)
     * @see Store#removeObserver(String)
     * @see Store#notifyObservers(Data[])
     */
    void onDataStored(Store<T> store, T... data);
}
