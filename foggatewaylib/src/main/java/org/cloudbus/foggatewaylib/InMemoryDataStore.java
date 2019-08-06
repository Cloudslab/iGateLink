package org.cloudbus.foggatewaylib;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class InMemoryDataStore<T extends Data> extends DataStore<T> {
    private LimitedSortedLinkedList<T> dataList = new LimitedSortedLinkedList<>();

    public InMemoryDataStore(Class<T> dataType){
        super(dataType);
    }

    public InMemoryDataStore(int max_elements, Class<T> dataType){
        super(dataType);
        dataList.setMaxElements(max_elements);
    }

    @Override
    public T[] retrieveLastN(int N) {
        Iterator<T> iter = dataList.descendingIterator();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < N && iter.hasNext(); i++){
            list.add(0, iter.next());
        }
        return list.toArray((T[]) Array.newInstance(getDataType(), list.size()));
    }

    //TODO inefficient
    @Override
    public T[] retrieveLastN(int N, long requestID) {
        Iterator<T> iter = dataList.descendingIterator();
        List<T> list = new ArrayList<>();
        for (int i = 0; i < N && iter.hasNext();){
            T e = iter.next();
            if (e.getRequestID() == requestID){
                list.add(0, iter.next());
                i++;
            }
        }
        return list.toArray((T[]) Array.newInstance(getDataType(), list.size()));
    }

    @Override
    public T retrieveLast() {
        return dataList.getLast();
    }

    //TODO inefficient
    @Override
    public T retrieveLast(long requestID) {
        Iterator<T> iter = dataList.descendingIterator();
        while (iter.hasNext()){
            T e = iter.next();
            if (e.getRequestID() == requestID){
                return e;
            }
        }
        return null;
    }

    @Override
    public T[] retrieveInterval(long from, long to) {
        List<T> subList = dataList.subList(from, to);
        return subList.toArray((T[]) Array.newInstance(getDataType(), subList.size()));
    }

    //TODO inefficient
    @Override
    public T[] retrieveInterval(long from, long to, long requestID) {
        List<T> subList = dataList.subList(from, to);
        List<T> filteredSubList = new ArrayList<>();
        for (T e:subList){
            if (e.getRequestID() == requestID){
                filteredSubList.add(e);
            }
        }
        return filteredSubList
                .toArray((T[]) Array.newInstance(getDataType(), filteredSubList.size()));
    }

    @Override
    public void __store(T... data) {
        dataList.addAll(Arrays.asList(data));
    }

    @Override
    public int size() {
        return dataList.size();
    }
}
