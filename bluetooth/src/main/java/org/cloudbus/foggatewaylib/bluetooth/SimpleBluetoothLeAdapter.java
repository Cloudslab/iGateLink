package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;

/**
 * Simple Bluetooth Low Energy adapter.
 *
 * @see <a href="https://developer.android.com/guide/topics/connectivity/bluetooth-le#java">https://developer.android.com/guide/topics/connectivity/bluetooth-le#java</a>
 *
 * @author Riccardo Mancini
 */
public class SimpleBluetoothLeAdapter {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private Context context;
    private BluetoothAdapter.LeScanCallback leScanCallback;

    private boolean mScanning = false;
    private Handler handler = new Handler();
    private int scanPeriod = 10000;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
            if (mListener != null)
                mListener.onScanStop();
        }
    };

    private OnScanStartStopListener mListener;

    public SimpleBluetoothLeAdapter(Context context) {
        this.context = context;

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean checkBluetooth() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    private void scanLeDevice(final boolean enable) {
        if (leScanCallback == null)
            throw new RuntimeException("You have to set a scan callback using setLeScanCallback!");
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(mRunnable, scanPeriod);

            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            handler.removeCallbacks(mRunnable);
            bluetoothAdapter.stopLeScan(leScanCallback);
        }

        if (mListener != null){
            if (enable)
                mListener.onScanStart();
            else
                mListener.onScanStop();
        }
    }

    public void startScan(){
        scanLeDevice(true);
    }

    public void stopScan(){
        scanLeDevice(false);
    }

    public void setScanPeriod(int scanPeriod) {
        this.scanPeriod = scanPeriod;
    }

    public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
        this.leScanCallback = leScanCallback;
    }

    public void setOnScanStartStopListener(OnScanStartStopListener listener){
        mListener = listener;
    }

    public int getScanPeriod() {
        return scanPeriod;
    }

    public boolean isDeviceConnected(BluetoothDevice device){
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).contains(device);
    }

    public boolean isScanning(){
        return mScanning;
    }

    public interface OnScanStartStopListener{
        void onScanStart();
        void onScanStop();
    }
}
