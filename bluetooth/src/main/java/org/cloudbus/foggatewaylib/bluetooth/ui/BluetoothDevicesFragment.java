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
 * A simple {@link Fragment} that holds a list of bluetooth devices.
 *
 * NB: an empty constructor is required when extending this class.
 *
 * @see BluetoothDeviceListAdapter
 *
 * @author Riccardo Mancini
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

    /**
     * Constructs a new fragment with the given layout ID and list ID.
     * NB: an empty constructor is required when extending this class.
     *
     * @param layout the resource id for the layout to be inflated.
     * @param listID the rsource id for the list within the specified layout.
     */
    public BluetoothDevicesFragment(@LayoutRes int layout, @IdRes int listID) {
        this.layout = layout;
        this.listID = listID;
    }

    /**
     * Inflates the view and initializes the {@link #recyclerView} and {@link #adapter}.
     * Override this method to add additional views.
     *
     * @see Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
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

    /**
     * Returns the adapter used for the device list.
     */
    protected BluetoothDeviceListAdapter getAdapter() {
        return adapter;
    }

    /**
     * Updates or adds a new device.
     *
     * @see BluetoothDeviceListAdapter#updateItem(BluetoothDevice, int, String)
     */
    protected void updateItem(BluetoothDevice device, int status, String error) {
        adapter.updateItem(device, status, error);
    }

    /**
     * Removes all devices.
     *
     * @see BluetoothDeviceListAdapter#clear()
     */
    protected void clearAdapter() {
        adapter.clear();
    }
}