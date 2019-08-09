package org.cloudbus.foggatewaylib;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Collection of <code>Data</code> sorted by their <code>id</code>.
 * <p>
 * Inside the {@link ExecutionManager}, there may be different <code>Store</code>s of the same
 * <code>Data</code> since each store is identified by an <code>outputKey</code>,
 * which identifies the data flow it stores.
 *
 * @param <T> the <code>Data</code> type this <code>Store</code> stores.
 *
 * @author Riccardo Mancini
 */
public abstract class Store<T extends Data> {

    /**
     * Registered observers. Every observer is identified by a <code>key</code> for easier access.
     * They will be called on every call to {@link #store(Data[])}.
     *
     * @see #notifyObservers(Data[])
     * @see #addObserver(String, Observer)
     * @see #removeObserver(String)
     * @see Observer
     * @see Trigger
     */
    private Map<String, Observer<T>> observers;

    /**
     * Type of the stored elements.
     */
    private Class<T> dataType;


    /**
     * @param dataType the type of the elements that will be stored.
     */
    public Store(@NonNull Class<T> dataType){
        observers = new HashMap<>();
        this.dataType = dataType;
    }

    /**
     * @return type of the stored elements.
     */
    @NonNull
    public Class<T> getDataType() {
        return dataType;
    }

    /**
     * @return number of elements in this <code>Store</code>.
     */
    public abstract int size();

    /**
     * Get last element.
     *
     * @return last stored element or null if the <code>Store</code> is empty.
     * @see #retrieveLast(long)
     * @see #retrieveLastN(int)
     * @see #retrieveLastN(int, long)
     */
    @Nullable
    public abstract T retrieveLast();

    /**
     * Get last element with given request id.
     *
     * @param requestID the request id to look for.
     * @return last stored element with <code>requestID</code> request id or null if no element
     *         is found.
     * @see #retrieveLast()
     * @see #retrieveLastN(int)
     * @see #retrieveLastN(int, long)
     */
    @Nullable
    public abstract T retrieveLast(long requestID);

    /**
     * Get last N elements.
     *
     * @return array with the retrieved elements. The array may contain fewer elements than N if
     *         there are not enough elements in the <code>Store</code>. It can also be empty.
     * @see #retrieveLast()
     * @see #retrieveLast(long)
     * @see #retrieveLastN(int, long)
     */
    public abstract T[] retrieveLastN(int N);

    /**
     * Get last N elements with given request id.
     *
     * @param N the number of requests to
     * @param requestID the request id to look for.
     * @return array with the retrieved elements. The array may contain fewer elements than N if
     *         there are not enough elements in the <code>Store</code>. It can also be empty.
     * @see #retrieveLast()
     * @see #retrieveLast(long)
     * @see #retrieveLastN(int)
     */
    public abstract T[] retrieveLastN(int N, long requestID);

    /**
     * Retrieve all data in an interval of <code>id</code>s.
     *
     * @param from lower bound of the <code>id</code>s (included).
     * @param to upper bound of the <code>id</code>s (excluded).
     * @return array with the retrieved elements.
     * @see #retrieveInterval(long, long, long)
     * @see #retrieveIntervalFrom(long)
     * @see #retrieveIntervalFrom(long, long)
     */
    public abstract T[] retrieveInterval(long from, long to);

    /**
     * Retrieve all data with given request id in an interval of <code>id</code>s.
     *
     * @param from lower bound of the <code>id</code>s (included).
     * @param to upper bound of the <code>id</code>s (excluded).
     * @param requestID the request id to look for.
     * @return array with the retrieved elements.
     * @see #retrieveInterval(long, long, long)
     * @see #retrieveIntervalFrom(long)
     * @see #retrieveIntervalFrom(long, long)
     */
    public abstract T[] retrieveInterval(long from, long to, long requestID);

    /**
     * Retrieve all data with <code>id</code> greater than <code>from</code>.
     *
     * @param from lower bound of the <code>id</code>s (included).
     * @return array with the retrieved elements.
     * @see #retrieveInterval(long, long)
     * @see #retrieveInterval(long, long, long)
     * @see #retrieveIntervalFrom(long, long)
     */
    public T[] retrieveIntervalFrom(long from){
        return retrieveInterval(from, Long.MAX_VALUE);
    }

    /**
     * Retrieve all data with <code>id</code> greater than <code>from</code> and with given
     * request id.
     *
     * @param from lower bound of the <code>id</code>s (included).
     * @param requestID the request id to look for.
     * @return array with the retrieved elements.
     * @see #retrieveInterval(long, long)
     * @see #retrieveInterval(long, long, long)
     * @see #retrieveIntervalFrom(long)
     */
    public T[] retrieveIntervalFrom(long from, long requestID){
        return retrieveInterval(from, Long.MAX_VALUE, requestID);
    }

    /**
     * Inner implementation of the {@link #store(Data[])} method.
     *
     * @param data the data to be stored.
     * @see #store(Data[])
     */
    protected abstract void __store(T... data);

    /**
     * Calls callbacks of {@link Observer}s in {@link #observers}.
     *
     * @param data the data that has been stored.
     * @see Observer
     * @see Trigger
     * @see #store(Data[])
     * @see #observers
     * @see #addObserver(String, Observer)
     * @see #removeObserver(String)
     */
    protected void notifyObservers(T... data){
        for (Observer<T> observer:observers.values()){
            observer.onDataStored(this, data);
        }
    }

    /**
     * Stores given data. If no data is given. nothing will be done.
     * Every element must have the same {@link Data#request_id}.
     *
     * @param data data to be stored.
     * @see #__store(Data[])
     * @see #notifyObservers(Data[])
     */
    public void store(T... data){
        if (data.length == 0)
            return;
        long requestID = data[0].getRequestID();
        for (T t:data){
            if (t.getRequestID() != requestID)
                throw new RuntimeException("Every data must have the same request_id.");
        }

        __store(data);
        notifyObservers(data);
    }

    /**
     * Adds a {@link Observer} to the @{link #observers}.
     *
     * @param key the key for the new <code>observer</code> to use for later removal.
     * @param observer the {@link Observer} to be added to the @{link Store}.
     * @see Observer
     * @see Trigger
     * @see #observers
     * @see #removeObserver(String)
     * @see #notifyObservers(Data[])
     */
    public void addObserver(String key, Observer<T> observer){
        observers.put(key, observer);
    }

    /**
     * Removes the {@link Observer} identified by the given <code>key</code> from the
     * {@link #observers}.
     *
     * @param key the key for the new <code>observer</code> to use for later removal.
     * @return the removed {@link Observer} or null if it is not found.
     * @see Observer
     * @see Trigger
     * @see #observers
     * @see #addObserver(String, Observer)
     * @see #notifyObservers(Data[])
     */
    @Nullable
    public Observer<T> removeObserver(String key){
        return observers.remove(key);
    }

    /**
     * Simple interface for registering callbacks when a new element is added to a {@link Store}.
     *
     * @param <T> the type of {@link Data} in the {@link Store} that this {@link Observer} is
     *           observing.
     */
    public interface Observer<T extends Data> {

        /**
         * Callback called after data is being added to the {@link Store}.
         *
         * @param store reference to the {@link Store} the <code>data</code> is being added to.
         * @param data the data that has been added to the <code>store</code>.
         * @see Store#addObserver(String, Observer)
         * @see Store#removeObserver(String)
         * @see Store#notifyObservers(Data[])
         */
        void onDataStored(Store<T> store, T... data);
    }
}
