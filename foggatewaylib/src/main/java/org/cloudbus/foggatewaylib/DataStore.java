package org.cloudbus.foggatewaylib;

import java.util.HashMap;
import java.util.Map;

public abstract class DataStore<T extends Data> {
    private Map<String, DataStoreObserver<T>> observers;
    Class<T> dataType;

    public DataStore(Class<T> dataType){
        observers = new HashMap<>();
        this.dataType = dataType;
    }

    public Class<T> getDataType() {
        return dataType;
    }

    public abstract T retrieveLast();
    public abstract T retrieveLast(long requestID);

    public abstract T[] retrieveLastN(int N);
    public abstract T[] retrieveLastN(int N, long requestID);

    public abstract T[] retrieveInterval(long from, long to);
    public abstract T[] retrieveInterval(long from, long to, long requestID);

    protected abstract void __store(T... data);

    public abstract int size();

    protected void notifyObservers(T... data){
        for (DataStoreObserver<T> observer: observers.values()){
            observer.onDataStored(this, data);
        }
    }

    public void store(T... data){
        __store(data);
        notifyObservers(data);
    }

    public void addObserver(String key, DataStoreObserver<T> observer){
        observers.put(key, observer);
    }

    public boolean removeObserver(String key){
        if (!observers.containsKey(key))
            return false;

        observers.remove(key);
        return true;
    }

    public T[] retrieveIntervalFrom(long from){
        return retrieveInterval(from, Long.MAX_VALUE);
    }
    public T[] retrieveIntervalFrom(long from, long requestID){
        return retrieveInterval(from, Long.MAX_VALUE, requestID);
    }

}