package org.cloudbus.foggatewaylib;

import androidx.annotation.NonNull;

import java.util.Collection;

public class LimitedSortedLinkedList<E extends Data> extends SortedLinkedList<E> {
    private int maxElements = 0;

    public void setMaxElements(int maxElements) {
        this.maxElements = maxElements;

    }

    public int getMaxElements() {
        return maxElements;
    }

    public void removeFirstN(int N) {
        for (int i = 0; i< N; i++)
            removeFirst();
    }

    private void removeExceeding(){
        if (maxElements > 0 && size() > maxElements){
            removeFirstN(size()-maxElements);
        }
    }

    @Override
    public boolean add(E e) {
        boolean result = super.add(e);
        removeExceeding();
        return result;
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends E> c) {
        boolean result = super.addAll(c);
        removeExceeding();
        return result;
    }
}
