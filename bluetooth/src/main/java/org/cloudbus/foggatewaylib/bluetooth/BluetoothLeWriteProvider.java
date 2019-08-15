package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.VoidData;

import java.util.UUID;

public abstract class BluetoothLeWriteProvider<T extends Data>
        extends BluetoothLeProvider<T, VoidData> {

    private BluetoothGattCallback callback = new BluetoothWriteProviderGattCallback();

    /**
     * @param service
     * @param characteristic
     * @param inputType      type of the input data.
     * @param inputType     type of the output data.
     */
    public BluetoothLeWriteProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                                    Class<T> inputType) {
        super(gatt, service, characteristic, inputType, VoidData.class);
    }

    /**
     * @param service
     * @param characteristic
     * @param inputType      type of the input data.
     * @param inputType     type of the output data.
     */
    public BluetoothLeWriteProvider(UUID service, UUID characteristic, Class<T> inputType) {
        super(null, service, characteristic, inputType, VoidData.class);
    }

    @Override
    public void execute(long requestID, T... input) {
        getBluetoothLeHandler()
                .writeCharacteristic(null, getService(),
                        getCharacteristic(), dataToBytes(input));
    }

    protected abstract byte[] dataToBytes(T... data);

    protected void onWrite(BluetoothGatt gatt,
                                 BluetoothGattCharacteristic characteristic,
                                 int status){}

    private class BluetoothWriteProviderGattCallback extends BluetoothGattCallback{
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            onWrite(gatt, characteristic, status);
        }
    }

    @Override
    public void onAttach() {
        super.onAttach();
        getBluetoothLeHandler().addCallback(getService(), getCharacteristic(), false, callback);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getBluetoothLeHandler().removeCallback(callback);
    }
}
