package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.cloudbus.foggatewaylib.Data;
import org.cloudbus.foggatewaylib.VoidData;

import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BluetoothLeReadProvider<T extends Data>
        extends BluetoothLeProvider<VoidData, T> {

    private BluetoothGattCallback callback = new BluetoothReadProviderGattCallback();

    /**
     * @param service
     * @param characteristic
     * @param inputType      type of the input data.
     * @param outputType     type of the output data.
     */
    public BluetoothLeReadProvider(BluetoothGatt gatt, UUID service, UUID characteristic,
                                   Class<T> outputType) {
        super(gatt, service, characteristic, VoidData.class, outputType);
    }

    /**
     * @param service
     * @param characteristic
     * @param inputType      type of the input data.
     * @param outputType     type of the output data.
     */
    public BluetoothLeReadProvider(UUID service, UUID characteristic,
                                   Class<T> outputType) {
        super(null, service, characteristic, VoidData.class, outputType);
    }

    @Override
    public void execute(long requestID, VoidData... input) {
        getBluetoothLeHandler().readCharacteristic(getGatt(), getService(), getCharacteristic());
    }

    protected abstract T readCharacteristic(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic);

    protected void onRead(BluetoothGatt gatt,
                        BluetoothGattCharacteristic characteristic,
                        int status){
        if (status == BluetoothGatt.GATT_SUCCESS){
            T output = readCharacteristic(gatt, characteristic);
            if (output != null)
                publishResultsThreadSafe(output);
        }
    }

    private class BluetoothReadProviderGattCallback extends BluetoothGattCallback{
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            onRead(gatt, characteristic, status);
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
