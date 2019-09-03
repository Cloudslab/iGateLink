package org.cloudbus.foggatewaylib.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class SortedLinkedListTest {
    @Test
    public void add_isCorrect() {
        SortedLinkedList<Integer> list = new SortedLinkedList<>();
        int[] array = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        list.add(2);
        list.add(4);
        list.add(6);
        list.add(8);
        list.add(3);
        list.add(5);
        list.add(0);
        list.add(10);
        list.add(1);
        list.add(7);
        list.add(9);
        Integer[] intList = list.toArray(new Integer[0]);
        int[] outArray = new int[array.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = intList[i];
        Assert.assertArrayEquals(array, outArray);
    }

    @Test
    public void addAll_isCorrect() {
        SortedLinkedList<Integer> list = new SortedLinkedList<>();
        int[] array = new int[]{-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        list.addAll(Arrays.asList(new Integer[]{
                2,
                4,
                6,
        }));
        list.addAll(Arrays.asList(new Integer[]{
                8,
                3,
                5,
        }));
        list.addAll(Arrays.asList(new Integer[]{
                0,
                10,
                1,
        }));
        list.addAll(Arrays.asList(new Integer[]{
                7,
                9,
        }));
        list.addAll(Arrays.asList(new Integer[]{
                new Integer(-1),
                new Integer(-2),
        }));
        list.addAll(Arrays.asList(new Integer[]{
                12,
                11,
        }));
        Integer[] intList = list.toArray(new Integer[0]);
        int[] outArray = new int[array.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = intList[i];
        Assert.assertArrayEquals(array, outArray);
    }
}