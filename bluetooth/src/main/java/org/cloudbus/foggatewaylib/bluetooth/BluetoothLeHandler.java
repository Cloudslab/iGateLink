package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

/**
 * Generic class for connecting multiple devices and defining different actions for different
 * services and characteristics.
 * This class is a singleton, meaning that there can exist only one instance at a time, which can
 * be obtained by calling {@link #getInstance()}.
 *
 * @author Riccardo Mancini
 */
public class BluetoothLeHandler {

    private static final int ACTION_READ = 1;
    private static final int ACTION_WRITE = 2;

    /**
     * Possible {@code status} for
     * {@link BluetoothGattCallback#onServicesDiscovered(BluetoothGatt, int)} which indicates that
     * the server does not satisfy the requirements provided with
     * {@link #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])}.
     *
     * @see #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])
     * @see BluetoothGattCallback#onServicesDiscovered(BluetoothGatt, int)
     */
    public static final int GATT_FAILS_REQUIREMENTS = 1;

    /**
     * Singleton instance.
     */
    private static BluetoothLeHandler instance;

    private Context context;

    /**
     * The {@link BluetoothGatt}s connected.
     */
    private Set<BluetoothGatt> gatts = new HashSet<>();

    /**
     * The provided callbacks associated with a {@link ServiceCharacteristicPair}.
     * These are executed at:
     * <ul>
     *     <li>{@link GeneralCallback#onCharacteristicRead(BluetoothGatt, BluetoothGattCharacteristic, int)} </li>
     *     <li>{@link GeneralCallback#onCharacteristicWrite(BluetoothGatt, BluetoothGattCharacteristic, int)} </li>
     *     <li>{@link GeneralCallback#onCharacteristicChanged(BluetoothGatt, BluetoothGattCharacteristic)} </li>
     * </ul>
     *
     * @see #addCallback(UUID, UUID, boolean, BluetoothGattCallback)
     * @see #removeCallback(BluetoothGattCallback)
     */
    private Map<ServiceCharacteristicPair, BluetoothGattCallback> callbacks = new HashMap<>();

    /**
     * The provided callbacks associated with a {@link BluetoothGatt}.
     * These are executed at:
     * <ul>
     *     <li>{@link GeneralCallback#onConnectionStateChange(BluetoothGatt, int, int)}</li>
     *     <li>{@link GeneralCallback#onServicesDiscovered(BluetoothGatt, int)}</li>
     * </ul>
     *
     * @see #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])
     * @see #removeGattConnectCallback(BluetoothGattCallback)
     */
    private Map<BluetoothGatt, BluetoothGattCallback> gattConnectCallbacks = new HashMap<>();

    /**
     * Requirements associated with the {@link BluetoothGatt}.
     * A requirement is a {@link Set} of {@link ServiceCharacteristicPair} which the device must
     * provide. A device must match at least one of the requirements to pass the test.
     *
     * @see #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])
     */
    private Map<BluetoothGatt, List<Set<ServiceCharacteristicPair>>> gattRequirements = new HashMap<>();

    /**
     * {@link Set} of {@link ServiceCharacteristicPair} that must be flagged for notifications.
     *
     * @see #enableNotification(BluetoothGatt, UUID, UUID, boolean)
     * @see #enableNotifications(boolean)
     * @see #enableNotifications(BluetoothGatt, boolean)
     */
    private Set<ServiceCharacteristicPair> notifyServiceCharacteristicPairs = new HashSet<>();

    /**
     * Queue of pending actions.
     * NB: the device can only do one action at a time!
     *
     * @see #nextAction()
     * @see #doAction(BluetoothGatt, BluetoothGattCharacteristic, int)
     */
    private Queue<PendingAction> queue = new LinkedList<>();

    /**
     * Current action being executed.
     *
     * @see #nextAction()
     * @see #doAction(BluetoothGatt, BluetoothGattCharacteristic, int)
     */
    private PendingAction actionInProgress = null;

    /**
     * Callback instance that will be passed to every connected {@link BluetoothGatt}.
     */
    private GeneralCallback mCallback = new GeneralCallback();

