package org.cloudbus.foggatewaylib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;

/**
 * Simple Bluetooth Low Energy adapter.
 * This class simplifies the operations of scan and check of connected devices.
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

    /**
     * Constructs a new {@link SimpleBluetoothLeAdapter}, initializing the
     * {@link BluetoothManager} and the {@link BluetoothAdapter} using the given {@link Context}.
     */
    public SimpleBluetoothLeAdapter(Context context) {
        this.context = context;

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

    /**
     * Checks whether bluetooth is enabled.
     *
     * @return {@code true} if the bluetooth is enabled, {@code false} otherwise.
     */
    public boolean checkBluetooth() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    /**
     * Starts/stops a Bluetooth Low Energy scan.
     * The scan will be automatically stopped after {@link #scanPeriod} milliseconds.
     *
     * @param enable {@code true} to start a scan, {@code false} to stop it.
     * @see #setScanPeriod(int)
     */
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

    /**
     * Starts a Bluetooth Low Energy scan.
     * The scan will be automatically stopped after {@link #scanPeriod} milliseconds.
     * @see #setScanPeriod(int)
     */
    public void startScan(){
        scanLeDevice(true);
    }

    /**
     * Stops a Bluetooth Low Energy scan.
     */
    public void stopScan(){
        scanLeDevice(false);
    }

    /**
     * Sets the duration of the scan in milliseconds.
     */
    public void setScanPeriod(int scanPeriod) {
        this.scanPeriod = scanPeriod;
    }

    /**
     * Sets the callback to be called when new devices are found.
     */
    public void setLeScanCallback(BluetoothAdapter.LeScanCallback leScanCallback) {
        this.leScanCallback = leScanCallback;
    }

    /**
     * Sets the callback to be called when the scan is started and stopped.
     */
    public void setOnScanStartStopListener(OnScanStartStopListener listener){
        mListener = listener;
    }

    /**
     * Returns the duration of the scan in milliseconds.
     */
    public int getScanPeriod() {
        return scanPeriod;
    }

    /**
     * Checks whether the given device is connected.
     *
     * @param device the device
     * @return {@code true} if the {@code device} is connected, {@code false} otherwise.
     */
    public boolean isDeviceConnected(BluetoothDevice device){
        return bluetoothManager.getConnectedDevices(BluetoothProfile.GATT).contains(device);
    }

    /**
     * Checks whether a scan is currently in progress.
     *
     * @return {@code true} if a scan is running, {@code false} otherwise.
     */
    public boolean isScanning(){
        return mScanning;
    }

    /**
     * Interface for receiving callbacks when the scan is started and stopped.
     */
    public interface OnScanStartStopListener{
        /**
         * Called when a scan is started.
         */
        void onScanStart();

        /**
         * Called when a scan is stopped.
         */
        void onScanStop();
    }
}
