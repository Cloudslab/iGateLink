package org.cloudbus.foggatewaylib.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiMap<K, V> implements Map<K, V> {
    private Map<K, Set<V>> map;

    public MultiMap(){
        this.map = new HashMap<>();
    }

    @Override
    public int size() {
        int size = 0;
        for (Set<V> set:map.values())
            size += set.size();
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(@Nullable Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(@Nullable Object value) {
        for (Set<V> set:map.values())
            if (set.contains(value))
                return true;
        return false;
    }

    @Nullable
    @Deprecated
    @Override
    public V get(@Nullable Object key) {
        throw new UnsupportedOperationException("Use getAll(...) instead");
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
    @Override
    public V put(@NonNull K key, @NonNull V value) {
        Set<V> set = map.get(key);
        if (set == null){
            set = new HashSet<>();
            map.put(key, set);
        }
        set.add(value);
        return value;
    }

    @Nullable
    @Override
    public V remove(@Nullable Object key) {
        map.remove(key);
        return null;
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

    @Override
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        for (K key:m.keySet())
            put(key, m.get(key));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @NonNull
    @Override
    public Collection<V> values() {
        Collection<V> val = new HashSet<>();
        for (Set<V> set:map.values())
            val.addAll(set);
        return val;
    }

    @NonNull
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new HashSet<>();
        for (K key:map.keySet())
            for (V val:map.get(key))
                entries.add(new Entry<>(key, val));
        return entries;
    }

    class Entry<K, V> implements Map.Entry<K,V>{
        private K key;
        private V value;

        Entry(K key, V value){
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }
    }
}
