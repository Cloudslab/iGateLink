package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.util.Pair;

import androidx.annotation.RequiresApi;

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
 *
 * @author Riccardo Mancini
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeHandler {

    private static final int ACTION_READ = 1;
    private static final int ACTION_WRITE = 2;

    public static final int GATT_FAILS_REQUIREMENTS = 1;

    private static BluetoothLeHandler instance;

    private Context context;

    private Set<BluetoothGatt> gatts = new HashSet<>();
    private Map<Pair<UUID, UUID>, BluetoothGattCallback> callbacks = new HashMap<>();
    private Map<BluetoothGatt, BluetoothGattCallback> gattConnectCallbacks = new HashMap<>();
    private Map<BluetoothGatt, List<Set<Pair<UUID, UUID>>>> gattRequirements = new HashMap<>();
    private Set<Pair<UUID, UUID>> notifyMeServiceCharacteristicPairs = new HashSet<>();

    private Queue<PendingAction> queue = new LinkedList<>();
    private PendingAction actionInProgress = null;

    private GeneralCallback mCallback = new GeneralCallback();

    private SimpleBluetoothLeAdapter bluetoothLeAdapter;

    public static BluetoothLeHandler getInstance() {
        if (instance == null)
            instance = new BluetoothLeHandler();
        return instance;
    }

    public Set<BluetoothGatt> getGatts() {
        return gatts;
    }

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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
        if (context != null)
            bluetoothLeAdapter = new SimpleBluetoothLeAdapter(context);
        else
            bluetoothLeAdapter = null;
    }

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

    private synchronized void doAction(BluetoothGatt gatt,
                                       BluetoothGattCharacteristic characteristic,
                                       int action){
        queue.add(new PendingAction(gatt, characteristic, action));
        if (actionInProgress == null)
            nextAction();
    }

    public boolean addCallback(UUID service, UUID characteristic, boolean notify,
                            BluetoothGattCallback callback){
        Pair<UUID, UUID> pair = new Pair<>(service, characteristic);
        boolean result = callbacks.put(pair, callback) != null;
        if (notify){
            notifyMeServiceCharacteristicPairs.add(pair);
            enableNotification(null, service, characteristic, true);
        }
        return result;

    }

    public int removeCallback(BluetoothGattCallback callback){
        Set<Pair<UUID, UUID>> keysToRemove = new HashSet<>();
        for (Pair<UUID, UUID> key:callbacks.keySet()){
            if (callback.equals(callbacks.get(key))){
                keysToRemove.add(key);
            }
        }
        for (Pair<UUID, UUID> key:keysToRemove){
            callbacks.remove(key);
        }
        return keysToRemove.size();
    }

    public boolean connectGatt(BluetoothDevice device,
                               BluetoothGattCallback connectionStateCallback,
                               Set<Pair<UUID, UUID>>... requirements){
        if (context == null)
            throw new IllegalArgumentException("Context must be set before connecting.");

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

    public void readCharacteristic(BluetoothGatt readGatt, UUID readService,
                                   UUID readCharacteristic){
        Pair<UUID, UUID> pair = new Pair<>(readService, readCharacteristic);
        Collection<BluetoothGatt> gattCollection;

        if (readGatt == null)
            gattCollection = gatts;
        else
            gattCollection = Collections.singletonList(readGatt);

        for (BluetoothGatt gatt:gattCollection){
            for (BluetoothGattService service:gatt.getServices()){
                for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                    if (checkPairMatch(pair, service.getUuid(), characteristic.getUuid())){
                        doAction(gatt, characteristic, ACTION_READ);
                    }
                }
            }
        }
    }

    public void writeCharacteristic(BluetoothGatt writeGatt, UUID readService,
                                    UUID readCharacteristic, byte[] newValue){
        Pair<UUID, UUID> pair = new Pair<>(readService, readCharacteristic);
        Collection<BluetoothGatt> gattCollection;

        if (writeGatt == null)
            gattCollection = gatts;
        else
            gattCollection = Collections.singletonList(writeGatt);

        for (BluetoothGatt gatt:gattCollection){
            for (BluetoothGattService service:gatt.getServices()){
                for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                    if (checkPairMatch(pair, service.getUuid(), characteristic.getUuid())){
                        characteristic.setValue(newValue);
                        doAction(gatt, characteristic, ACTION_WRITE);
                    }
                }
            }
        }
    }

    public void enableNotifications(boolean enable){
        for (BluetoothGatt gatt:gatts){
            enableNotifications(gatt, enable);
        }
    }

    public void enableNotifications(BluetoothGatt gatt, boolean enable){
        for (BluetoothGattService service:gatt.getServices()){
            for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                if(!getMatchingPairs(notifyMeServiceCharacteristicPairs, service.getUuid(),
                        characteristic.getUuid()).isEmpty()){
                    gatt.setCharacteristicNotification(characteristic, enable);
                }
            }
        }
    }


    public void enableNotification(BluetoothGatt notifyGatt, UUID readService,
                                   UUID readCharacteristic, boolean enable){
        Pair<UUID, UUID> pair = new Pair<>(readService, readCharacteristic);
        Collection<BluetoothGatt> gattCollection;

        if (notifyGatt == null)
            gattCollection = gatts;
        else
            gattCollection = Collections.singletonList(notifyGatt);

        for (BluetoothGatt gatt:gattCollection){
            for (BluetoothGattService service:gatt.getServices()){
                for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                    if (checkPairMatch(pair, service.getUuid(), characteristic.getUuid())){
                        gatt.setCharacteristicNotification(characteristic, enable);
                    }
                }
            }
        }
    }

    private boolean checkPairMatch(Pair<UUID, UUID> pair,
                                                   UUID service, UUID characteristic){
        return (pair.first == null || service == null || pair.first.equals(service))
            && (pair.second == null || characteristic == null|| pair.second.equals(characteristic));
    }

    private Set<Pair<UUID, UUID>> getMatchingPairs(Set<Pair<UUID, UUID>> pairs,
                                                        UUID service, UUID characteristic){
        Set<Pair<UUID, UUID>> result = new HashSet<>();
        for (Pair<UUID, UUID> pair:pairs) {
            if (checkPairMatch(pair, service, characteristic)) {
                result.add(pair);
            }
        }
        return result;
    }

    private Set<BluetoothGattCallback> getMatchingCallbacks(
            BluetoothGattCharacteristic characteristic){
        Set<BluetoothGattCallback> result = new HashSet<>();
        for (Pair<UUID, UUID> key:getMatchingPairs(callbacks.keySet(),
                                                   characteristic.getService().getUuid(),
                                                   characteristic.getUuid())) {
            result.add(callbacks.get(key));
        }
        return result;
    }

    private class GeneralCallback extends BluetoothGattCallback{

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            for (BluetoothGattCallback callback:getMatchingCallbacks(characteristic)){
                callback.onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            for (BluetoothGattCallback callback:getMatchingCallbacks(characteristic)){
                callback.onCharacteristicRead(gatt, characteristic, status);
            }
            nextAction();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                         int status) {
            for (BluetoothGattCallback callback:getMatchingCallbacks(characteristic)){
                callback.onCharacteristicWrite(gatt, characteristic, status);
            }
            nextAction();
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED)
                gatt.discoverServices();

            BluetoothGattCallback callback = gattConnectCallbacks.get(gatt);
            if (callback != null)
                callback.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            int mStatus;
            
            if (status == BluetoothGatt.GATT_SUCCESS){
                List<Set<Pair<UUID, UUID>>> requirements = gattRequirements.get(gatt);
                if (requirements != null && !requirements.isEmpty()){
                    for (BluetoothGattService service:gatt.getServices()){
                        for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                            for (Set<Pair<UUID, UUID>> requirement:requirements){
                                requirement.removeAll(getMatchingPairs(requirement,
                                                                       service.getUuid(),
                                                                       characteristic.getUuid()));
                            }
                        }
                    }
                }

                boolean checkRequirements = false;

                for (Set<Pair<UUID, UUID>> requirement:requirements) {
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

    public void disconnectAll(){
        for (BluetoothGatt gatt:gatts)
            gatt.disconnect();
    }

    public void disconnect(BluetoothDevice device){
        for (BluetoothGatt gatt:gatts)
            if (gatt.getDevice().equals(device))
                gatt.disconnect();
    }

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
}
