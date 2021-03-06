package org.cloudbus.foggatewaylib.core;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class InMemoryStoreTest {

    private long[] dataToLong(Data[] dataList){
        long[] outArray = new long[dataList.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = dataList[i].getId();
        return outArray;
    }

    @Test
    public void add_isCorrect() {
        Store<Data> store = new InMemoryStore<>(Data.class, SortedDataList.class);

        long[] array = new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        store.store(new Data[]{
                new Data(2, 0),
                new Data(4, 0),
                new Data(6, 0),
        });
        store.store(new Data[]{
                new Data(8, 0),
                new Data(0, 0),
                new Data(10, 0),
        });
        store.store(

                new Data(3, 1),
                new Data(5, 1),
                new Data(1, 1),
                new Data(7, 1),
                new Data(9, 1)
        );
        store.store(new Data(11));

        assertEquals(11, store.retrieveLast().getId());
        assertArrayEquals(new long[]{7, 8, 9, 10, 11}, dataToLong(store.retrieveLastN(5)));
        assertArrayEquals(new long[]{3, 4, 5}, dataToLong(store.retrieveInterval(3,6)));
        assertArrayEquals(new long[]{3, 5}, dataToLong(store.retrieveInterval(3,6, 1)));
    }


    @Test
    public void cast_isCorrect() {
        Store<Data> store = new InMemoryStore<>(Data.class, SortedDataList.class);
        IndividualTrigger<Data> mIndividualTrigger = new IndividualTrigger<Data>(Data.class) {
            @Override
            public void onNewData(Store<Data> store, Data data) { }
        };
        store.addObserver("mObserver", mIndividualTrigger);

        IndividualTrigger individualTrigger1 = (IndividualTrigger) store.removeObserver("mObserver");
        assertNotNull(individualTrigger1);

        IndividualTrigger individualTrigger2 = (IndividualTrigger) store.removeObserver("mObserver");
        assertNull(individualTrigger2);
    }
}