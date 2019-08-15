package org.cloudbus.foggatewaylib.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Utility functions for Bluetooth.
 *
 * @see <a href="https://developer.android.com/guide/topics/connectivity/bluetooth-le#java">https://developer.android.com/guide/topics/connectivity/bluetooth-le#java</a>
 *
 * @author Riccardo Mancini
 */
public class BluetoothUtils {

    public static void askEnableBluetooth(Activity activity, int requestCode) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, requestCode);
    }
}
