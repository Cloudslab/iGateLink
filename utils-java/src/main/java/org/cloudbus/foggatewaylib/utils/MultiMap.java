package org.cloudbus.foggatewaylib.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link Map} that stores for every key a {@link Set} of values.
 * The interface of this class is very similar to {@link Map} with the only difference that you
 * can't {@link Map#get(Object)} a single value but you can only {@link #getAll(Object)} values
 * stored for the given key.
 * Furthermore, the method {@link #findValue(Object)} is added.
 *
 * @param <K> type of the keys.
 * @param <V> type of the values in the {@link Set}s.
 * @see Map
 * @see Set
 *
 * @author Riccardo Mancini
 */
public class MultiMap<K, V> {
    private Map<K, Set<V>> map;

    /**
     * Default constructor.
     * Initializes the inner {@link Map} of {@link Set}s.
     */
    public MultiMap(){
        this.map = new HashMap<>();
    }

    /**
     * Gets total number of values stored in the {@link MultiMap}.
     *
     * @return the sum of the sizes of all the {@link Set}s in the {@link MultiMap}.
     * @see #isEmpty()
     */
    public int size() {
        int size = 0;
        for (Set<V> set:map.values())
            size += set.size();
        return size;
    }

    /**
     * Returns {@code true} if there are no keys inside the map or if all the sets associated
     * with the keys are empty.
     *
     * @return {@code true} if {@link #size()} is 0, {@code false} otherwise.
     * @see #size()
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns {@code true} if the specified key is present in the {@link MultiMap}.
     * NB: This does not imply that there are values associated with that key. In fact, the
     * {@link Set} associated with it may be empty.
     *
     * @param key the key to look for.
     * @return {@code true} if the key is present in the {@link MultiMap}, {@code false }otherwise.
     */
    public boolean containsKey(@Nullable Object key) {
        return map.containsKey(key);
    }

    /**
     * Returns {@code true} if the specified value is present in any of the {@link Set}s within
     * this {@link MultiMap}
     *
     * @param value the value to look for.
     * @return {@code true} if the value is found, {@code false} otherwise.
     */
    public boolean containsValue(@Nullable Object value) {
        for (Set<V> set:map.values())
            if (set.contains(value))
                return true;
        return false;
    }

    /**
     * Returns all the values associated with the given key.
     *
     * @param key the key associated with the values to return.
     * @return the {@link Set} of values or an empty {@link Set} if the key was not found.
     */
    @NonNull
    public Set<V> getAll(@Nullable Object key) {
        Set<V> set = map.get(key);
        if (set == null)
            return new HashSet<>();
        else
            return set;
    }

    /**
     * Returns the {@code key}s associated with a given value.
     *
     * @param value the value to look for.
     * @return a {@link Set} containing the {@code key}s associated with that value or an empty
     *         {@link Set} if the given value was not found.
     */
    public Set<K> findValue(@Nullable Object value) {
        Set<K> keys = new HashSet<>();
        for (K key:map.keySet()){
            if (map.get(key).contains(value))
                keys.add(key);
        }
        return keys;
    }

    /**
     * Inserts a key-value pair in the {@link MultiMap}.
     * If the key was not already present, it is added and a {@link Set} containing only the given
     * value is associated with it.
     * If the key was already present, the given value is added to the {@link Set} associated with
     * the given key.
     * NB: if the {@code value} is already present in the {@link Set} associated with the
     * {@code key}, this method will do nothing.
     *
     * @param key the key to be added.
     * @param value the value to be added.
     * @return {@code true} if the {@code value} has been added, {@code false} if it was already
     *         present in the {@link Set} associated with the {@code key}.
     */
    @Nullable
    public boolean put(@NonNull K key, @NonNull V value) {
        Set<V> set = map.get(key);
        if (set == null){
            set = new HashSet<>();
            map.put(key, set);
        }
        return set.add(value);
    }

    /**
     * Removes the given key-value pair from the {@link MultiMap}.
     * The {@code value} is removed if and only if it is present in the {@link Set} associated
     * with the given {@code key}.
     *
     * @param key the {@code key} to be removed.
     * @param value the {@code value} to be removed.
     * @return {@code true} if the given key-value pair was found, {@code false} otherwise.
     * @see Map#remove(Object)
     */
    public boolean remove(@Nullable Object key, @Nullable Object value) {
        Set<V> values = getAll(key);
        return values.remove(value);
    }

    /**
     * Removes the given {@code key} from the {@link MultiMap} and all the values associated with
     * it.
     *
     * @param key the {@code key} to be removed.
     * @return the {@link Set} of values associated with the {@code key} or {@code null} if the
     *         {@code key} was not found.
     * @see Map#remove(Object)
     */
    public Set<V> removeAll(@Nullable Object key) {
        return map.remove(key);
    }

    /**
     * Removes the given {@code value} from the {@link MultiMap}, independently on the {@code key}
     * associated with it.
     *
     * @param key the {@code key} to be removed.
     * @return the {@link Set} of values associated with the {@code key} or {@code null} if the
     *         {@code key} was not found.
     * @see Map#remove(Object)
     */
    @Nullable
    public Set<K> removeValue(@Nullable Object value) {
        Set<K> keys = findValue(value);
        for (K key:keys){
            map.get(key).remove(value);
        }
        return keys;
    }

    /**
     * Adds the given {@code values} in the {@link Set} associated with the given {@code key}.
     *
     * @param key the {@code key} associated with the {@link Set} in which we want to add the new
     *            {@code values}.
     * @param values the {@code values} to be added.
     */
    public void putAll(@NonNull K key, @NonNull Collection<V> values) {
        Set<V> set = map.get(key);
        if (set == null){
            set = new HashSet<>();
            map.put(key, set);
        }
        set.addAll(values);
    }

    /**
     * Adds the given key-value pairs in the {@link MultiMap}.
     *
     * @param m the {@link Map} containing all the key-value pairs to be added.
     */
    public void putAll(@NonNull Map<? extends K, ? extends V> m) {
        for (K key:m.keySet())
            put(key, m.get(key));
    }

    /**
     * Removes all {@code key}s and {@code value}s in this {@link MultiMap}.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns the {@link Set} of the {@code key}s of this {@link MultiMap}.
     *
     * @return the {@link Set} of keys.
     */
    @NonNull
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Returns all the values in the {@link MultiMap}.
     * NB: duplicates could be returned if they are associated with different keys.
     *
     * @return a {@link Collection} of all the values in the {@link MultiMap} (duplicates are
     *         possible).
     */
    @NonNull
    public Collection<V> values() {
        Collection<V> val = new ArrayList<>();
        for (Set<V> set:map.values())
            val.addAll(set);
        return val;
    }

    /**
     * Returns all the unique values in the {@link MultiMap}.
     *
     * @return a {@link Set} of all the values in the {@link MultiMap}.
     */
    @NonNull
    public Set<V> uniqueValues() {
        Set<V> val = new HashSet<>();
        for (Set<V> set:map.values())
            val.addAll(set);
        return val;
    }
}
