package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.VoidData;

import java.util.UUID;

/**
 * {@link BluetoothLeProvider} that writes to the given GATT server, service and characteristic
 * when executed and calls {@link #onWrite(BluetoothGatt, BluetoothGattCharacteristic, int)}
 * when the operation is completed.
 *
 * @param <T> the type of the input data
 *
 * @author Riccardo Mancini
 */
//TODO requestID is ignored
//TODO not tested
public abstract class BluetoothLeWriteProvider<T extends Data>
        extends BluetoothLeProvider<T, VoidData> {

    private BluetoothGattCallback callback = new BluetoothWriteProviderGattCallback();

    /**
     * Constructs a provider with the given parameters.
     */
    public BluetoothLeWriteProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                                    Class<T> inputType) {
        super(gatt, service, characteristic, inputType, VoidData.class);
    }

    /**
     * Constructs a provider with the given parameters that listens to all possible devices.
     */
    public BluetoothLeWriteProvider(UUID service, UUID characteristic, Class<T> inputType) {
        super(null, service, characteristic, inputType, VoidData.class);
    }

    /**
     * Initiates a write of given input (converted into bytes by {@link #dataToBytes(Data[])}).
     */
    @Override
    public void execute(long requestID, T... input) {
        getBluetoothLeHandler()
                .writeCharacteristic(null, getService(),
                        getCharacteristic(), dataToBytes(input));
    }

    /**
     * Converts the input data to a byte array that can be written in the
     * {@link BluetoothGattCharacteristic}.
     *
     * @param data the input data
     * @return the bytes to be written
     */
    protected abstract byte[] dataToBytes(T... data);


    /**
     * When a write operation is completed, this function will be called.
     */
    protected void onWrite(BluetoothGatt gatt,
                                 BluetoothGattCharacteristic characteristic,
                                 int status){}

    /**
     * Custom callback that calls {@link #onWrite(BluetoothGatt, BluetoothGattCharacteristic, int)}
     * at {@link BluetoothGattCallback#onCharacteristicWrite(BluetoothGatt, BluetoothGattCharacteristic, int)}.
     */
    private class BluetoothWriteProviderGattCallback extends BluetoothGattCallback{
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            onWrite(gatt, characteristic, status);
        }
    }

    /**
     * When attached to a {@link org.cloudbus.foggatewaylib.core.ExecutionManager}, adds the
     * callback to the {@link BluetoothLeHandler}.
     */
    @Override
    public void onAttach() {
        super.onAttach();
        getBluetoothLeHandler().addCallback(getService(), getCharacteristic(), false, callback);
    }

    /**
     * When detached from a {@link org.cloudbus.foggatewaylib.core.ExecutionManager}, removes the
     * callback from the {@link BluetoothLeHandler}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        getBluetoothLeHandler().removeCallback(callback);
    }
}
