package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.VoidData;

import java.util.UUID;

public abstract class BluetoothLeNotifyLeProvider<T extends Data>
        extends BluetoothLeProvider<VoidData, T> {

    private BluetoothGattCallback callback = new BluetoothNotifyProviderGattCallback();

    /**
     * @param service
     * @param characteristic
     * @param inputType      type of the input data.
     * @param outputType     type of the output data.
     */
    public BluetoothLeNotifyLeProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                                       Class<T> outputType) {
        super(gatt, service, characteristic, VoidData.class, outputType);
    }

    /**
     * @param service
     * @param characteristic
     * @param inputType      type of the input data.
     * @param outputType     type of the output data.
     */
    public BluetoothLeNotifyLeProvider(UUID service, UUID characteristic,
                                       Class<T> outputType) {
        super(null, service, characteristic, VoidData.class, outputType);
    }

    @Override
    public void execute(long requestID, VoidData... input) {
        getBluetoothLeHandler()
                .enableNotification(getGatt(), getService(), getCharacteristic(), true);
    }

    protected abstract T readCharacteristic(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic);

    protected void onChanged(BluetoothGatt gatt,
                        BluetoothGattCharacteristic characteristic){
        T output = readCharacteristic(gatt, characteristic);
        if (output != null)
            publishResultsThreadSafe(output);
    }

    private class BluetoothNotifyProviderGattCallback extends BluetoothGattCallback{
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            onChanged(gatt, characteristic);
        }
    }

    @Override
    public void onAttach() {
        super.onAttach();
        getBluetoothLeHandler().addCallback(getService(), getCharacteristic(), true, callback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getBluetoothLeHandler().removeCallback(callback);
        getBluetoothLeHandler()
                .enableNotification(getGatt(), getService(), getCharacteristic(), false);
    }
}
