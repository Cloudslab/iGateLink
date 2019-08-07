package org.cloudbus.foggatewaylib;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InMemoryDataStore<E extends Data> extends DataStore<E> {
    private SortedLinkedList<E> dataList = new SortedLinkedList<>();
    private Map<Long, SortedLinkedList<E>> dataRequestMap = new HashMap<>();

    private int maxElements;

    public InMemoryDataStore(Class<E> dataType){
        super(dataType);
    }

    public InMemoryDataStore(int maxElements, Class<E> dataType){
        super(dataType);
        this.maxElements = maxElements;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] retrieveLastN(int N) {
        Iterator<E> iter = dataList.descendingIterator();
        List<E> list = new ArrayList<>();
        for (int i = 0; i < N && iter.hasNext(); i++){
            list.add(0, iter.next());
        }
        return list.toArray((E[]) Array.newInstance(getDataType(), list.size()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] retrieveLastN(int N, long requestID) {
        SortedLinkedList<E> requestList = dataRequestMap.get(requestID);
        if (requestList == null){
            return (E[]) Array.newInstance(getDataType());
        }

        Iterator<E> iter = requestList.descendingIterator();
        List<E> list = new ArrayList<>();
        for (int i = 0; i < N && iter.hasNext(); i++){
            list.add(0, iter.next());
        }
        return list.toArray((E[]) Array.newInstance(getDataType(), list.size()));
    }

    @Override
    public E retrieveLast() {
        return dataList.getLast();
    }

    @Override
    public E retrieveLast(long requestID) {
        SortedLinkedList<E> requestList = dataRequestMap.get(requestID);
        if (requestList == null){
            return null;
        }

        return requestList.getLast();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] retrieveInterval(long from, long to) {
        List<E> subList = dataList.subList(from, to);
        return subList.toArray((E[]) Array.newInstance(getDataType(), subList.size()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] retrieveInterval(long from, long to, long requestID) {
        SortedLinkedList<E> requestList = dataRequestMap.get(requestID);
        if (requestList == null){
            return (E[]) Array.newInstance(getDataType());
        }

        List<E> subList = requestList.subList(from, to);
        return subList.toArray((E[]) Array.newInstance(getDataType(), subList.size()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void __store(E... data) {
        if (data.length == 0)
            return;

        List<E> newData = Arrays.asList(data);
        dataList.addAll(newData);

        SortedLinkedList<E> requestList = dataRequestMap.get(data[0].getRequestID());
        if (requestList == null){
            requestList = new SortedLinkedList<>();
            dataRequestMap.put(data[0].getRequestID(), requestList);
        }
        requestList.addAll(newData);

        if (dataList.size() > maxElements){
            for (int i = 0; i < dataList.size() - maxElements; i++){
                E removedE = dataList.removeLast();
                SortedLinkedList<E> listRemoved = dataRequestMap.get(removedE.getRequestID());
                listRemoved.removeLast();
            }
        }
    }

    @Override
    public int size() {
        return dataList.size();
    }
}
