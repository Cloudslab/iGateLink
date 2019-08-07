package org.cloudbus.foggatewaylib;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.cloudbus.foggatewaylib.utils.MultiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionManager{
    public static final String TAG = "ExecutionManager";
    public static final String KEY_DATA_PROGRESS = "progressStore";

    private static long NEXT_REQUEST_ID = 1;

    private Map<String, Store> stores;
    private Map<String, Provider> providers;
    private MultiMap<String, String> providersOfData;
    private Map<String, String> triggers;
    private Map<String, String> UITriggers;
    private Map<String, Chooser> choosers;

    private Context context;

    public ExecutionManager(Context context){
        super();

        this.context = context;

        stores = new HashMap<>();
        providers = new HashMap<>();
        providersOfData = new MultiMap<>();
        triggers = new HashMap<>();
        choosers = new HashMap<>();
        UITriggers = new HashMap<>();

        addStore(KEY_DATA_PROGRESS, new InMemoryStore<>(ProgressData.class));
    }

    public Context getContext() {
        return context;
    }

    public static synchronized long nextRequestID(){
        return NEXT_REQUEST_ID++;
    }

    public ExecutionManager addStore(String key, Store store){

        if (stores.containsKey(key)){
            Log.w(TAG, "Store " + key + " already exists");
            return this;
        }
        stores.put(key, store);

        return this;
    }

    public boolean removeStore(String key){
        return stores.remove(key) != null;
    }

    @SuppressWarnings("unchecked")
    private ExecutionManager addTriggerGeneric(String dataKey, String triggerKey,
                                               BulkTrigger trigger,
                                               Map<String, String> map){
        if (map.containsKey(triggerKey)){
            Log.w(TAG, "Trigger " + triggerKey + " already exists");
            return this;
        }


        Store store = stores.get(dataKey);
        if (store == null)
            throw new StoreNotDefinedException(dataKey);

        if (store.getDataType().equals(trigger.getDataType())){
            trigger.bindExecutionManager(this);
            store.addObserver(triggerKey, trigger);
            map.put(triggerKey, dataKey);
        } else
            throw new TypeMismatchException(dataKey, store.getDataType(),
                    trigger.getDataType());

        return this;
    }

    public ExecutionManager addTrigger(String dataKey, String triggerKey,
                                       BulkTrigger trigger){

        return addTriggerGeneric(dataKey, triggerKey, trigger, triggers);
    }

    public ExecutionManager addUITrigger(String dataKey, String triggerKey,
                                         BulkTrigger trigger){

        return addTriggerGeneric(dataKey, triggerKey, trigger, UITriggers);
    }

    @SuppressWarnings("unchecked")
    public ExecutionManager addUITrigger(String dataKey, String triggerKey, long requestID,
                                         BulkTrigger trigger){
        return addTriggerGeneric(dataKey, triggerKey,
                new FilteredBulkTrigger(requestID, trigger),
                UITriggers);
    }

    public boolean removeTriggerGeneric(String triggerKey, Map<String, String> map){
        String dataKey = map.get(triggerKey);
        if (dataKey == null)
            return false;

        Store store = stores.get(dataKey);
        if (store == null)
            return false;

        map.remove(triggerKey);

        BulkTrigger trigger = (BulkTrigger) store.removeObserver(triggerKey);
        if (trigger == null)
            return false;

        trigger.unbindExecutionManager();
        return true;
    }

    public boolean removeTrigger(String triggerKey){
        return removeTriggerGeneric(triggerKey, triggers);
    }

    public boolean removeUITrigger(String triggerKey){
        return removeTriggerGeneric(triggerKey, UITriggers);
    }

    public void removeAllUITriggers() {
        for (String trigger:UITriggers.keySet())
            removeUITrigger(trigger);
    }

    @SuppressWarnings("unchecked")
    private void checkStore(String key, @Nullable Class type, boolean autoCreate){
        Store store = stores.get(key);
        if (store == null){
            if (type != null && autoCreate){
                store = new InMemoryStore(type);
                addStore(key, store);
            } else
                throw new StoreNotDefinedException(key);
        }
        if (type != null && !store.getDataType().equals(type))
                throw new TypeMismatchException(key,
                        store.getDataType(),
                        type);
    }

    @SuppressWarnings("unchecked")
    public ExecutionManager addChooser(String outputKey,
                                       Chooser chooser){
        if (choosers.containsKey(outputKey)){
            Log.w(TAG, "Chooser for data " + outputKey + " already exists");
            return this;
        }

        checkStore(outputKey, null, false);
        choosers.put(outputKey, chooser);
        chooser.attach(this);

        return this;
    }

    public boolean removeChooser(String outputKey){
        Chooser chooser = choosers.get(outputKey);

        if (chooser != null){
            chooser.detach();
            return choosers.remove(outputKey) != null;
        } else
            return false;
    }

    @SuppressWarnings("unchecked")
    public ExecutionManager addProvider(String providerKey, String outputKey,
                                        Provider provider){
        if (providers.containsKey(providerKey)){
            Log.w(TAG, "Provider " + providerKey + " already exists");
            return this;
        }

        checkStore(outputKey, provider.getOutputType(), true);

        providers.put(providerKey, provider);
        providersOfData.put(outputKey, providerKey);

        provider.attach(this,
                stores.get(outputKey),
                stores.get(KEY_DATA_PROGRESS));

        return this;
    }

    public boolean removeProvider(String providerKey){
        Provider provider = providers.get(providerKey);

        if (provider != null){
            provider.detach();
            providersOfData.removeValue(providerKey);
            return providers.remove(providerKey) != null;
        } else
            return false;
    }

    @SuppressWarnings("unchecked")
    public void runProvider(String providerKey, long request_id, Data... input){
        Provider provider = providers.get(providerKey);
        if (provider != null){
            if (input.length == 0 || input[0].getClass().equals(provider.getInputType())){
                provider.executeCast(request_id, input);
            } else
                throw new IllegalArgumentException("Expected "
                        + provider.getInputType()
                        + " but "
                        + input[0].getClass()
                        + " was given");
        } else
            throw new ProviderNotDefinedException(providerKey);

    }

    @SuppressWarnings("unchecked")
    public void produceData(String dataKey, long request_id, Data... input){
        List<String> providers = new ArrayList<>(providersOfData.getAll(dataKey));
        if (!providers.isEmpty()){
            if (providers.size() == 1){
                runProvider(providers.get(0), request_id, input);
                return;
            }

            Chooser chooser = choosers.get(dataKey);
            if (chooser == null)
                throw new ChooserNotDefinedException(dataKey);

            runProvider(chooser.chooseProvider(providers.toArray(new String[providers.size()])),
                    request_id, input);
        } else
            throw new ProviderForDataNotDefinedException(dataKey);
    }

    public Store getStore(String key){
        return stores.get(key);
    }

    public Provider getProvider(String key){
        return providers.get(key);
    }

    public Chooser getChooser(String key){
        return choosers.get(key);
    }

    public class StoreNotDefinedException extends RuntimeException{
        StoreNotDefinedException(String key){
            super("No data store was defined for data with key " + key);
        }
    }

    public class ProviderNotDefinedException extends RuntimeException{
        ProviderNotDefinedException(String key){
            super("No provider was defined for key " + key);
        }
    }

    public class ProviderForDataNotDefinedException extends RuntimeException{
        ProviderForDataNotDefinedException(String key){
            super("No provider produces data with key " + key);
        }
    }

    public class ChooserNotDefinedException extends RuntimeException{
        ChooserNotDefinedException(String key){
            super("Multiple providers for data with key " + key + " but no chooser was defined!");
        }
    }

    public class TypeMismatchException extends RuntimeException{
        TypeMismatchException(String key, Class storeType, Class otherType){
            super("Data store for key " + key + " is of type " + storeType.getName() +
                    " but type " + otherType.getName() + " was given.");
        }
    }
}
