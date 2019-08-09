package org.cloudbus.foggatewaylib;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.cloudbus.foggatewaylib.utils.MultiMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrator and manager of the other components ({@link Store}, {@link Provider},
 * {@link Chooser}, {@link Trigger}).
 *
 * @see Store
 * @see Provider
 * @see Chooser
 * @see Trigger
 *
 * @author Riccardo Mancini
 */
public class ExecutionManager{
    public static final String TAG = "ExecutionManager";

    /**
     * Key for the progress data.
     *
     * @see ProgressData
     * @see #ExecutionManager(Context)
     */
    public static final String KEY_DATA_PROGRESS = "progressStore";

    /**
     * Used for generating unique auto-incrementing <code>request_id</code>s using
     * {@link #nextRequestID()}.
     *
     * @see #nextRequestID()
     */
    private static long NEXT_REQUEST_ID = 1;

    /**
     * Map that holds all the {@link Store}s, identified by the key of the data they store.
     * The key is just a unique string defined by the user.
     *
     * @see #addStore(String, Store)
     * @see #getStore(String)
     * @see #removeStore(String)
     * @see #checkStore(String, Class, boolean)
     * @see #produceData(String, long, Data...) a
     */
    private Map<String, Store> stores;

    /**
     * Map that holds all the {@link Provider}s, identified by a unique user-defined key.
     *
     * @see #providersOfData
     * @see #addProvider(String, String, Provider)
     * @see #getProvider(String)
     * @see #removeProvider(String)
     * @see #runProvider(String, long, Data...)
     */
    private Map<String, Provider> providers;

    /**
     * {@link MultiMap} that holds, for every data key, the keys of the providers that can generate
     * that data.
     *
     * @see MultiMap
     * @see #providers
     * @see #addProvider(String, String, Provider)
     * @see #getProvider(String)
     * @see #removeProvider(String)
     */
    private MultiMap<String, String> providersOfData;

    /**
     * Map that associates to every {@link Trigger} key, the key of the data of the {@link Store}
     * they are observers.
     * For {@link Trigger}s related to the UI, use {@link #UITriggers} instead.
     *
     * @see #UITriggers
     * @see #addTrigger(String, String, Trigger)
     * @see #removeTrigger(String)
     */
    private Map<String, String> triggers;

    /**
     * Same as {@link #triggers} but it holds all and only the {@link Trigger}s related to the UI.
     *
     * @see #triggers
     * @see #addUITrigger(String, String, Trigger)
     * @see #addUITrigger(String, String, long, Trigger)
     * @see #removeUITrigger(String)
     * @see #removeAllUITriggers()
     */
    private Map<String, String> UITriggers;

    /**
     * Map that holds all the {@link Chooser}s, identified by the key of the data control the
     * production.
     *
     * @see #addChooser(String, Chooser)
     * @see #getChooser(String)
     * @see #removeChooser(String)
     */
    private Map<String, Chooser> choosers;

    /**
     * Context in which the {@link ExecutionManager} is running.
     *
     * @see #ExecutionManager(Context)
     * @see #getContext()
     */
    private Context context;

    /**
     * Default constructor.
     * It sets the {@link #context}, initializes all the maps and creates the {@link ProgressData}
     * {@link Store}.
     *
     * @param context the {@link Context} in which {@link ExecutionManager} is running.
     *
     * @see #context
     * @see #stores
     * @see #providers
     * @see #providersOfData
     * @see #triggers
     * @see #choosers
     * @see #UITriggers
     * @see #KEY_DATA_PROGRESS
     */
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

    /**
     * @return the {@link Context} in which {@link ExecutionManager} is running.
     */
    public Context getContext() {
        return context;
    }

    /**
     * Generates unique auto-incrementing <code>requestID</code>s.
     *
     * @return unique auto-incremented request ID.
     * @see #NEXT_REQUEST_ID
     */
    public static synchronized long nextRequestID(){
        return NEXT_REQUEST_ID++;
    }

