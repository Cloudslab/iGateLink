package org.cloudbus.foggatewaylib.core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implementation of a sorted linked list, based on the {@link LinkedList}.
 * Elements are sorted in ascending order according to their {@link Comparable} interface.
 * Despite the name, this class does not have the {@link List} interface since some methods would
 * not make sense. Anyways, some more advanced methods are present from {@link java.util.Deque},
 * like {@link #getFirst()}, {@link #getLast()}, {@link #removeFirst()}, {@link #removeLast()},
 * and other methods from {@link List}, like {@link #descendingIterator()}.
 * {@code null} elements are not allowed.
 *
 * @param <E> the type of the elements
 * @see LinkedList
 *
 * @author Riccardo Mancini
 */
public class SortedLinkedList<E extends Comparable> implements Collection<E> {
    protected LinkedList<E> list;

    /**
     * Default constructor that creates an empty list.
     */
    public SortedLinkedList() {
        list = new LinkedList<>();
    }


    /**
     * Constructor that creates a list containing all elements in the {@link Collection} {@code c}.
     *
     * @param c the initial elements of the list.
     */
    public SortedLinkedList(Collection<E> c) {
        list = new LinkedList<>();
        addAll(c);
    }


    /**
     * Gets the number of elements in the list.
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Returns {@code true} if the list is empty, {@code false} otherwise.
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Finds the position in which the element {@code o} should be added to preserve the sorting
     * or the position of {@code o} in the list if it is already present.
     * This operation has a cost of O(1) in case the element can is last or first,
     * O(N) otherwise.
     *
     * @param o the element to find the position of insertion of.
     * @return a {@link ListIterator} on the position or {@code null} if {@code o} is {@code null}.
     * @see LinkedList#listIterator()
     */
    @SuppressWarnings("unchecked")
    protected ListIterator<E> findPosition(@Nullable Object o) {
        if (o == null)
            return null;

        if (list.isEmpty())
            return list.listIterator();

        E e = (E) o;

        if (e.compareTo(list.getLast()) > 0) {
            return list.listIterator(list.size());
        }
        if (e.compareTo(list.getFirst()) < 0) {
            return list.listIterator(0);
        }

        ListIterator<E> iter = list.listIterator(list.size() - 1);

        while (true) {
            E el;

            el = iter.previous();

            if (el == null)
                throw new RuntimeException("Insert position not found, wtf?");

            if (el.compareTo(e) == 0){
                return iter;
            } else if (el.compareTo(e) < 0) {
                iter.next();
                return iter;
            }
        }
    }

    /**
     * Finds the position in which the element {@code o} should be added to preserve the sorting
     * or the position of {@code o} in the list if it is already present.
     * This operation has a cost of O(1) in case the element can is last or first,
     * O(N) otherwise.
     *
     * @param o the element to find the position of insertion of.
     * @return the index at which the element could be inserted or {@code -1} if {@code o} is
     *         {@code null}.
     * @see #findPosition(Object)
     */
    protected int indexOfInsertion(@Nullable Object o) {
        ListIterator<E> iterator = findPosition(o);
        if (iterator != null)
            return iterator.nextIndex();
        else
            return -1;
    }

    /**
     * Finds the position of an element in the list.
     * This operation has a cost of O(1) in case the element can is last or first,
     * O(N) otherwise.
     *
     * @param o the element to look for in the list.
     * @return the index of the element or {@code -1} if the element was not found.
     * @see #findPosition(Object)
     */
    protected int indexOf(@Nullable Object o) {
        int i = indexOfInsertion(o);

        if (i < 0 || i >= list.size()) {
            return -1;
        } else if (list.get(i).equals(o))
            return i;
        else
            return -1;
    }

    /**
     * Returns {@code true} if the given element is present in the list, {@code false} otherwise.
     * This operation has a cost of O(1) in case the element can is last or first,
     * O(N) otherwise.
     *
     * @param o the element to look for in the list.
     * @return {@code true} if the element was found, {@code false} otherwise.
     * @see #indexOf(Object)
     */
    @Override
    public boolean contains(@Nullable Object o) {
        return indexOf(o) < 0;
    }


    /**
     * Returns an iterator over the elements of the list in ascending order.
     *
     * @return the iterator.
     * @see LinkedList#iterator()
     */
    @NonNull
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    /**
     * Returns a iterator over the elements of the list in descending order.
     *
     * @return the iterator.
     * @see LinkedList#descendingIterator()
     */
    @NonNull
    public Iterator<E> descendingIterator() {
        return list.descendingIterator();
    }

    /**
     * Returns an array of the elements in the list.
     *
     * @return the array.
     * @see LinkedList#toArray()
     */
    @Nullable
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    /**
     * Returns an array of the elements in the list.
     *
     * @param a an array of the same type as the elements of the list.
     * @param <T> the type of elements.
     * @return the array.
     * @see LinkedList#toArray(Object[])
     */
    @Override
    public <T> T[] toArray(@Nullable T[] a) {
        return list.toArray(a);
    }

    /**
     * Adds an element to the list.
     * It will be added in the correct position to preserve the sorting.
     * This operation has a cost of O(1) in case the element can be added as last or first,
     * O(N) otherwise.
     *
     * @param e the element to be added.
     * @return {@code true} if the element was added, {@code false} otherwise (e.g. the element
     *         is {@code null}).
     * @see #findPosition(Object)
     */
    @Override
    public boolean add(E e) {
        ListIterator<E> iterator = findPosition(e);
        if (iterator != null){
            iterator.add(e);
            return true;
        } else
            return false;
    }

    /**
     * Removes an element from the list.
     * This operation has a cost of O(1) in case the element can is last or first,
     * O(N) otherwise.
     *
     * @param o the element to be removed.
     * @return {@code true} if the element was removed, {@code false} otherwise.
     * @see #findPosition(Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(@Nullable Object o) {
        ListIterator<E> iterator = findPosition(o);
        if (iterator != null && iterator.next().equals(o)){
            iterator.remove();
            return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if all the elements in the given {@link Collection} are present in
     * the list.
     *
     * @param c the elements to be checked.
     * @return {@code true} if all the elements are contained, {@code false} otherwise.
     * @see LinkedList#containsAll(Collection)
     */
    //TODO it could be optimized to take advantage of the sorting.
    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    /**
     * Adds all the given elements to the list.
     * The elements are first sorted and then added one by one to the list, iterating over it in
     * ascending order.
     * If a {@code null} element is found, it is ignored.
     *
     * @param c the elements to be added.
     * @return {@code true} if all the elements have been added, {@code false} otherwise (e.g.
     *         some elements in {@code c} are {@code null}).
     */
    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        List<E> newList = new ArrayList<>(c);
        Collections.sort(newList);

        if (list.isEmpty())
            return list.addAll(newList);

        if (newList.get(0).compareTo(list.getLast()) > 0) {
            return list.addAll(newList);
        }
        if (newList.get(newList.size() - 1).compareTo(list.getFirst()) < 0) {
            ListIterator<E> iterator = newList.listIterator(newList.size());
            while (iterator.hasPrevious()){
                list.addFirst(iterator.previous());
            }
            return true;
        }

        ListIterator<E> iter = list.listIterator();
        boolean result = true;

        for (E e:newList){
            if (e == null){
                result = false;
                continue;
            }

            while (true) {
                E el;

                if (iter.hasNext())
                    el = iter.next();
                else{
                    iter.add(e);
                    break;
                }

                if (el.compareTo(e) >= 0){
                    iter.previous();
                    iter.add(e);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Removes all the given elements from the list.
     *
     * @param c the elements to be removed.
     * @return refer to {@link LinkedList#removeAll(Collection) }
     * @see LinkedList#removeAll(Collection)
     */
    //TODO it could be optimized to take advantage of the sorting.
    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return list.removeAll(c);
    }

    /**
     * Retains all the given elements in the list.
     *
     * @param c the elements to be retained.
     * @return refer to {@link LinkedList#removeAll(Collection) }
     * @see LinkedList#retainAll(Collection)
     */
    //TODO it could be optimized to take advantage of the sorting.
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return list.retainAll(c);
    }

    /**
     * Removes all the elements from the list.
     *
     * @see LinkedList#clear()
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * Gets the last element in the list.
     *
     * @return last element or {@code null} if the list is empty
     */
    @Nullable
    public E getLast() {
        try {
            return list.getLast();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    /**
     * Gets the first element in the list.
     *
     * @return first element or {@code null} if the list is empty
     */
    @Nullable
    public E getFirst() {
        try {
            return list.getFirst();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    /**
     * Removes the first element in the list.
     *
     * @return removed element or {@code null} if the list is empty
     */
    @Nullable
    public E removeFirst() {
        try {
            return list.removeFirst();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    /**
     * Removes the last element in the list.
     *
     * @return removed element or {@code null} if the list is empty
     */
    @Nullable
    public E removeLast() {
        try {
            return list.removeLast();
        } catch (NoSuchElementException e){
            return null;
        }
    }
}