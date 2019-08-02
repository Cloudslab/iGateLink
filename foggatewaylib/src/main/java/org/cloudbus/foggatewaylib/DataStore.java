package org.cloudbus.foggatewaylib;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataStore<T extends Data> {
    private Map<Integer, DataStoreObserver<T> > observers;
    private int netxtObserverId = 0;

    public DataStore(){
        observers = new HashMap<>();
    }

    public abstract T retrieveLast();

    public abstract List<T> retrieveLastN(int N);

    public abstract List<T> retrieveInterval(Date from, Date to);

    protected abstract void __store(List<T> data);

    public abstract int size();

    protected void notifyObservers(List<T> data){
        Log.d("DataStore", "Notifying observers");
        for (DataStoreObserver<T> observer:observers.values()){
            observer.onDataStored(this, data);
        }
    }

    public void store(List<T> data){
        __store(data);
        notifyObservers(data);
    }

    public void store(T data){
        List<T> list = new ArrayList<T>();
        list.add(data);
        store(list);
    }

    public int addObserver(DataStoreObserver<T> observer){
        observers.put(netxtObserverId, observer);
        return netxtObserverId++;
    }

    public void removeObserver(int observerId){
        observers.remove(observerId);
    }

}