package org.cloudbus.foggatewaylib;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class InMemoryDataStoreTest {

    private long[] dataToLong(Data[] dataList){
        long[] outArray = new long[dataList.length];
        for (int i = 0; i < outArray.length; i++)
            outArray[i] = dataList[i].getId();
        return outArray;
    }

    @Test
    public void add_isCorrect() {
        InMemoryDataStore<Data> store = new InMemoryDataStore<>(Data.class);
        long[] array = new long[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        store.store(new Data[]{
                new Data(2),
                new Data(4),
                new Data(6),
        });
        store.store(new Data[]{
                new Data(8),
                new Data(3),
                new Data(5),
        });
        store.store(
                new Data(0),
                new Data(10),
                new Data(1),
                new Data(7),
                new Data(9)
        );
        store.store(new Data(11));

        assertEquals(11, store.retrieveLast().getId());
        assertArrayEquals(new long[]{7, 8, 9, 10, 11}, dataToLong(store.retrieveLastN(5)));
        assertArrayEquals(new long[]{3, 4, 5}, dataToLong(store.retrieveInterval(3,6)));
    }


    @Test
    public void cast_isCorrect() {
        DataStore<Data> dataStore = new InMemoryDataStore<>(Data.class);
        DataTrigger<Data> mTrigger = new DataTrigger<Data>(Data.class) {
            @Override
            public void onNewData(DataStore<Data> dataStore, Data data) { }
        };
        dataStore.addObserver("mObserver", mTrigger);

        DataTrigger trigger1 = (DataTrigger) dataStore.removeObserver("mObserver");
        assertNotNull(trigger1);

        DataTrigger trigger2 = (DataTrigger) dataStore.removeObserver("mObserver");
        assertNull(trigger2);
    }
}