    /**
     * Used to check connected devices.
     */
    private SimpleBluetoothLeAdapter bluetoothLeAdapter;

    /**
     * Returns the singleton instance.
     */
    public static BluetoothLeHandler getInstance() {
        if (instance == null)
            instance = new BluetoothLeHandler();
        return instance;
    }

    /**
     * Returns all associated {@link BluetoothGatt}s.
     * NB: also {@link BluetoothGatt}s that are not yet connected can be returned.
     */
    @NonNull
    public Set<BluetoothGatt> getGatts() {
        return gatts;
    }

    /**
     * Returns all connected devices.
     *
     * @see SimpleBluetoothLeAdapter#isDeviceConnected(BluetoothDevice)
     */
    @NonNull
    public Set<BluetoothDevice> getConnectedDevices() {
        Set<BluetoothDevice> devices = new HashSet<>();

        if (bluetoothLeAdapter == null)
            return devices;

        for (BluetoothGatt gatt:gatts){
            if (bluetoothLeAdapter.isDeviceConnected(gatt.getDevice()))
                devices.add(gatt.getDevice());
        }

        return devices;
    }

    /**
     * Returns latest set {@link Context}.
     *
     * @see #setContext(Context)
     */
    public Context getContext() {
        return context;
    }

    /**
     * Sets the {@link Context} that will be used for future
     * {@link #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])}.
     *
     * @see #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])
     * @see BluetoothGatt#connect()
     */
    public void setContext(Context context) {
        this.context = context;
        if (context != null)
            bluetoothLeAdapter = new SimpleBluetoothLeAdapter(context);
        else
            bluetoothLeAdapter = null;
    }

    /**
     * Retrieves and executes the next action in the {@link #queue}.
     */
    private synchronized void nextAction(){
        actionInProgress = queue.poll();
        if (actionInProgress != null){
            switch(actionInProgress.action){
                case ACTION_READ:
                    actionInProgress.gatt.readCharacteristic(actionInProgress.characteristic);
                    break;
                case ACTION_WRITE:
                    actionInProgress.gatt.writeCharacteristic(actionInProgress.characteristic);
                    break;
            }
        }
    }

    /**
     * Adds an action to the queue and, if no action is currently executed, executes it.
     *
     * @param gatt the {@link BluetoothGatt} server to which the action will be done.
     * @param characteristic the {@link BluetoothGattCharacteristic} of the {@code gatt} to
     *                       which the action will be done.
     * @param action the action to be done. It must be one of {@link #ACTION_READ} or
     *               {@link #ACTION_WRITE}.
     */
    private synchronized void doAction(BluetoothGatt gatt,
                                       BluetoothGattCharacteristic characteristic,
                                       int action){
        queue.add(new PendingAction(gatt, characteristic, action));
        if (actionInProgress == null)
            nextAction();
    }

    /**
     * Adds a new callback for the specified pair of service and characteristic.
     * If {@code notify} is {@code true}, then notifications on currently connected devices will
     * be enabled for the matching pairs.
     *
     * @param service the {@link UUID} of the {@link BluetoothGattService}.
     * @param characteristic the {@link UUID} of the {@link BluetoothGattCharacteristic}.
     * @param notify the flag specifying whether to ask connected devices to notify changes on this
     *               {@link BluetoothGattCharacteristic}.
     * @param callback the callback to be executed when this service characteristic pair is
     *                 received.
     * @return {@code true} if there was already another callback associated with this pair that
     *         has been replaced, {@code false} otherwise.
     */
    public boolean addCallback(UUID service, UUID characteristic, boolean notify,
                            BluetoothGattCallback callback){
        ServiceCharacteristicPair pair = new ServiceCharacteristicPair(service, characteristic);
        boolean result = callbacks.put(pair, callback) != null;
        if (notify){
            notifyServiceCharacteristicPairs.add(pair);
            enableNotification(null, service, characteristic, true);
        }
        return result;

    }

