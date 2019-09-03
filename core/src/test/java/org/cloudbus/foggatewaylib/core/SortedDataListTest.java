package org.cloudbus.foggatewaylib.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class SortedDataListTest {



    @Test
    public void sublistFrom_isCorrect() {
        int[] array = new int[]{10, 11, 12};
        SortedDataList<Data> list = new SortedDataList<>(Arrays.asList(new Data[]{
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
        SortedDataList<Data> list = new SortedDataList<>(Arrays.asList(new Data[]{
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
        SortedDataList<Data> list = new SortedDataList<>(Arrays.asList(new Data[]{
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