    /**
     * Adds a new {@link Store} identified by the given key.
     *
     * @param key the key that identifies the data stored in this {@link Store}.
     * @param store the store to be added.
     * @return the {@link ExecutionManager} instance for chaining.
     * @see #stores
     * @see #getStore(String)
     * @see #removeStore(String)
     * @see #checkStore(String, Class, boolean)
     */
    public ExecutionManager addStore(String key, Store store){

        if (stores.containsKey(key)){
            Log.w(TAG, "Store " + key + " already exists");
            return this;
        }
        stores.put(key, store);

        return this;
    }

    /**
     * Removes the {@link Store} identified by the given key.
     *
     * @param key the key that identifies the data stored in this {@link Store}.
     * @return true if the store was found, false otherwise
     * @see #stores
     * @see #getStore(String)
     * @see #addStore(String, Store)
     */
    public boolean removeStore(String key){
        return stores.remove(key) != null;
    }

    /**
     * Adds a new {@link Trigger} identified by the given <code>triggerKey</code> that operates
     * on the {@link Store} identified by the <code>dataKey</code> in the given map.
     * In case another {@link Trigger} already exists for the given key, the addition will be
     * aborted.
     *
     * @param dataKey the key that identifies the {@link Store} observed by the {@link Trigger}.
     * @param triggerKey the key of the {@link Trigger}.
     * @param trigger the trigger to be added.
     * @param map the map in which the trigger should be put (must be one of {@link #triggers} or
     *            {@link #UITriggers}.
     * @return the {@link ExecutionManager} instance for chaining.
     * @see #removeTriggerGeneric(String, Map)
     * @see #addUITrigger(String, String, long, Trigger)
     * @see #addUITrigger(String, String, Trigger)
     * @see #addTrigger(String, String, Trigger)
     */
    @SuppressWarnings("unchecked")
    private ExecutionManager addTriggerGeneric(String dataKey, String triggerKey,
                                               Trigger trigger,
                                               Map<String, String> map){
        if (map.containsKey(triggerKey)){
            Log.w(TAG, "IndividualTrigger " + triggerKey + " already exists");
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

    /**
     * Adds a new {@link Trigger} identified by the given <code>triggerKey</code> that operates
     * on the {@link Store} identified by the <code>dataKey</code> in {@link #triggers} map.
     * In case another {@link Trigger} already exists for the given key, the addition will be
     * aborted.
     *
     * @param dataKey the key that identifies the {@link Store} observed by the {@link Trigger}.
     * @param triggerKey the key of the {@link Trigger}.
     * @param trigger the trigger to be added.
     * @return the {@link ExecutionManager} instance for chaining.
     * @see #addTriggerGeneric(String, String, Trigger, Map)
     * @see #removeTrigger(String)
     */
    public ExecutionManager addTrigger(String dataKey, String triggerKey,
                                       Trigger trigger){

        return addTriggerGeneric(dataKey, triggerKey, trigger, triggers);
    }

    /**
     * Adds a new {@link Trigger} identified by the given <code>triggerKey</code> that operates
     * on the {@link Store} identified by the <code>dataKey</code> in {@link #UITriggers} map.
     * In case another {@link Trigger} already exists for the given key, the addition will be
     * aborted.
     *
     * @param dataKey the key that identifies the {@link Store} observed by the {@link Trigger}.
     * @param triggerKey the key of the {@link Trigger}.
     * @param trigger the trigger to be added.
     * @return the {@link ExecutionManager} instance for chaining.
     * @see #addTriggerGeneric(String, String, Trigger, Map)
     * @see #addUITrigger(String, String, long, Trigger)
     * @see #removeUITrigger(String)
     * @see #removeAllUITriggers()
     */
    public ExecutionManager addUITrigger(String dataKey, String triggerKey,
                                         Trigger trigger){

        return addTriggerGeneric(dataKey, triggerKey, trigger, UITriggers);
    }

    /**
     * Adds a new {@link Trigger} identified by the given <code>triggerKey</code> that is sensible
     * only to new data from the request identified by the <code>requestID</code> and that operates
     * on the {@link Store} identified by the <code>dataKey</code> in {@link #UITriggers} map.
     * The <code>trigger</code> will be wrapped inside a {@link FilteredTrigger}.
     * In case another {@link Trigger} already exists for the given key, the addition will be
     * aborted.
     *
     * @param dataKey the key that identifies the {@link Store} observed by the {@link Trigger}.
     * @param triggerKey the key of the {@link Trigger}.
     * @param requestID the id of the request this {@link Trigger } should be observing.
     * @param trigger the trigger to be added.
     * @return the {@link ExecutionManager} instance for chaining.
     * @see FilteredTrigger
     * @see #addTriggerGeneric(String, String, Trigger, Map)
     * @see #addUITrigger(String, String, Trigger)
     * @see #removeUITrigger(String)
     * @see #removeAllUITriggers()
     */
    @SuppressWarnings("unchecked")
    public ExecutionManager addUITrigger(String dataKey, String triggerKey, long requestID,
                                         Trigger trigger){
        return addTriggerGeneric(dataKey, triggerKey,
                new FilteredTrigger(requestID, trigger),
                UITriggers);
    }

    /**
     * Removes the {@link Trigger} identified by the given <code>triggerKey</code> from the
     * given map.
     *
     * @param triggerKey the key of the {@link Trigger}.
     * @param map the map from which the trigger should be removed (must be one of
     *            {@link #triggers} or {@link #UITriggers}.
     * @return true if the trigger was found, false otherwise .
     * @see #addTriggerGeneric(String, String, Trigger, Map)
     * @see #removeTrigger(String)
     * @see #removeUITrigger(String)
     * @see #removeAllUITriggers()
     */
    public boolean removeTriggerGeneric(String triggerKey, Map<String, String> map){
        String dataKey = map.get(triggerKey);
        if (dataKey == null)
            return false;

        Store store = stores.get(dataKey);
        if (store == null)
            return false;

        map.remove(triggerKey);

        Trigger trigger = (Trigger) store.removeObserver(triggerKey);
        if (trigger == null)
            return false;

        trigger.unbindExecutionManager();
        return true;
    }

    /**
     * Removes the {@link Trigger} identified by the given <code>triggerKey</code> from the
     * {@link #triggers} map.
     *
     * @param triggerKey the key of the {@link Trigger}.
     * @return true if the trigger was found, false otherwise .
     * @see #removeTriggerGeneric(String, Map)
     * @see #addTrigger(String, String, Trigger)
     */
    public boolean removeTrigger(String triggerKey){
        return removeTriggerGeneric(triggerKey, triggers);
    }

    /**
     * Removes the {@link Trigger} identified by the given <code>triggerKey</code> from the
     * {@link #triggers} map.
     *
     * @param triggerKey the key of the {@link Trigger}.
     * @return true if the trigger was found, false otherwise.
     * @see #removeTriggerGeneric(String, Map)
     * @see #addTrigger(String, String, Trigger)
     */
    public boolean removeUITrigger(String triggerKey){
        return removeTriggerGeneric(triggerKey, UITriggers);
    }

    /**
     * Removes the {@link Trigger} identified by the given <code>triggerKey</code> from the
     * {@link #UITriggers} map.
     *
     * @param triggerKey the key of the {@link Trigger}.
     * @return true if the trigger was found, false otherwise .
     * @see #removeTriggerGeneric(String, Map)
     * @see #addUITrigger(String, String, Trigger)
     * @see #addUITrigger(String, String, long, Trigger)
     */
    public void removeAllUITriggers() {
        for (String trigger:UITriggers.keySet())
            removeUITrigger(trigger);
    }

    /**
     * Checks if a {@link Store} for the given key exists and if the data it constains is of the
     * same type of <code>type</code>. If it does not exist and <code>autoCreate</code> is set to
     * true, then it will be created using a new unlimited {@link InMemoryStore}.
     *
     * @param key the key of the store to check.
     * @param type the type of the data we want to check against.
     * @param autoCreate if true, the store will be created if not found.
     * @throws StoreNotDefinedException thrown if no store is found.
     * @throws TypeMismatchException thrown if <code>type</code> is not the same as the
     *                               {@link Store#getDataType()}'
     * @see #addStore(String, Store)
     * @see #addProvider(String, String, Provider)
     */
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

    /**
     * Adds a new {@link Chooser} that manages the production of the data identified by the given
     * key.
     *
     * @param outputKey the key of the data the {@link Chooser} manages the production of.
     * @param chooser the {@link Chooser} to be added.
     * @return the {@link ExecutionManager} instance for chaining.
     * @throws StoreNotDefinedException if no {@link Store} was found for the given key.
     * @see #removeChooser(String)
     * @see #getChooser(String)
     * @see #checkStore(String, Class, boolean)
     */
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

    /**
     * Removes the {@link Chooser} that manages the production of the data identified by the given
     * key.
     *
     * @param outputKey the key of the data the {@link Chooser} manages the production of.
     * @return true if the {@link Chooser} was found, false otherwise.
     * @see #addChooser(String, Chooser)
     * @see #getChooser(String)
     */
    public boolean removeChooser(String outputKey){
        Chooser chooser = choosers.get(outputKey);

        if (chooser != null){
            chooser.detach();
            return choosers.remove(outputKey) != null;
        } else
            return false;
    }

    /**
     * Adds a new {@link Provider} identified by the given <code>providerKey</code> that
     * provides data identified by the key <code>outputKey</code>.
     * If no {@link Store} is found for the given <code>outputKey</code>, a new one will be
     * created as in {@link #checkStore(String, Class, boolean)}.
     *
     * @param providerKey the key that identifies the {@link Provider}.
     * @param outputKey the key of the data the {@link Provider} provides.
     * @param provider the {@link Provider} to be added.
     * @return the {@link ExecutionManager} instance for chaining.
     * @throws TypeMismatchException if {@link Store#getDataType()} is different from
     *                               {@link Provider#getInputType()}.
     * @see #removeProvider(String)
     * @see #getProvider(String)
     * @see #checkStore(String, Class, boolean)
     */
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
                providerKey,
                stores.get(outputKey),
                stores.get(KEY_DATA_PROGRESS));

        return this;
    }

