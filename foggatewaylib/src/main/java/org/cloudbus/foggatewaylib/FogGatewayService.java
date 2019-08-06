package org.cloudbus.foggatewaylib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class FogGatewayService extends ForegroundService{
    public static final String TAG = "FogGatewayService";
    public static final String KEY_PROGRESS = "progressStore";

    private Map<String, DataStore<? extends Data>> dataStores;
    private Map<String, DataProvider> providers;
    private Map<String, String> providerOutputs;
    private Map<String, String> triggers;
    private Map<String, String> UITriggers;

    private static long NEXT_REQUEST_ID = 1;

    public FogGatewayService(){
        super();
        dataStores = new HashMap<>();
        providers = new HashMap<>();
        providerOutputs = new HashMap<>();
        triggers = new HashMap<>();
        UITriggers = new HashMap<>();

        addDataStore(KEY_PROGRESS, new InMemoryDataStore<>(ProgressData.class));
    }

    @Override
    protected boolean init(Bundle extras) {
        return true;
    }

    @Override
    protected int execute() {
        return 0;
    }

    public static synchronized long nextRequestID(){
        return NEXT_REQUEST_ID++;
    }

    public FogGatewayService addDataStore(String key, DataStore store){

        if (dataStores.containsKey(key)){
            Log.w(TAG, "DataStore " + key + " already exists");
            return this;
        }
        dataStores.put(key, store);

        return this;
    }

    public boolean removeDataStore(String key){
        if (!dataStores.containsKey(key))
            return false;

        dataStores.remove(key);
        return true;
    }

    private FogGatewayService addTriggerGeneric(String storeKey, String triggerkey,
                                                BulkDataTrigger trigger,
                                                Map<String, String> map){
        if (map.containsKey(triggerkey)){
            Log.w(TAG, "Trigger " + triggerkey + " already exists");
            return this;
        }

        if (!dataStores.containsKey(storeKey))
            throw new StoreNotDefinedException(storeKey);

        DataStore dataStore = dataStores.get(storeKey);
        if (dataStore.getDataType().equals(trigger.getDataType())){
            trigger.bindService(this);
            dataStore.addObserver(triggerkey, trigger);
            map.put(triggerkey, storeKey);
        } else
            throw new TypeMismatchException(storeKey, dataStore.getDataType(),
                    trigger.getDataType());

        return this;
    }

    public FogGatewayService addTrigger(String storeKey, String triggerkey,
                                        BulkDataTrigger trigger){

        return addTriggerGeneric(storeKey, triggerkey, trigger, triggers);
    }

    public FogGatewayService addUITrigger(String storeKey, String triggerkey,
                                          final BulkDataTrigger trigger){

        return addTriggerGeneric(storeKey, triggerkey, trigger, UITriggers);
    }

    public FogGatewayService addUITrigger(String storeKey, String triggerkey,
                                          final long requestID, final BulkDataTrigger trigger){
        BulkDataTrigger actualTrigger = new BulkDataTrigger(trigger.getDataType()) {
            @Override
            public void onNewData(DataStore dataStore, Data[] data) {
                if (data[0].getRequestID() == requestID)
                    trigger.onNewData(dataStore, data);
            }
        };

        return addTriggerGeneric(storeKey, triggerkey, actualTrigger, UITriggers);
    }

    public boolean removeTriggerGeneric(String triggerkey,
                                        Map<String, String> map){
        if (!map.containsKey(triggerkey)){
            return false;
        }
        String storeKey = map.get(triggerkey);
        if (!dataStores.containsKey(storeKey))
            return false;

        DataStore dataStore = dataStores.get(storeKey);
        map.remove(triggerkey);

        return dataStore.removeObserver(triggerkey);
    }

    public boolean removeTrigger(String triggerkey){
        return removeTriggerGeneric(triggerkey, triggers);
    }

    public boolean removeUITrigger(String triggerkey){
        return removeTriggerGeneric(triggerkey, UITriggers);
    }

    private void checkDataStore(String key, Class type){
        if (!dataStores.containsKey(key))
            throw new StoreNotDefinedException(key);
        if (!dataStores.get(key).getDataType().equals(type))
            throw new TypeMismatchException(key,
                    dataStores.get(key).getDataType(),
                    type);
    }

    public FogGatewayService addProvider(String providerKey, String outputKey,
                                         DataProvider provider){
        if (providers.containsKey(providerKey)){
            Log.w(TAG, "Provider " + providerKey + " already exists");
            return this;
        }

        checkDataStore(outputKey, provider.getOutputType());

        providers.put(providerKey, provider);
        providerOutputs.put(providerKey, outputKey);

        provider.attach(this, dataStores.get(outputKey), dataStores.get(KEY_PROGRESS));

        return this;
    }

    public boolean removeProvider(String providerKey){
        if (!providers.containsKey(providerKey))
            return false;

        DataProvider provider = providers.get(providerKey);
        provider.detach();

        providers.remove(providerKey);
        return true;
    }

    public void runProvider(String key, long request_id, Data... input){
        if (!providers.containsKey(key))
            throw new ProviderNotDefinedException(key);
        DataProvider provider = providers.get(key);
        provider.execute(request_id, input);
    }

    public DataStore getDataStore(String key){
        return dataStores.get(key);
    }

    public DataProvider getProvider(String key){
        return providers.get(key);
    }

    public class StoreNotDefinedException extends RuntimeException{
        public StoreNotDefinedException(String key){
            super("No data store was defined for key " + key);
        }
    }

    public class ProviderNotDefinedException extends RuntimeException{
        public ProviderNotDefinedException(String key){
            super("No provider was defined for key " + key);
        }
    }

    public class TypeMismatchException extends RuntimeException{
        public TypeMismatchException(String key, Class storeType, Class otherType){
            super("Data store for key " + key + " is of type " + storeType.getName() +
                    " but type " + otherType.getName() + " was given.");
        }
    }

    public static void start(Context context, Class<? extends Activity> cls){
        startForegroundService(context, FogGatewayService.class, cls);
    }

    public static void stop(Context context){
        stopForegroundService(context, FogGatewayService.class);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        for (String trigger:UITriggers.keySet())
            removeUITrigger(trigger);
        return false;
    }
}
