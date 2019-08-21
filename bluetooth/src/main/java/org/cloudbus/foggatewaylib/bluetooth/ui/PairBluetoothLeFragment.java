package org.cloudbus.foggatewaylib.bluetooth.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.widget.ContentLoadingProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeHandler;
import org.cloudbus.foggatewaylib.bluetooth.BluetoothUtils;
import org.cloudbus.foggatewaylib.bluetooth.R;
import org.cloudbus.foggatewaylib.bluetooth.SimpleBluetoothLeAdapter;

import java.util.HashSet;
import java.util.Set;


/**
 * A fragment that handles the pairing to Bluetooth LE devices.
 * An {@link android.app.Activity} using this fragment MUST implement the {@link OnPairDevice}
 * interface.
 *
 * @author Riccardo Mancini
 */
public class PairBluetoothLeFragment extends BluetoothDevicesFragment {
    public static final String TAG = "PairBluetoothLeFragment";

    private OnPairDevice mListener;
    private BluetoothLeHandler bluetoothLeHandler;
    private SimpleBluetoothLeAdapter bluetoothLeAdapter;
    private BluetoothGattConnectCallback connectCallback = new BluetoothGattConnectCallback();
    private Set<String> rememberedDevices;
    private FloatingActionButton fab;
    private ContentLoadingProgressBar progressBar;

    private int timeout;

    /**
     * Called when a new device is found, it connects to it if it is in the
     * {@link #rememberedDevices} list, otherwise it just adds it to the list view.
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            if (rememberedDevices != null
                    && rememberedDevices.contains(bluetoothDevice.getAddress())){
                connectTo(bluetoothDevice);
            } else {
                updateItem(bluetoothDevice,
                        BluetoothDeviceListAdapter.Device.STATUS_DISCONNECTED,
                        null);
            }
        }
    };

    /**
     * Called when scan is started or stopped, updates the views.
     */
    private SimpleBluetoothLeAdapter.OnScanStartStopListener mScanStartStopListener
            = new SimpleBluetoothLeAdapter.OnScanStartStopListener() {
                        @Override
                        public void onScanStart() {
                            fab.setImageResource(R.drawable.ic_stop_white_24dp);
                            progressBar.show();
                        }

                        @Override
                        public void onScanStop() {
                            fab.setImageResource(R.drawable.ic_refresh_white_24dp);
                            progressBar.hide();
                        }
                    };

    /**
     * Default constructor.
     */
    public PairBluetoothLeFragment() {
        super(R.layout.fragment_pair_bluetooth, R.id.list);
    }

    /**
     * Creates the view and sets the references to the views.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothLeAdapter != null){
                    if(bluetoothLeAdapter.isScanning())
                        bluetoothLeAdapter.stopScan();
                    else{
                        clearAdapter();
                        bluetoothLeAdapter.startScan();
                    }
                }
            }
        });

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        return rootView;
    }

    /**
     * Initializes {@link #bluetoothLeAdapter} and {@link #bluetoothLeHandler}.
     */
    @Override
    public void onResume() {
        super.onResume();
        this.bluetoothLeHandler = BluetoothLeHandler.getInstance();
        if (getContext() != null){
            String timeoutString = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getString("bluetooth_scan_timeout", "10");

            try{
                timeout = Integer.valueOf(timeoutString)*1000;
            } catch (NumberFormatException e){
                timeout = 10000;
            }

            bluetoothLeAdapter = new SimpleBluetoothLeAdapter(getContext());
            bluetoothLeAdapter.setScanPeriod(timeout);

            if (!bluetoothLeAdapter.checkBluetooth()){
                BluetoothUtils.askEnableBluetooth(getActivity(), 2);
            }

            bluetoothLeAdapter.setLeScanCallback(mLeScanCallback);

            bluetoothLeAdapter.setOnScanStartStopListener(mScanStartStopListener);
            bluetoothLeAdapter.startScan();

            rememberedDevices = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getStringSet("remembered_devices", new HashSet<String>());

            for (BluetoothDevice device: bluetoothLeHandler.getConnectedDevices()){
                updateItem(device,
                        BluetoothDeviceListAdapter.Device.STATUS_CONNECTED,
                        null);
            }
        }
    }

    /**
     * Removes callbacks, stops scan and dereferences {@link #bluetoothLeAdapter} and
     * {@link #bluetoothLeHandler}.
     */
    @Override
    public void onPause() {
        super.onPause();

        this.bluetoothLeHandler.removeGattConnectCallback(connectCallback);
        this.bluetoothLeHandler = null;

        bluetoothLeAdapter.stopScan();
        bluetoothLeAdapter.setLeScanCallback(null);
        bluetoothLeAdapter.setOnScanStartStopListener(null);
        this.bluetoothLeAdapter = null;
    }

