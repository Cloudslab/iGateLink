package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeHandler;
import org.cloudbus.foggatewaylib.bluetooth.BluetoothUtils;
import org.cloudbus.foggatewaylib.bluetooth.SimpleBluetoothLeAdapter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPairDevice} interface
 * to handle interaction events.
 * Use the {@link PairBluetoothFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PairBluetoothFragment extends Fragment
        implements BluetoothDeviceListAdapter.OnItemClickListener {
    public static final String TAG = "PairBluetoothFragment";

    private OnPairDevice mListener;
    private BluetoothLeHandler bluetoothLeHandler;
    private BluetoothDeviceListAdapter adapter;
    private SimpleBluetoothLeAdapter bluetoothLeAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private BluetoothGattConnectCallback connectCallback = new BluetoothGattConnectCallback();
    private Set<String> rememberedDevices;
    private FloatingActionButton fab;
    private ContentLoadingProgressBar progressBar;

    private int timeout;


    public PairBluetoothFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pair_bluetooth, container, false);
        recyclerView = rootView.findViewById(R.id.list);

        recyclerView.setHasFixedSize(true);

        adapter = new BluetoothDeviceListAdapter(this);
        recyclerView.setAdapter(adapter);


        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        fab = rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothLeAdapter != null){
                    if(bluetoothLeAdapter.isScanning())
                        bluetoothLeAdapter.stopScan();
                    else{
                        adapter.clear();
                        bluetoothLeAdapter.startScan();
                    }
                }
            }
        });

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        return rootView;
    }

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

            bluetoothLeAdapter.setLeScanCallback(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                    if (rememberedDevices != null
                            && rememberedDevices.contains(bluetoothDevice.getAddress())){
                        connectTo(bluetoothDevice);
                    } else {
                        adapter.updateItem(bluetoothDevice,
                                BluetoothDeviceListAdapter.MyItem.STATUS_DISCONNECTED,
                                null);
                    }
                }
            });

            bluetoothLeAdapter.setOnScanStartStopListener(
                    new SimpleBluetoothLeAdapter.OnScanStartStopListener() {
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
                    });
            bluetoothLeAdapter.startScan();

            rememberedDevices = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getStringSet("remembered_devices", new HashSet<String>());

            for (BluetoothDevice device: bluetoothLeHandler.getConnectedDevices()){
                adapter.updateItem(device,
                        BluetoothDeviceListAdapter.MyItem.STATUS_CONNECTED,
                        null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        this.bluetoothLeHandler.removeGattConnectCallback(connectCallback);
        this.bluetoothLeHandler = null;

        bluetoothLeAdapter.stopScan();
        this.bluetoothLeAdapter = null;
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(BluetoothDeviceListAdapter adapter,
                            BluetoothDeviceListAdapter.MyItem item) {
        switch (item.status){
            case BluetoothDeviceListAdapter.MyItem.STATUS_CONNECTED:
                disconnectFrom(item.device);
            case BluetoothDeviceListAdapter.MyItem.STATUS_ERROR:
            case BluetoothDeviceListAdapter.MyItem.STATUS_DISCONNECTED:
                connectTo(item.device);
                break;

            case BluetoothDeviceListAdapter.MyItem.STATUS_CONNECTING:
            case BluetoothDeviceListAdapter.MyItem.STATUS_DISCONNECTING:
            default:
                break;
        }
    }

    private void connectTo(BluetoothDevice device){
        if (bluetoothLeHandler != null && getContext() != null){
            if (bluetoothLeHandler.getContext() == null)
                bluetoothLeHandler.setContext(getContext());
            bluetoothLeHandler.connectGatt(device, connectCallback,
                    mListener.getRequirements());

            adapter.updateItem(device,
                    BluetoothDeviceListAdapter.MyItem.STATUS_CONNECTING,
                    null);
        }
    }

    private void disconnectFrom(BluetoothDevice device){
        if (bluetoothLeHandler != null && getContext() != null){
            if (bluetoothLeHandler.getContext() == null)
                bluetoothLeHandler.setContext(getContext());
            bluetoothLeHandler.disconnect(device);

            adapter.updateItem(device,
                    BluetoothDeviceListAdapter.MyItem.STATUS_DISCONNECTING,
                    null);
        }
    }

    private class BluetoothGattConnectCallback extends BluetoothGattCallback{
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, int newState) {
            Log.d(TAG, "Received onConnectionStateChange");

            if (adapter == null || getActivity() == null) {
                return;
            }

            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.MyItem.STATUS_CONNECTED,
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
                        adapter.updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.MyItem.STATUS_DISCONNECTED,
                                null);
                    }
                });
                forgetDevice(gatt.getDevice().getAddress());

            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = "Error connecting to bluetooth device.";

                        adapter.updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.MyItem.STATUS_ERROR,
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
                        adapter.updateItem(gatt.getDevice(),
                                BluetoothDeviceListAdapter.MyItem.STATUS_ERROR,
                                text);
                    }
                });
            }
        }
    }

    private void rememberDevice(String address){
        if (getContext() == null)
            return;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> rememberedDevices = prefs.getStringSet("remembered_devices",
                new HashSet<String>());
        rememberedDevices.add(address);
        prefs.edit().putStringSet("remembered_devices", rememberedDevices).apply();
    }

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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPairDevice {
        void onDevicePaired(BluetoothGatt gatt);
        Set<Pair<UUID, UUID>>[] getRequirements();
    }
}
