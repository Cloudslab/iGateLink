package org.cloudbus.foggatewaylib.core;

import org.cloudbus.foggatewaylib.utils.SortedLinkedList;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Extension of the {@link SortedLinkedList} class that enables splicing with given id.
 *
 * @param <E> the type of the data stored in the list
 *
 * @author Riccardo Mancini
 */
public class SortedDataList<E extends Data> extends SortedLinkedList<E> {

    /**
     * Constructs an empty list.
     */
    public SortedDataList() {
    }

    /**
     * Constructs a new list with the given elements.
     */
    public SortedDataList(Collection<E> c) {
        super(c);
    }

    /**
     * Returns a sublist with all the elements with {@link Data#id} between {@code from} (included)
     * and {@code to} (excluded).
     *
     * @param from lower bound (included)
     * @param to upper bound (excluded)
     * @return the sublist.
     * @see LinkedList#subList(int, int)
     * @see #subList(Data, Data)
     * @see #subList(long)
     * @see #subList(Data)
     */
    public List<E> subList(long from, long to) {
        return subList(new Data(from), new Data(to));
    }

    /**
     * Returns a sublist with all the elements with {@link Data#id} between {@code from} (included)
     * and {@code to} (excluded).
     *
     * @param from lower bound (included)
     * @param to upper bound (excluded)
     * @return the sublist.
     * @see LinkedList#subList(int, int)
     * @see #subList(long, long)
     * @see #subList(long)
     * @see #subList(Data)
     */
    public List<E> subList(Data from, Data to) {
        int indexFrom = indexOfInsertion(from);
        int indexTo = indexOfInsertion(to);

        return list.subList(indexFrom, indexTo);
    }

    /**
     * Returns a sublist with all the elements with {@link Data#id} higher than or equal to
     * {@code from} (included).
     *
     * @param from lower bound (included)
     * @return the sublist.
     * @see LinkedList#subList(int, int)
     * @see #subList(long, long)
     * @see #subList(Data, Data)
     * @see #subList(Data)
     */
    public List<E> subList(long from) {
        return subList(new Data(from));
    }

    /**
     * Returns a sublist with all the elements with {@link Data#id} higher than or equal to
     * {@code from} (included).
     *
     * @param from lower bound (included)
     * @return the sublist.
     * @see LinkedList#subList(int, int)
     * @see #subList(long, long)
     * @see #subList(Data, Data)
     * @see #subList(long)
     */
    public List<E> subList(Data from) {
        return subList(from, new Data(Long.MAX_VALUE));
    }

}
