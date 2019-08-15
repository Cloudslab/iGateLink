package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeNotifyProvider;

import java.util.UUID;

public class BluetoothLeOximeterLeNotifyProvider extends BluetoothLeNotifyProvider<OximeterData> {
    public static final UUID SERVICE_UUID = UUID.fromString("cdeacb80-5235-4c07-8846-93a37ee6b86d");
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("cdeacb81-5235-4c07-8846-93a37ee6b86d");

    public BluetoothLeOximeterLeNotifyProvider() {
        super(null, SERVICE_UUID, CHARACTERISTIC_UUID, OximeterData.class);
    }

    @Override
    protected OximeterData readCharacteristic(BluetoothGatt gatt,
                                                     BluetoothGattCharacteristic characteristic) {
        return OximeterData.fromJumper(gatt.getDevice().getAddress(), characteristic.getValue());
    }
}
