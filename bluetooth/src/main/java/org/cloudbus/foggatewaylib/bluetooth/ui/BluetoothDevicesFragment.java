package org.cloudbus.foggatewaylib.bluetooth.ui;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnPairDevice} interface
 * to handle interaction events.
 * Use the {@link ChooseDeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public abstract class BluetoothDevicesFragment extends Fragment
        implements BluetoothDeviceListAdapter.OnItemClickListener {

    private BluetoothDeviceListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;

    @LayoutRes
    private int layout;

    @IdRes
    private int listID;

    public BluetoothDevicesFragment(@LayoutRes int layout, @IdRes int listID) {
        this.layout = layout;
        this.listID = listID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @CallSuper
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(layout, container, false);
        recyclerView = rootView.findViewById(listID);
        recyclerView.setHasFixedSize(true);

        adapter = new BluetoothDeviceListAdapter(this);
        recyclerView.setAdapter(adapter);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    protected BluetoothDeviceListAdapter getAdapter() {
        return adapter;
    }

    protected void updateItem(BluetoothDevice device, int status, String error) {
        adapter.updateItem(device, status, error);
    }

    protected void clearAdapter() {
        adapter.clear();
    }
}