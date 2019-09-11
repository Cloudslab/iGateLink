package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import androidx.annotation.CallSuper;

import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.VoidData;

import java.util.UUID;

/**
 * {@link BluetoothLeProvider} that reads from the given GATT server, service and characteristic
 * when executed and calls {@link #readCharacteristic(BluetoothGatt, BluetoothGattCharacteristic)}
 * when the value is retrieved.
 *
 * @param <T> the type of the output data
 *
 * @author Riccardo Mancini
 */
//TODO requestID is ignored
//TODO not tested
public abstract class BluetoothLeReadProvider<T extends Data>
        extends BluetoothLeProvider<VoidData, T> {

    private BluetoothGattCallback callback = new BluetoothReadProviderGattCallback();

    /**
     * Constructs a provider with the given parameters.
     */
    public BluetoothLeReadProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                                   Class<T> outputType) {
        super(gatt, service, characteristic, VoidData.class, outputType);
    }

    /**
     * Constructs a provider with the given parameters that listens to all possible devices.
     */
    public BluetoothLeReadProvider(UUID service, UUID characteristic,
                                   Class<T> outputType) {
        super(null, service, characteristic, VoidData.class, outputType);
    }

    /**
     * Initiates a read.
     */
    @Override
    public void execute(long requestID, VoidData... input) {
        getBluetoothLeHandler().readCharacteristic(getGatt(), getService(), getCharacteristic());
    }

    /**
     * Called when the characteristic value is read, provides the output to be published.
     * You can retrieve the value of the characteristic from within the method through
     * {@link BluetoothGattCharacteristic#getValue()} (or equivalent).
     *
     * @param gatt the GATT server in which the read occurred.
     * @param characteristic the read characteristic.
     * @see BluetoothGattCharacteristic#getValue()
     * @see BluetoothGattCallback#onCharacteristicRead(BluetoothGatt, BluetoothGattCharacteristic, int)
     */
    protected abstract T readCharacteristic(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic);

    /**
     * Called when the characteristic value is read, gets the output to be published and publishes
     * it, if the {@code status} is {@link BluetoothGatt#GATT_SUCCESS}.
     *
     * @param gatt the GATT server in which the read occurred.
     * @param characteristic the read characteristic.
     * @param status {@link BluetoothGatt#GATT_SUCCESS} if the read was successful, something else
     *               otherwise.
     * @see BluetoothGattCallback#onCharacteristicRead(BluetoothGatt, BluetoothGattCharacteristic, int)
     */
    @CallSuper
    protected void onRead(BluetoothGatt gatt,
                        BluetoothGattCharacteristic characteristic,
                        int status){
        if (status == BluetoothGatt.GATT_SUCCESS){
            T output = readCharacteristic(gatt, characteristic);
            if (output != null)
                publishResults(output);
        }
    }

    /**
     * Custom callback that calls {@link #onRead(BluetoothGatt, BluetoothGattCharacteristic, int)}
     * at {@link BluetoothGattCallback#onCharacteristicRead(BluetoothGatt, BluetoothGattCharacteristic, int)}.
     */
    private class BluetoothReadProviderGattCallback extends BluetoothGattCallback{
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            onRead(gatt, characteristic, status);
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
