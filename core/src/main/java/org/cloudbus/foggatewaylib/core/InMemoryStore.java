package org.cloudbus.foggatewaylib.core;

import org.cloudbus.foggatewaylib.core.utils.SortedLinkedList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a {@link Store} that uses a custom extension of a {@link java.util.LinkedList},
 * {@link SortedLinkedList} for storing the data in memory.
 * <p>
 * Furthermore, for any request id, a {@link SortedLinkedList} links all the elements for that
 * request, speeding up time required to query data from the same request.
 * <p>
 * The choice to use a sorted linked list is to have O(1) insertion in the tail, since it is the
 * most common application scenario, while keeping the possibility of out-of-order insertion
 *
 * @param <E> the type of the elements stored.
 * @see SortedLinkedList
 *
 * @author Riccardo Mancini
 */
public class InMemoryStore<E extends Data> extends Store<E> {
    /**
     * List that stores all the data in this {@link InMemoryStore}.
     */
    private SortedLinkedList<E> dataList = new SortedLinkedList<>();

    /**
     * Lists that link all the data of the same request.
     */
    private Map<Long, SortedLinkedList<E>> dataRequestMap = new HashMap<>();

    /**
     * Maximum number of elements.
     * If this size is exceeded, elements will be removed from the head.
     * A value of 0 means that the store in unlimited.
     */
    private int maxElements;

    /**
     * Creates an unlimited store for elements of the given type.
     *
     * @param dataType the type of the elements stored.
     */
    public InMemoryStore(Class<E> dataType){
        super(dataType);
        maxElements = 0;
    }

    /**
     * Creates a limited store for elements of the given type.
     *
     * @param maxElements maximum number of elements that are allowed in this store.
     * @param dataType the type of the elements stored.
     * @see #maxElements
     */
    public InMemoryStore(int maxElements, Class<E> dataType){
        super(dataType);
        this.maxElements = maxElements;
    }

    /**
     * @see Store#retrieveLastN(int)
     */
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

    /**
     * @see Store#retrieveLastN(int, long)
     */
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

    /**
     * @see Store#retrieveLast()
     */
    @Override
    public E retrieveLast() {
        return dataList.getLast();
    }

    /**
     * @see Store#retrieveLast(long)
     */
    @Override
    public E retrieveLast(long requestID) {
        SortedLinkedList<E> requestList = dataRequestMap.get(requestID);
        if (requestList == null){
            return null;
        }

        return requestList.getLast();
    }

    /**
     * @see Store#retrieveInterval(long, long)
     */
    @Override
    @SuppressWarnings("unchecked")
    public E[] retrieveInterval(long from, long to) {
        List<E> subList = dataList.subList(from, to);
        return subList.toArray((E[]) Array.newInstance(getDataType(), subList.size()));
    }

    /**
     * @see Store#retrieveInterval(long, long, long)
     */
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

    /**
     * Stores new data in the {@link InMemoryStore}.
     * The elements are added to the {@link #dataList} and to the right request-specific list in
     * {@link #dataRequestMap}.
     * Finally exceeding elements are removed from both lists.
     *
     * @param data data to be stored
     * @see Store#__store(Data[])
     */
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

        if (maxElements != 0 && dataList.size() > maxElements){
            for (int i = 0; i < dataList.size() - maxElements; i++){
                E removedE = dataList.removeLast();
                SortedLinkedList<E> listRemoved = dataRequestMap.get(removedE.getRequestID());
                listRemoved.removeLast();
            }
        }
    }

    /**
     * Returns the number of elements in this {@link InMemoryStore}.
     *
     * @see Store#size()
     * @see SortedLinkedList#size()
     */
    @Override
    public int size() {
        return dataList.size();
    }
}
