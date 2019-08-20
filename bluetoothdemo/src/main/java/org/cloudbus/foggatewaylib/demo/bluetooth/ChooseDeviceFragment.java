package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeHandler;
import org.cloudbus.foggatewaylib.bluetooth.ui.BluetoothDeviceListAdapter;
import org.cloudbus.foggatewaylib.bluetooth.ui.BluetoothDevicesFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPairDevice} interface
 * to handle interaction events.
 * Use the {@link ChooseDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseDeviceFragment extends BluetoothDevicesFragment {
    public static final String TAG = "ChooseDeviceFragment";

    private BluetoothLeHandler bluetoothLeHandler;

    public ChooseDeviceFragment() {
        super(R.layout.fragment_choose_device, R.id.list);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.bluetoothLeHandler = BluetoothLeHandler.getInstance();
        if (getContext() != null){

            for (BluetoothDevice device: bluetoothLeHandler.getConnectedDevices()){
                updateItem(device, BluetoothDeviceListAdapter.Device.STATUS_CONNECTED, null);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.bluetoothLeHandler = null;
    }

    @Override
    public void onItemClick(BluetoothDeviceListAdapter adapter,
                            BluetoothDeviceListAdapter.Device item) {
        if (getActivity() == null)
            return;

        Bundle args = new Bundle();
        args.putString("device_address", item.device.getAddress());
        Navigation.findNavController(getActivity(), R.id.mainNavigationFragment)
                .navigate(R.id.resultFragment, args);
    }
}