    /**
     * Removes the {@link Provider} identified by the given key.
     *
     * @param providerKey the key that identifies the {@link Provider}.
     * @param provider the {@link Provider} to be added.
     * @return true if the {@link Provider} was found, false otherwise.
     * @see #addProvider(String, String, Provider)
     * @see #getProvider(String)
     */
    public boolean removeProvider(String providerKey){
        Provider provider = providers.get(providerKey);

        if (provider != null){
            provider.detach();
            providersOfData.removeValue(providerKey);
            return providers.remove(providerKey) != null;
        } else
            return false;
    }

    /**
     * Runs the {@link Provider} identified by the given key.
     *
     * @param providerKey the key that identifies the {@link Provider}.
     * @param request_id the id of the request this execution belongs to.
     * @param input the input data
     * @throws IllegalArgumentException if input data is not the same type as
     *                                  {@link Provider#getInputType()}.
     * @throws ProviderNotDefinedException if no provider was found matching the provided key.
     * @see #addProvider(String, String, Provider)
     * @see #removeProvider(String)
     * @see #getProvider(String)
     */
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

    /**
     * Chooses a {@link Provider} to run in the given list. If more than one {@link Provider} is
     * given, the right {@link Chooser} will be queried to select one of those.
     *
     * @param providers the list of providers to choose from.
     * @param dataKey the key that identifies the data.
     * @param request_id the id of the request this execution belongs to.
     * @param input the input data
     * @throws ChooserNotDefinedException if there are multiple {@link Provider}s but no
     *                                    {@link Chooser}.
     * @throws ProviderForDataNotDefinedException if no provider was found that provides the given
     *                                            data.
     * @see #produceData(String, long, Data...) 
     * @see #produceDataExcludeProviders(String, long, String[], Data...)
     */
    private void chooseProvider(List<String> providers, String dataKey, long request_id,
                                Data... input){
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

    /**
     * Runs a {@link Provider} that produces the data identified by the given key. If more than one
     * {@link Provider} is defined, the right {@link Chooser} will be queried to select one of
     * those.
     *
     * @param dataKey the key that identifies the data.
     * @param request_id the id of the request this execution belongs to.
     * @param input the input data
     * @throws ChooserNotDefinedException if there are multiple {@link Provider}s but no
     *                                    {@link Chooser}.
     * @throws ProviderForDataNotDefinedException if no provider was found that provides the given
     *                                            data.
     * @see #chooseProvider(List, String, long, Data...)
     * @see #addProvider(String, String, Provider)
     * @see #removeProvider(String)
     * @see #getProvider(String)
     * @see #produceDataExcludeProviders(String, long, String[], Data...)
     */
    @SuppressWarnings("unchecked")
    public void produceData(String dataKey, long request_id, Data... input){
        chooseProvider(new ArrayList<>(providersOfData.getAll(dataKey)),
                dataKey, request_id, input);
    }

    /**
     * Runs a {@link Provider} that produces the data identified by the given key excluding some of
     * the {@link Provider}s. If more than one {@link Provider} is defined and has not been
     * excluded, the right {@link Chooser} will be queried to select one of those.
     *
     * @param dataKey the key that identifies the data.
     * @param request_id the id of the request this execution belongs to.
     * @param excludedProviders providers that must not be run.
     * @param input the input data
     * @throws ChooserNotDefinedException if there are multiple {@link Provider}s but no
     *                                    {@link Chooser}.
     * @throws ProviderForDataNotDefinedException if no provider was found that provides the given
     *                                            data.
     * @see #chooseProvider(List, String, long, Data...)
     * @see #addProvider(String, String, Provider)
     * @see #removeProvider(String)
     * @see #getProvider(String)
     * @see #produceData(String, long, Data...)
     */
    @SuppressWarnings("unchecked")
    public void produceDataExcludeProviders(String dataKey, long request_id,
                                            String[] excludedProviders,
                                            Data... input){
        List<String> providers = new ArrayList<>(providersOfData.getAll(dataKey));
        providers.removeAll(Arrays.asList(excludedProviders));
        chooseProvider(providers, dataKey, request_id, input);
    }

    /**
     * Gets the {@link Store} identified by the given key.
     *
     * @param key the key
     * @return the removed {@link Store} or null if it was not found.
     * @see #addStore(String, Store)
     * @see #removeStore(String)
     */
    public Store getStore(String key){
        return stores.get(key);
    }

    /**
     * Gets the {@link Provider} identified by the given key.
     *
     * @param key the key
     * @return the removed {@link Provider} or null if it was not found.
     * @see #addProvider(String, String, Provider)
     * @see #removeProvider(String)
     */
    public Provider getProvider(String key){
        return providers.get(key);
    }

    /**
     * Gets the {@link Chooser} identified by the given key.
     *
     * @param key the key
     * @return the removed {@link Chooser} or null if it was not found.
     * @see #addChooser(String, Chooser)
     * @see #removeChooser(String)
     */
    public Chooser getChooser(String key){
        return choosers.get(key);
    }

    /**
     * @see #checkStore(String, Class, boolean)
     * @see #addChooser(String, Chooser)
     */
    public class StoreNotDefinedException extends RuntimeException{
        StoreNotDefinedException(String key){
            super("No data store was defined for data with key " + key);
        }
    }

    /**
     * @see #runProvider(String, long, Data...)
     */
    public class ProviderNotDefinedException extends RuntimeException{
        ProviderNotDefinedException(String key){
            super("No provider was defined for key " + key);
        }
    }

    /**
     * @see #produceData(String, long, Data...)
     */
    public class ProviderForDataNotDefinedException extends RuntimeException{
        ProviderForDataNotDefinedException(String key){
            super("No provider produces data with key " + key);
        }
    }

    /**
     * @see #produceData(String, long, Data...)
     */
    public class ChooserNotDefinedException extends RuntimeException{
        ChooserNotDefinedException(String key){
            super("Multiple providers for data with key " + key + " but no chooser was defined!");
        }
    }

    /**
     * @see #checkStore(String, Class, boolean)
     * @see #addProvider(String, String, Provider)
     */
    public class TypeMismatchException extends RuntimeException{
        TypeMismatchException(String key, Class storeType, Class otherType){
            super("Data store for key " + key + " is of type " + storeType.getName() +
                    " but type " + otherType.getName() + " was given.");
        }
    }
}
