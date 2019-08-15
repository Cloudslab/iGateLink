package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import androidx.annotation.CallSuper;

import org.cloudbus.foggatewaylib.core.Data;
import org.cloudbus.foggatewaylib.core.VoidData;

import java.util.UUID;

/**
 * {@link BluetoothLeProvider} that enables notifications on the given GATT server, service and
 * characteristic and calls {@link #readCharacteristic(BluetoothGatt, BluetoothGattCharacteristic)}
 * when it changes.
 *
 * @param <T> the type of the output data
 *
 * @author Riccardo Mancini
 */
public abstract class BluetoothLeNotifyLeProvider<T extends Data>
        extends BluetoothLeProvider<VoidData, T> {

    private BluetoothGattCallback callback = new BluetoothNotifyProviderGattCallback();

    /**
     * Constructs a provider with the given parameters.
     */
    public BluetoothLeNotifyLeProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                                       Class<T> outputType) {
        super(gatt, service, characteristic, VoidData.class, outputType);
    }

    /**
     * Constructs a provider with the given parameters that listens to all possible devices.
     */
    public BluetoothLeNotifyLeProvider(UUID service, UUID characteristic,
                                       Class<T> outputType) {
        super(null, service, characteristic, VoidData.class, outputType);
    }

    /**
     * It may never be used but all it does is re-enabling notifications.
     */
    @Override
    public void execute(long requestID, VoidData... input) {
        getBluetoothLeHandler()
                .enableNotification(getGatt(), getService(), getCharacteristic(), true);
    }

    /**
     * Called when the characteristic value changes, provides the output to be published.
     *
     * @param gatt the GATT server in which the change occurred.
     * @param characteristic the updated characteristic.
     * @see BluetoothGattCharacteristic#getValue()
     * @see BluetoothGattCallback#onCharacteristicChanged(BluetoothGatt, BluetoothGattCharacteristic)
     */
    protected abstract T readCharacteristic(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic);

    /**
     * Called when the characteristic value changes, gets the output to be published and publishes
     * it.
     *
     * @param gatt the GATT server in which the change occurred.
     * @param characteristic the updated characteristic.
     * @see BluetoothGattCallback#onCharacteristicChanged(BluetoothGatt, BluetoothGattCharacteristic)
     */
    @CallSuper
    @SuppressWarnings("unchecked")
    protected void onChanged(BluetoothGatt gatt,
                        BluetoothGattCharacteristic characteristic){
        T output = readCharacteristic(gatt, characteristic);
        if (output != null)
            publishResultsThreadSafe(output);
    }

    /**
     * Custom callback that calls {@link #onChanged(BluetoothGatt, BluetoothGattCharacteristic)}
     * at {@link BluetoothGattCallback#onCharacteristicChanged(BluetoothGatt, BluetoothGattCharacteristic)}.
     */
    private class BluetoothNotifyProviderGattCallback extends BluetoothGattCallback{
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            onChanged(gatt, characteristic);
        }
    }

    /**
     * When attached to a {@link org.cloudbus.foggatewaylib.core.ExecutionManager}, adds the
     * callback to the {@link BluetoothLeHandler} and enables notifications.
     */
    @Override
    public void onAttach() {
        super.onAttach();
        getBluetoothLeHandler().addCallback(getService(), getCharacteristic(), true, callback);
    }

    /**
     * When detached from a {@link org.cloudbus.foggatewaylib.core.ExecutionManager}, removes the
     * callback from the {@link BluetoothLeHandler} and disables notifications.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        getBluetoothLeHandler().removeCallback(callback);
        getBluetoothLeHandler()
                .enableNotification(getGatt(), getService(), getCharacteristic(), false);
    }
}
