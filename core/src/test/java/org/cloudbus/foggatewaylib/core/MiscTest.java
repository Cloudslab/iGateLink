package org.cloudbus.foggatewaylib.core;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;

public class MiscTest {

    int[] reverse(int... a){
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++){
            b[a.length - i - 1] = a[i];
        }
        return b;
    }

    private static <T> T[] convert(Class<T> cls, Data... array){
        return Arrays.asList(array)
                .toArray((T[]) Array.newInstance(cls, array.length));
    }

    @Test
    public void varargs_test() {
        int[] r1 = reverse(reverse(1));
        assertArrayEquals(new int[]{1}, r1);

        int[] r2 = reverse(reverse(1, 2, 3));
        assertArrayEquals(new int[]{1, 2, 3}, r2);

        int[] r3 = reverse(reverse(r2));
        assertArrayEquals(new int[]{1, 2, 3}, r3);
    }

    public class HugeData extends Data{
        private int[] array;

        HugeData(){
            array = new int[1000000];
        }
    }


    @Test
    public void conversion_memorytest() {
        Runtime instance = Runtime.getRuntime();
        long mb = 1024*1024;
        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);

        HugeData[] hugeData = new HugeData[100];

        for (int i = 0; i < hugeData.length; i++)
            hugeData[i] = new HugeData();

        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);

        HugeData[] datas1 = convert(HugeData.class, hugeData);

        System.out.println("Used Memory: "
                + (instance.totalMemory() - instance.freeMemory()) / mb);

        for (int i = 0; i < hugeData.length; i++)
            assertSame(datas1[i], hugeData[i]);
    }
}