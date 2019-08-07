package org.cloudbus.foggatewaylib.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiMap<K, V> {
    private Map<K, Set<V>> map;

    public MultiMap(){
        this.map = new HashMap<>();
    }

    public int size() {
        int size = 0;
        for (Set<V> set:map.values())
            size += set.size();
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(@Nullable Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(@Nullable Object value) {
        for (Set<V> set:map.values())
            if (set.contains(value))
                return true;
        return false;
    }

    @NonNull
    public Set<V> getAll(@Nullable Object key) {
        Set<V> set = map.get(key);
        if (set == null)
            return new HashSet<>();
        else
            return set;
    }

    @Nullable
    public K findValue(@Nullable Object value) {
        for (K key:map.keySet())
            if (map.get(key).contains(value))
                return key;
        return null;
    }

    @Nullable
    public V put(@NonNull K key, @NonNull V value) {
        Set<V> set = map.get(key);
        if (set == null){
            set = new HashSet<>();
            map.put(key, set);
        }
        set.add(value);
        return value;
    }

    public void removeAll(@Nullable Object key) {
        map.remove(key);
    }


    @Nullable
    public K removeValue(@Nullable Object value) {
        K key = findValue(value);
        if (key == null)
            return null;
        else{
            map.get(key).remove(value);
            return key;
        }
    }

    public void putAll(@NonNull K key, @NonNull Collection<V> values) {
        Set<V> set = map.get(key);
        if (set == null){
            set = new HashSet<>();
            map.put(key, set);
        }
        set.addAll(values);
    }

    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        for (K key:m.keySet())
            put(key, m.get(key));
    }

    public void clear() {
        map.clear();
    }

    @NonNull
    public Set<K> keySet() {
        return map.keySet();
    }

    @NonNull
    public Collection<V> values() {
        Collection<V> val = new ArrayList<>();
        for (Set<V> set:map.values())
            val.addAll(set);
        return val;
    }
}
