package org.cloudbus.foggatewaylib;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class InMemoryDataStore<T extends Data> extends DataStore<T> {
    private TreeMap<Date, T> dataMap = new TreeMap<>();
    private int max_elements;

    public InMemoryDataStore(){
        super();
        max_elements = 0;
    }

    public InMemoryDataStore(int max_elements){
        super();
        this.max_elements = max_elements;
    }

    @Override
    public List<T> retrieveLastN(int N) {
        Iterator<Map.Entry<Date, T>> iter = dataMap.descendingMap().entrySet().iterator();
        List<T> dataList = new ArrayList<>();
        for (int i = 0; i < N && iter.hasNext(); i++){
            dataList.add(iter.next().getValue());
        }
        return dataList;
    }

    @Override
    public T retrieveLast() {
        if (dataMap.size() > 0)
            return dataMap.lastEntry().getValue();
        else
            return null;
    }

    @Override
    public List<T> retrieveInterval(final Date from, final Date to) {
        return new ArrayList<>(dataMap.subMap(from, to).values());

    }

    @Override
    public void __store(List<T> dataList) {
        for (T data:dataList){
            dataMap.put(data.getTimestamp(), data);
        }
        if (max_elements > 0 && size() > max_elements){
            removeFirstN(size() - max_elements);
        }
    }

    @Override
    public int size() {
        return dataMap.size();
    }

    public void removeFirstN(int N){
        for (int i = 0; i < N; i++)
            dataMap.pollFirstEntry();
    }
}
