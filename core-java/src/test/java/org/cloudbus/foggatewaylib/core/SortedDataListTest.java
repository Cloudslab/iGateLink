package org.cloudbus.foggatewaylib.core;

import org.junit.Assert;
import org.junit.Test;

public class SortedDataListTest {

    private SortedDataList<Data> makeList(long from, long to, long step){
        SortedDataList<Data> list = new SortedDataList<>();
        for (long i = from; i < to; i += step)
            list.add(new Data(i));
        return list;
    }

    private SortedDataList<GenericData> makeGenericDataList(long from, long to, long step){
        SortedDataList<GenericData> list = new SortedDataList<>();
        for (long i = from; i < to; i += step)
            list.add(new GenericData<>(i, i));
        return list;
    }

    @Test
    public void subList_long_long_isCorrect() {
        SortedDataList<Data> list = makeList(0, 11, 1);
        long[] array = new long[]{3, 4, 5, 6, 7};
        Data[] dataArray = list.subList(3,8).toArray(new Data[0]);
        long[] outArray = new long[array.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = dataArray[i].getId();
        Assert.assertArrayEquals(array, outArray);
    }

    @Test
    public void subList_data_data_isCorrect() {
        SortedDataList<GenericData> list = makeGenericDataList(0, 11, 1);
        long[] array = new long[]{3, 4, 5, 6};
        Data[] dataArray = list.subList(new Data(3), new Data(7)).toArray(new Data[0]);
        long[] outArray = new long[array.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = dataArray[i].getId();
        Assert.assertArrayEquals(array, outArray);
    }
}