    /**
     * Removes the given callback.
     *
     * @param callback the callback to be removed
     * @return number of callback occurrences removed.
     * @see #addCallback(UUID, UUID, boolean, BluetoothGattCallback)
     * @see #removeGattConnectCallback(BluetoothGattCallback)
     */
    public int removeCallback(BluetoothGattCallback callback){
        Set<ServiceCharacteristicPair> keysToRemove = new HashSet<>();
        for (ServiceCharacteristicPair key:callbacks.keySet()){
            if (callback.equals(callbacks.get(key))){
                keysToRemove.add(key);
            }
        }
        for (ServiceCharacteristicPair key:keysToRemove){
            callbacks.remove(key);
        }
        return keysToRemove.size();
    }

    /**
     * Connects to the specified {@link BluetoothDevice}.
     * The given {@link BluetoothGattCallback} will be executed when device is connected and when
     * its services are being discovered.
     * Optionally, an array of requirements can be passed to this function. A requirement is a
     * {@link Set} of {@link ServiceCharacteristicPair} that the device must contain. At least one
     * of the given {@code requirements} must be matched in order for the device to pass the test.
     * In the most simple use-case, there will be just one requirement with one single
     * {@link ServiceCharacteristicPair} representing the {@link BluetoothGattCharacteristic} that
     * a {@link BluetoothGattCallback} has been registered for.
     *
     *
     * @param device the device to connect to.
     * @param connectionStateCallback the callback to be called when device is connected and when
     *                                its services are being discovered.
     * @param requirements the requirements the device must match as described previously.
     * @return {@code true} if initialization of connection is successful, {@code false} otherwise.
     * @throws RuntimeException if no context has previously been set.
     * @see #setContext(Context)
     * @see #removeGattConnectCallback(BluetoothGattCallback)
     */
    public boolean connectGatt(BluetoothDevice device,
                               BluetoothGattCallback connectionStateCallback,
                               Set<ServiceCharacteristicPair>... requirements){
        if (context == null)
            throw new RuntimeException("Context must be set before connecting.");

        if (bluetoothLeAdapter != null && bluetoothLeAdapter.isDeviceConnected(device)){
            return false;
        }

        BluetoothGatt gatt = device.connectGatt(context, true, mCallback);

        if (gatt == null)
            return false;

        gatts.add(gatt);
        gattRequirements.put(gatt, Arrays.asList(requirements));
        gattConnectCallbacks.put(gatt, connectionStateCallback);
        return true;
    }

    /**
     * Removes the given callback from the callbacks associated with a device connection event.
     *
     * @param callback the callback to be removed
     * @return number of callback occurrences removed.
     * @see #connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])
     * @see #removeCallback(BluetoothGattCallback)
     */
    public int removeGattConnectCallback(BluetoothGattCallback callback){
        Set<BluetoothGatt> keysToRemove = new HashSet<>();
        for (BluetoothGatt key:gattConnectCallbacks.keySet()){
            if (callback.equals(gattConnectCallbacks.get(key))){
                keysToRemove.add(key);
            }
        }
        for (BluetoothGatt key:keysToRemove){
            gattConnectCallbacks.remove(key);
        }
        return keysToRemove.size();
    }

