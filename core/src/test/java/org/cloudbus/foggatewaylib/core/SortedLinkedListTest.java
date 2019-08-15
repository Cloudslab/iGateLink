package org.cloudbus.foggatewaylib.core;

import org.cloudbus.foggatewaylib.core.utils.SortedLinkedList;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class SortedLinkedListTest {
    @Test
    public void add_isCorrect() {
        SortedLinkedList<Data> list = new SortedLinkedList<>();
        int[] array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        list.add(new Data(2));
        list.add(new Data(4));
        list.add(new Data(6));
        list.add(new Data(8));
        list.add(new Data(3));
        list.add(new Data(5));
        list.add(new Data(0));
        list.add(new Data(10));
        list.add(new Data(1));
        list.add(new Data(7));
        list.add(new Data(9));
        Data[] dataList = list.toArray(new Data[0]);
        int[] outArray = new int[array.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = (int) dataList[i].getId();
        assertArrayEquals(array, outArray);
    }

    @Test
    public void addAll_isCorrect() {
        SortedLinkedList<Data> list = new SortedLinkedList<>();
        int[] array = new int[]{-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        list.addAll(Arrays.asList(new Data[]{
                new Data(2),
                new Data(4),
                new Data(6),
        }));
        list.addAll(Arrays.asList(new Data[]{
                new Data(8),
                new Data(3),
                new Data(5),
        }));
        list.addAll(Arrays.asList(new Data[]{
                new Data(0),
                new Data(10),
                new Data(1),
        }));
        list.addAll(Arrays.asList(new Data[]{
                new Data(7),
                new Data(9),
        }));
        list.addAll(Arrays.asList(new Data[]{
                new Data(-1),
                new Data(-2),
        }));
        list.addAll(Arrays.asList(new Data[]{
                new Data(12),
                new Data(11),
        }));
        Data[] dataList = list.toArray(new Data[0]);
        int[] outArray = new int[array.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = (int) dataList[i].getId();
        assertArrayEquals(array, outArray);
    }


    @Test
    public void sublistFrom_isCorrect() {
        int[] array = new int[]{10, 11, 12};
        SortedLinkedList<Data> list = new SortedLinkedList<>(Arrays.asList(new Data[]{
                new Data(0),
                new Data(1),
                new Data(2),
                new Data(3),
                new Data(4),
                new Data(5),
                new Data(6),
                new Data(7),
                new Data(8),
                new Data(9),
                new Data(10),
                new Data(11),
                new Data(12),
        }));
        List<Data> outList = list.subList(10);
        Data[] dataList = outList.toArray(new Data[0]);
        int[] outArray = new int[dataList.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = (int) dataList[i].getId();
        assertArrayEquals(array, outArray);
    }


    @Test
    public void sublistFromTo_isCorrect() {
        int[] array = new int[]{2, 3, 4, 5};
        SortedLinkedList<Data> list = new SortedLinkedList<>(Arrays.asList(new Data[]{
                new Data(0),
                new Data(1),
                new Data(2),
                new Data(3),
                new Data(4),
                new Data(5),
                new Data(6),
                new Data(7),
                new Data(8),
                new Data(9),
                new Data(10),
                new Data(11),
                new Data(12),
        }));
        List<Data> outList = list.subList(2, 6);
        Data[] dataList = outList.toArray(new Data[0]);
        int[] outArray = new int[dataList.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = (int) dataList[i].getId();
        assertArrayEquals(array, outArray);
    }


    @Test
    public void sublistFromToGap_isCorrect() {
        int[] array = new int[]{2, 4};
        SortedLinkedList<Data> list = new SortedLinkedList<>(Arrays.asList(new Data[]{
                new Data(0),
                new Data(2),
                new Data(4),
                new Data(6),
                new Data(8),
                new Data(10),
                new Data(12),
        }));
        List<Data> outList = list.subList(1, 5);
        Data[] dataList = outList.toArray(new Data[0]);
        int[] outArray = new int[dataList.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = (int) dataList[i].getId();
        assertArrayEquals(array, outArray);
    }
}