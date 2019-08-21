package org.cloudbus.foggatewaylib.bluetooth.ui;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.cloudbus.foggatewaylib.bluetooth.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter for a simple {@link RecyclerView} list containing bluetooth devices.
 * A {@link BluetoothDevice} can be added or updated using
 * {@link #updateItem(BluetoothDevice, int, String)} and passing the device itself, its status
 * and an optional error message.
 *
 * @author Riccardo Mancini
 */
public class BluetoothDeviceListAdapter
         extends RecyclerView.Adapter<BluetoothDeviceListAdapter.MyViewHolder> {

    /**
     * Backing list for the items.
     */
    private List<Device> mDataset = new ArrayList<>();

    /**
     * Custom listener for clicks on items.
     */
    private OnItemClickListener listener;

    /**
     * Class that contains all the {@link View}s in the list item root view needed to be
     * accessed in {@link #onBindViewHolder(MyViewHolder, int)}
     *
     * @see #onCreateViewHolder(ViewGroup, int)
     * @see #onBindViewHolder(MyViewHolder, int)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linearLayout;
        public TextView deviceName;
        public TextView deviceAddress;
        public ImageView image;
        public ProgressBar progressBar;

        public MyViewHolder(LinearLayout linearLayout) {
            super(linearLayout);
            this.linearLayout = linearLayout;
            this.deviceName = linearLayout.findViewById(R.id.device_name);
            this.deviceAddress = linearLayout.findViewById(R.id.device_address);
            this.image = linearLayout.findViewById(R.id.status);
            this.progressBar = linearLayout.findViewById(R.id.progress_circular);
        }
    }

    /**
     * Class representing the device.
     */
    public static class Device implements Comparable<Device>{
        public static final int STATUS_CONNECTING = 0;
        public static final int STATUS_CONNECTED = 1;
        public static final int STATUS_DISCONNECTING = 2;
        public static final int STATUS_ERROR = 3;
        public static final int STATUS_DISCONNECTED = 4;

        public BluetoothDevice device;
        public int status;
        public String error;

        /**
         * Constructs a new {@link Device}.
         *
         * @param device the device itself.
         * @param status the device status. Must be one of: <ul>
         *                  <li>{@link #STATUS_CONNECTING}</li>
         *                  <li>{@link #STATUS_CONNECTED}</li>
         *                  <li>{@link #STATUS_DISCONNECTING}</li>
         *                  <li>{@link #STATUS_ERROR}</li>
         *                  <li>{@link #STATUS_DISCONNECTED}</li>
         *                </ul>
         * @param error an optional error message.
         * @see #STATUS_CONNECTING
         * @see #STATUS_CONNECTED
         * @see #STATUS_DISCONNECTING
         * @see #STATUS_ERROR
         * @see #STATUS_DISCONNECTED
         */
        public Device(@NonNull BluetoothDevice device, int status, @Nullable String error) {
            this.device = device;
            this.status = status;
            this.error = error;
        }

        /**
         * Compares devices such that resulting ordering is by: first, status code (thus order is
         * connecting, connected, disconnecting, error, disconnected); second, devices with a
         * name are shown before devices without one; third, alphabetically by name (if present)
         * or by address.
         */
        @Override
        public int compareTo(Device o) {
            if (status != o.status)
                return status - o.status;
            if (device.getName() != null && o.device.getName() != null){
                return device.getName().compareTo(o.device.getName());
            }
            if (device.getName() != null && o.device.getName() == null){
                return -1;
            }
            if (device.getName() == null && o.device.getName() != null){
                return 1;
            }
            // both names are null
            return device.getAddress().compareTo(o.device.getAddress());
        }

        /**
         * Returns {@code true} if {@link BluetoothDevice} is the same, {@code false} otherwise.
         */
        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Device){
                return ((Device)obj).device.equals(device);
            } else
                return false;
        }
    }

    /**
     * Interface to handle clicks on the list items.
     */
    public interface OnItemClickListener{
        void onItemClick(BluetoothDeviceListAdapter adapter, Device item);
    }

    /**
     * Inner click listener on list item root views.
     */
    private class OnClickListener implements View.OnClickListener{
        private BluetoothDeviceListAdapter adapter;
        private Device item;
        private OnItemClickListener listener;

        public OnClickListener(OnItemClickListener listener,
                               BluetoothDeviceListAdapter adapter, Device item){
            this.adapter = adapter;
            this.item = item;
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            if (listener != null)
                listener.onItemClick(adapter, item);
        }
    }

    /**
     * Constructs an adapter with the given {@code listener}.
     */
    public BluetoothDeviceListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Constructs an adapter with no listener.
     *
     * @see #setOnItemClickListener(OnItemClickListener)
     */
    public BluetoothDeviceListAdapter() {
        this.listener = listener;
    }

    /**
     * Inflates the view and returns a {@link MyViewHolder} instance.
     * Do not call directly.
     *
     * @see RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)
     */
    @Override
    public BluetoothDeviceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_item, parent, false);
        MyViewHolder vh = new MyViewHolder(linearLayout);
        return vh;
    }

    /**
     * Replace the contents of a view with the item at the given {@code position}.
     * Do not call directly (invoked by the layout manager).
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Device item = getItem(position);

        if (item.device.getName() != null) {
            holder.deviceName.setText(item.device.getName());
        } else{
            holder.deviceName.setText(item.device.getAddress());
        }

        if (item.status != Device.STATUS_ERROR){
            if (item.device.getName() != null) {
                holder.deviceAddress.setText(item.device.getAddress());
            } else{
                holder.deviceAddress.setText(null);
            }
        } else {
            holder.deviceAddress.setText(item.error);
        }

        switch (item.status){
            case Device.STATUS_CONNECTED:
            case Device.STATUS_DISCONNECTING:
                holder.image.setImageResource(R.drawable.ic_bluetooth_connected_blue_24dp);
                break;
            case Device.STATUS_CONNECTING:
            case Device.STATUS_DISCONNECTED:
                holder.image.setImageResource(R.drawable.ic_bluetooth_gray_24dp);
                break;
            case Device.STATUS_ERROR:
                holder.image.setImageResource(R.drawable.ic_bluetooth_disabled_red_24dp);
                break;
        }

        if (item.status == Device.STATUS_CONNECTING || item.status == Device.STATUS_DISCONNECTING){
            holder.progressBar.setVisibility(View.VISIBLE);
        } else{
            holder.progressBar.setVisibility(View.INVISIBLE);
        }

        holder.linearLayout.setOnClickListener(
                new OnClickListener(listener, this, item));
    }

    /**
     * Returns item at given position.
     */
    private Device getItem(int position){
        return mDataset.get(position);
    }

    /**
     * Returns the number of items (invoked by the layout manager).
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * Updates or adds a new item to the list.
     * Changes to the backing list are automatically notified to the adapter (no need to call
     * {@link #notifyDataSetChanged()}.
     * If an item becomes {@link Device#STATUS_DISCONNECTED} from {@link Device#STATUS_ERROR},
     * its change is ignored in order to keep showing the error to the user.
     *
     * @param device the device to be added or updated.
     * @param status its new status (see {@link Device}).
     * @param error optional error message.
     */
    public void updateItem(@NonNull BluetoothDevice device, int status, @Nullable String error){
        Device item = new Device(device, status, error);
        int oldIndex = mDataset.indexOf(item);
        if (oldIndex >= 0){
            Device oldItem = getItem(oldIndex);
            if (oldItem.status == item.status)
                return;
            if (oldItem.status == Device.STATUS_ERROR && item.status == Device.STATUS_DISCONNECTED)
                return;

            mDataset.remove(oldIndex);
        }
        int newIndex = insertInCorrectOrder(item);

        if (oldIndex >= 0){
            if (oldIndex != newIndex)
                notifyItemMoved(oldIndex, newIndex);

            notifyItemChanged(newIndex);
        } else{
            notifyItemInserted(newIndex);
        }
    }

    /**
     * Returns the position at which the item should be inserted in the {@link #mDataset} to keep
     * the ascending order.
     *
     * @param item the item to be added in the list
     * @return the position or {@code -1} if the item is already inside the list.
     */
    private int insertInCorrectOrder(Device item){
        int position = Collections.binarySearch(mDataset, item);
        if (position < 0){
            position = -position - 1;
            mDataset.add(position, item);
            return position;
        }
        return -1;
    }

    /**
     * Sets a new listener for clicks on items.
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Removes all
     */
    public void clear(){
        mDataset.clear();
        notifyDataSetChanged();
    }

}
