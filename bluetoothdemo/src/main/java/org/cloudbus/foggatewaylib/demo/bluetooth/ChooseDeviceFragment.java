package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.navigation.Navigation;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeHandler;
import org.cloudbus.foggatewaylib.bluetooth.ui.BluetoothDeviceListAdapter;
import org.cloudbus.foggatewaylib.bluetooth.ui.BluetoothDevicesFragment;

/**
 * Fragment for choosing a device to show stats about.
 *
 * @author Riccardo Mancini
 */
public class ChooseDeviceFragment extends BluetoothDevicesFragment {
    public static final String TAG = "ChooseDeviceFragment";

    private BluetoothLeHandler bluetoothLeHandler;

    public ChooseDeviceFragment() {
        // This fragment uses the fragment_choose_device layout and the devices must populate
        // the list with id `list`.
        super(R.layout.fragment_choose_device, R.id.list);
    }

    /**
     * Once the fragment is shown, update its list of connected devices.
     */
    @Override
    public void onResume() {
        super.onResume();
        this.bluetoothLeHandler = BluetoothLeHandler.getInstance();

        // remove any existing device
        clearAdapter();

        // add connected devices to the list
        for (BluetoothDevice device: bluetoothLeHandler.getConnectedDevices()){
            updateItem(device, BluetoothDeviceListAdapter.Device.STATUS_CONNECTED, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.bluetoothLeHandler = null;
    }

    /**
     * When an item is clicked, start a {@link ResultFragment} with the given
     * {@code device_address}.
     */
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