    /**
     * Reads the given service-characteristic pair from the given device.
     *
     * @param readGatt the GATT server to read from.
     * @param readService the {@link BluetoothGattService} to read from or
     *                    {@code null} if all services must be checked.
     * @param readCharacteristic the {@link BluetoothGattCharacteristic} fto read from or
     *                           {@code null} if all {@link BluetoothGattCharacteristic}s in the
     *                           given {@link BluetoothGattService} must be read.
     * @see #writeCharacteristic(BluetoothGatt, UUID, UUID, byte[])
     */
    public void readCharacteristic(BluetoothGatt readGatt, UUID readService,
                                   UUID readCharacteristic){

        ServiceCharacteristicPair pair
                = new ServiceCharacteristicPair(readService, readCharacteristic);

        Collection<BluetoothGatt> gattCollection;

        if (readGatt == null)
            gattCollection = gatts;
        else
            gattCollection = Collections.singletonList(readGatt);

        for (BluetoothGatt gatt:gattCollection){
            for (BluetoothGattService service:gatt.getServices()){
                for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                    if (pair.matches(service, characteristic)){
                        doAction(gatt, characteristic, ACTION_READ);
                    }
                }
            }
        }
    }

    /**
     * Writes the given value to the given service-characteristic pair in the given device.
     *
     * @param writeGatt the GATT server to write to.
     * @param writeService the {@link BluetoothGattService} to write to or
     *                    {@code null} if all services must be checked.
     * @param writeCharacteristic the {@link BluetoothGattCharacteristic} to write to or
     *                           {@code null} if all {@link BluetoothGattCharacteristic}s in the
     *                           given {@link BluetoothGattService} must be written.
     * @param newValue the value to be written.
     * @see #readCharacteristic(BluetoothGatt, UUID, UUID)
     */
    public void writeCharacteristic(BluetoothGatt writeGatt, UUID writeService,
                                    UUID writeCharacteristic, byte[] newValue){

        ServiceCharacteristicPair pair
                = new ServiceCharacteristicPair(writeService, writeCharacteristic);

        Collection<BluetoothGatt> gattCollection;

        if (writeGatt == null)
            gattCollection = gatts;
        else
            gattCollection = Collections.singletonList(writeGatt);

        for (BluetoothGatt gatt:gattCollection){
            for (BluetoothGattService service:gatt.getServices()){
                for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                    if (pair.matches(service, characteristic)){
                        characteristic.setValue(newValue);
                        doAction(gatt, characteristic, ACTION_WRITE);
                    }
                }
            }
        }
    }

    /**
     * Enables/disables notifications in all devices for the characteristics previously marked for
     * notification.
     *
     * @param enable {@code true} to enable the notifications, {@code false} to disable them.
     * @see #enableNotifications(BluetoothGatt, boolean)
     * @see #enableNotification(BluetoothGatt, UUID, UUID, boolean)
     */
    public void enableNotifications(boolean enable){
        for (BluetoothGatt gatt:gatts){
            enableNotifications(gatt, enable);
        }
    }

    /**
     * Enables/disables notifications in the given device for the characteristics previously marked
     * for notification.
     *
     * @param gatt the GATT server in which notifications must be enabled/disabled.
     * @param enable {@code true} to enable the notifications, {@code false} to disable them.
     * @see #enableNotifications(boolean)
     * @see #enableNotification(BluetoothGatt, UUID, UUID, boolean)
     */
    public void enableNotifications(BluetoothGatt gatt, boolean enable){
        for (BluetoothGattService service:gatt.getServices()){
            for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                if(!getMatchingPairs(notifyServiceCharacteristicPairs, characteristic)
                        .isEmpty()){
                    gatt.setCharacteristicNotification(characteristic, enable);
                }
            }
        }
    }

    /**
     * Enables/disables notifications in the given device and service-characteristic pair.
     *
     * @param notifyGatt the GATT server in which notifications must be enabled/disabled.
     * @param notifyService the {@link BluetoothGattService} in which notifications must be
     *                      enabled/disabled or {@code null} if all services must be checked.
     * @param notifyCharacteristic the {@link BluetoothGattCharacteristic} in which notifications
     *                             must be enabled/disabled or {@code null} if all
     *                             {@link BluetoothGattCharacteristic}s in the given
     *                             {@link BluetoothGattService} must be enabled/disabled.
     * @param enable {@code true} to enable the notifications, {@code false} to disable them.
     * @see #enableNotifications(boolean)
     * @see #enableNotifications(BluetoothGatt, boolean)
     */
    public void enableNotification(BluetoothGatt notifyGatt, UUID notifyService,
                                   UUID notifyCharacteristic, boolean enable){

        ServiceCharacteristicPair pair
                = new ServiceCharacteristicPair(notifyService, notifyCharacteristic);

        Collection<BluetoothGatt> gattCollection;

        if (notifyGatt == null)
            gattCollection = gatts;
        else
            gattCollection = Collections.singletonList(notifyGatt);

        for (BluetoothGatt gatt:gattCollection){
            for (BluetoothGattService service:gatt.getServices()){
                for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                    if (pair.matches(service, characteristic)){
                        gatt.setCharacteristicNotification(characteristic, enable);
                    }
                }
            }
        }
    }

    /**
     * Returns all the {@code pairs} matching the given {@code service} and {@code characteristic}.
     *
     * @see #getMatchingPairs(Set, UUID, UUID)
     */
    private Set<ServiceCharacteristicPair> getMatchingPairs(Set<ServiceCharacteristicPair> pairs,
                                                            BluetoothGattCharacteristic characteristic){
        return getMatchingPairs(pairs,
                characteristic.getService().getUuid(),
                characteristic.getUuid());
    }

    /**
     * Returns all the {@code pairs} matching the given {@code service} and {@code characteristic}.
     *
     * @see #getMatchingPairs(Set, BluetoothGattService, BluetoothGattCharacteristic)
     */
    private Set<ServiceCharacteristicPair> getMatchingPairs(Set<ServiceCharacteristicPair> pairs,
                                                            UUID service, UUID characteristic){
        Set<ServiceCharacteristicPair> result = new HashSet<>();
        for (ServiceCharacteristicPair pair:pairs) {
            if (pair.matches(service, characteristic)) {
                result.add(pair);
            }
        }
        return result;
    }

    /**
     * Returns all the {@link #callbacks} matching the given {@code characteristic}.
     */
    private Set<BluetoothGattCallback> getMatchingCallbacks(
            BluetoothGattCharacteristic characteristic){
        Set<BluetoothGattCallback> result = new HashSet<>();
        for (ServiceCharacteristicPair key:getMatchingPairs(callbacks.keySet(),
                                                   characteristic)) {
            result.add(callbacks.get(key));
        }
        return result;
    }

    /**
     * Class handling all callbacks from bluetooth devices.
     */
    private class GeneralCallback extends BluetoothGattCallback{

        /**
         * When a characteristic changes, call all callbacks associated with it.
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            for (BluetoothGattCallback callback:getMatchingCallbacks(characteristic)){
                callback.onCharacteristicChanged(gatt, characteristic);
            }
        }

        /**
         * When a characteristic is read, call all callbacks associated with it and executes the
         * next pending action.
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            for (BluetoothGattCallback callback:getMatchingCallbacks(characteristic)){
                callback.onCharacteristicRead(gatt, characteristic, status);
            }
            nextAction();
        }

        /**
         * When a characteristic is written, call all callbacks associated with it and executes the
         * next pending action.
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                         int status) {
            for (BluetoothGattCallback callback:getMatchingCallbacks(characteristic)){
                callback.onCharacteristicWrite(gatt, characteristic, status);
            }
            nextAction();
        }

        /**
         * When a connection state changes, call the callback associated with the given GATT
         * server. If the {@code newState} is {@link BluetoothGatt#STATE_CONNECTED}, service
         * discovery for the given GATT service is also initiated.
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED)
                gatt.discoverServices();

            BluetoothGattCallback callback = gattConnectCallbacks.get(gatt);
            if (callback != null)
                callback.onConnectionStateChange(gatt, status, newState);
        }

        /**
         * When services for a GATT server are discovered, check whether the given GATT server
         * matches all associated {@link #gattRequirements}. If it does not pass the test,
         * the device is disconnected. Finally, the callback associated with the given
         * {@link BluetoothGatt} are executed.
         * NB: in case of failed requirements test, {@code status} passed to the callback will be
         * {@link #GATT_FAILS_REQUIREMENTS}.
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            int mStatus;
            
            if (status == BluetoothGatt.GATT_SUCCESS){
                List<Set<ServiceCharacteristicPair>> requirements = gattRequirements.get(gatt);
                if (requirements != null && !requirements.isEmpty()){
                    for (BluetoothGattService service:gatt.getServices()){
                        for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                            for (Set<ServiceCharacteristicPair> requirement:requirements){
                                requirement.removeAll(getMatchingPairs(requirement, characteristic));
                            }
                        }
                    }
                }

                boolean checkRequirements = false;

                for (Set<ServiceCharacteristicPair> requirement:requirements) {
                    if (requirement.isEmpty()){
                        checkRequirements = true;
                        break;
                    }
                }

                if (checkRequirements){
                    mStatus = status;
                    enableNotifications(gatt, true);
                } else{
                    mStatus = GATT_FAILS_REQUIREMENTS;
                    disconnect(gatt.getDevice());
                }

                BluetoothGattCallback callback = gattConnectCallbacks.get(gatt);
                if (callback != null)
                    callback.onServicesDiscovered(gatt, mStatus);
            }

        }
    }

    /**
     * Disconnects all GATT servers.
     */
    public void disconnectAll(){
        for (BluetoothGatt gatt:gatts)
            gatt.disconnect();
    }

    /**
     * Disconnecrs all GATT servers associated with the given {@link BluetoothDevice}.
     */
    public void disconnect(BluetoothDevice device){
        for (BluetoothGatt gatt:gatts)
            if (gatt.getDevice().equals(device))
                gatt.disconnect();
    }

    /**
     * Class representing a pending read/write action.
     * In case of a write, the new value must be stored in the {@link BluetoothGattCharacteristic}
     * using {@link BluetoothGattCharacteristic#setValue(byte[])} or equivalent.
     */
    private static class PendingAction{
        int action;
        BluetoothGatt gatt;
        BluetoothGattCharacteristic characteristic;

        PendingAction(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int action){
            this.action = action;
            this.gatt = gatt;
            this.characteristic = characteristic;
        }
    }

    /**
     * Class representing a service-characteristic pair.
     */
    public static class ServiceCharacteristicPair{
        private UUID service;
        private UUID characteristic;

        /**
         * Constructs a {@link ServiceCharacteristicPair} with the given UUIDs.
         */
        public ServiceCharacteristicPair(UUID service, UUID characteristic){
            this.service = service;
            this.characteristic = characteristic;
        }

        /**
         * Constructs a {@link ServiceCharacteristicPair} from the given
         * {@link BluetoothGattService} and {@link BluetoothGattCharacteristic}
         */
        public ServiceCharacteristicPair(BluetoothGattService service,
                                         BluetoothGattCharacteristic characteristic){
            this(service.getUuid(), characteristic.getUuid());
        }

        /**
         * Returns the service.
         */
        public UUID getService() {
            return service;
        }

        /**
         * Returns the characteristic.
         */
        public UUID getCharacteristic() {
            return characteristic;
        }

        /**
         * Checks whether the given nullable {@link UUID}s are the same.
         */
        private boolean equalUUID(UUID a, UUID b){
            if (a == null || b == null){
                return a == null && b == null;
            } else{
                return a.equals(b);
            }
        }

        /**
         * Checks whether the given nullable {@link UUID}s match, considering {@code null} as a
         * wildcard.
         */
        private boolean matchUUID(UUID a, UUID b){
            if (a == null || b == null){
                return true;
            } else{
                return a.equals(b);
            }
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ServiceCharacteristicPair){
                ServiceCharacteristicPair o = (ServiceCharacteristicPair) obj;
                return equalUUID(service, o.getService())
                        && equalUUID(characteristic, o.getCharacteristic());
            } return false;
        }

        /**
         * Checks whether this pair matches the another one. Both service and characteristic must
         * match.
         */
        public boolean matches(ServiceCharacteristicPair o){
            return matchUUID(service, o.getService())
                    && matchUUID(characteristic, o.getCharacteristic());
        }

        /**
         * Checks whether this pair matches the given service-characteristic pair. Both service and
         * characteristic must match.
         */
        public boolean matches(UUID service, UUID characteristic){
            return matchUUID(this.service, service)
                    && matchUUID(this.characteristic, characteristic);
        }

        /**
         * Checks whether this pair matches the given service-characteristic pair. Both service and
         * characteristic must match.
         */
        public boolean matches(BluetoothGattService service,
                               BluetoothGattCharacteristic characteristic){
            return matchUUID(this.service, service.getUuid())
                    && matchUUID(this.characteristic, characteristic.getUuid());
        }
    }
}
