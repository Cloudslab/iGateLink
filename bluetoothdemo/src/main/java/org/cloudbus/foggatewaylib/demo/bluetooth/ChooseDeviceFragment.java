package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.cloudbus.foggatewaylib.bluetooth.BluetoothLeHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPairDevice} interface
 * to handle interaction events.
 * Use the {@link ChooseDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseDeviceFragment extends Fragment
        implements BluetoothDeviceListAdapter.OnItemClickListener {
    public static final String TAG = "ChooseDeviceFragment";

    private BluetoothLeHandler bluetoothLeHandler;
    private BluetoothDeviceListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;


    public ChooseDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_choose_device, container, false);
        recyclerView = rootView.findViewById(R.id.list);

        recyclerView.setHasFixedSize(true);

        adapter = new BluetoothDeviceListAdapter(this);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.bluetoothLeHandler = BluetoothLeHandler.getInstance();
        if (getContext() != null){

            for (BluetoothDevice device: bluetoothLeHandler.getConnectedDevices()){
                adapter.updateItem(device,
                        BluetoothDeviceListAdapter.Device.STATUS_CONNECTED,
                        null);
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
