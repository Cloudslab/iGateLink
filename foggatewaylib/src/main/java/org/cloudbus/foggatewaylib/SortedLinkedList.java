package org.cloudbus.foggatewaylib;

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

public class SortedLinkedList<E extends Data> implements Collection<E> {
    private LinkedList<E> list;

    public SortedLinkedList() {
        list = new LinkedList<>();
    }


    public SortedLinkedList(Collection<E> c) {
        list = new LinkedList<>(c);
    }


    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @SuppressWarnings("unchecked")
    private ListIterator<E> findPosition(@Nullable Object o) {
        if (o == null)
            return null;

        if (list.isEmpty())
            return list.listIterator();

        E e = (E) o;
        long id = e.getId();
        long lastId = list.getLast().getId();
        long firstId = list.getFirst().getId();

        if (id > lastId) {
            return list.listIterator(list.size());
        }
        if (id < firstId) {
            return list.listIterator(0);
        }

        ListIterator<E> iter;
        boolean asc;
        if ((lastId + firstId) / 2 > id) {
            iter = list.listIterator();
            asc = true;
        } else {
            iter = list.listIterator(list.size() - 1);
            asc = false;
        }

        while (true) {
            Data el;

            if (asc)
                el = iter.next();
            else
                el = iter.previous();

            if (el == null)
                throw new RuntimeException("Insert position not found, wtf?");

            long elId = el.getId();

            if (elId == id){
                if (asc)
                    iter.previous();
                return iter;
            } else if (!asc && elId < id) {
                iter.next();
                return iter;
            } else if (asc && elId > id) {
                iter.previous();
                return iter;
            }
        }
    }


    private int indexOfInsertion(@Nullable Object o) {
        ListIterator<E> iterator = findPosition(o);
        if (iterator != null)
            return iterator.nextIndex();
        else
            return -1;
    }

    private int indexOf(@Nullable Object o) {
        int i = indexOfInsertion(o);

        if (i < 0 || i >= list.size()) {
            return -1;
        } else if (list.get(i).getId() == ((Data) o).getId())
            return i;
        else
            return -1;
    }

    @Override
    public boolean contains(@Nullable Object o) {
        return indexOf(o) != -1;
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @NonNull
    public Iterator<E> descendingIterator() {
        return list.descendingIterator();
    }

    @Nullable
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(@Nullable T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(E e) {
        ListIterator<E> iterator = findPosition(e);
        if (iterator != null){
            iterator.add(e);
            return true;
        } else
            return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(@Nullable Object o) {
        ListIterator<E> iterator = findPosition(o);
        if (iterator != null && iterator.next().getId() == ((E)o).getId()){
            iterator.remove();
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        List<E> newList = new ArrayList<>(c);
        Collections.sort(newList);

        if (list.isEmpty())
            return list.addAll(newList);

        long lastId = list.getLast().getId();
        long firstId = list.getFirst().getId();

        if (newList.get(0).getId() > lastId) {
            return list.addAll(newList);
        }
        if (newList.get(newList.size() - 1).getId() < firstId) {
            ListIterator<E> iterator = newList.listIterator(newList.size());
            while (iterator.hasPrevious()){
                list.addFirst(iterator.previous());
            }
            return true;
        }

        ListIterator<E> iter = list.listIterator();

        for (E e:newList){
            long id = e.getId();
            while (true) {
                Data el;

                if (iter.hasNext())
                    el = iter.next();
                else{
                    iter.add(e);
                    break;
                }

                long elId = el.getId();

                if (elId >= id){
                    iter.previous();
                    iter.add(e);
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    public E getLast() {
        try {
            return list.getLast();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    public E getFirst() {
        try {
            return list.getFirst();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    public E removeFirst() {
        try {
            return list.removeFirst();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    public E removeLast() {
        try {
            return list.removeLast();
        } catch (NoSuchElementException e){
            return null;
        }
    }

    public List<E> subList(long from, long to) {
        return subList(new Data(from), new Data(to));
    }

    public List<E> subList(Data from, Data to) {
        int indexFrom = indexOfInsertion(from);
        int indexTo = indexOfInsertion(to);

        return list.subList(indexFrom, indexTo);
    }

    public List<E> subList(long from) {
        return subList(new Data(from));
    }

    public List<E> subList(Data from) {
        return subList(from, new Data(Long.MAX_VALUE));
    }

}