    /**
     * Sets the reference to the {@link OnPairDevice} interface.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPairDevice) {
            mListener = (OnPairDevice) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPairDevice");
        }

    }

    /**
     * Removes reference to the {@link OnPairDevice} interface.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Connects/disconnects to/from device when the corresponding list item is clicked.
     */
    @Override
    public void onItemClick(BluetoothDeviceListAdapter adapter,
                            BluetoothDeviceListAdapter.Device item) {
        switch (item.status){
            case BluetoothDeviceListAdapter.Device.STATUS_CONNECTED:
                disconnectFrom(item.device);
            case BluetoothDeviceListAdapter.Device.STATUS_ERROR:
            case BluetoothDeviceListAdapter.Device.STATUS_DISCONNECTED:
                connectTo(item.device);
                break;

            case BluetoothDeviceListAdapter.Device.STATUS_CONNECTING:
            case BluetoothDeviceListAdapter.Device.STATUS_DISCONNECTING:
            default:
                break;
        }
    }

    /**
     * Connects to the {@code device}.
     */
    private void connectTo(BluetoothDevice device){
        if (bluetoothLeHandler != null && getContext() != null){
            if (bluetoothLeHandler.getContext() == null)
                bluetoothLeHandler.setContext(getContext());
            bluetoothLeHandler.connectGatt(device, connectCallback,
                    mListener.getRequirements());

            updateItem(device,
                    BluetoothDeviceListAdapter.Device.STATUS_CONNECTING,
                    null);
        }
    }

    /**
     * Disconnects from the {@code device}.
     */
    private void disconnectFrom(BluetoothDevice device){
        if (bluetoothLeHandler != null && getContext() != null){
            if (bluetoothLeHandler.getContext() == null)
                bluetoothLeHandler.setContext(getContext());
            bluetoothLeHandler.disconnect(device);

            updateItem(device,
                    BluetoothDeviceListAdapter.Device.STATUS_DISCONNECTING,
                    null);
        }
    }

    /**
     * Internal callback for handling new-device connections and disconnections.
     */
    private class BluetoothGattConnectCallback extends BluetoothGattCallback{
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, int newState) {
            Log.d(TAG, "Received onConnectionStateChange");

            if (getAdapter() == null || getActivity() == null) {
                return;
            }

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.Device.STATUS_CONNECTED,
                                null);
                    }
                });

                if (mListener != null)
                    mListener.onDevicePaired(gatt);
                rememberDevice(gatt.getDevice().getAddress());
            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothGatt.STATE_DISCONNECTED) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.Device.STATUS_DISCONNECTED,
                                null);
                    }
                });
                forgetDevice(gatt.getDevice().getAddress());

            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "Error connecting to bluetooth device.";

                        updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.Device.STATUS_ERROR,
                                text);
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothLeHandler.GATT_FAILS_REQUIREMENTS){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "Device is not of the required type.";
                        updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.Device.STATUS_ERROR,
                                text);
                    }
                });
            }
        }
    }

    /**
     * Saves the device in a {@link android.preference.Preference}.
     */
    private void rememberDevice(String address){
        if (getContext() == null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> rememberedDevices = prefs.getStringSet("remembered_devices",
                new HashSet<String>());
        rememberedDevices.add(address);
        prefs.edit().putStringSet("remembered_devices", rememberedDevices).apply();
    }

    /**
     * Removes the given device address from the {@link android.preference.Preference}.
     */
    private void forgetDevice(String address){
        if (getContext() == null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> rememberedDevices = prefs.getStringSet("remembered_devices",
                new HashSet<String>());
        rememberedDevices.remove(address);
        prefs.edit().putStringSet("remembered_devices", rememberedDevices).apply();
    }

    /**
     * Interface to be implemented by the {@link android.app.Activity} in which this fragment runs.
     */
    public interface OnPairDevice {
        /**
         * Called when a device has been paired.
         *
         * @param gatt the GATT server that has been connected.
         */
        void onDevicePaired(BluetoothGatt gatt);

        /**
         * Returns the array of the requirements that connected devices must satisfy.
         * Please refer to the link below.
         *
         * @see BluetoothLeHandler#connectGatt(BluetoothDevice, BluetoothGattCallback, Set[])
         */
        Set<BluetoothLeHandler.ServiceCharacteristicPair>[] getRequirements();
    }
}
