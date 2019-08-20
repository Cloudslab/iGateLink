package org.cloudbus.foggatewaylib.bluetooth.ui;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.cloudbus.foggatewaylib.bluetooth.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//TODO documentation
public class BluetoothDeviceListAdapter
         extends RecyclerView.Adapter<BluetoothDeviceListAdapter.MyViewHolder> {

    private List<Device> mDataset = new ArrayList<>();
    private OnItemClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
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

    public static class Device implements Comparable<Device>{
        public static final int STATUS_CONNECTING = 0;
        public static final int STATUS_CONNECTED = 1;
        public static final int STATUS_DISCONNECTING = 2;
        public static final int STATUS_ERROR = 3;
        public static final int STATUS_DISCONNECTED = 4;

        public BluetoothDevice device;
        public int status;
        public String error;

        public Device(BluetoothDevice device, int status, String error) {
            this.device = device;
            this.status = status;
            this.error = error;
        }

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

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj instanceof Device){
                return ((Device)obj).device.equals(device);
            } else
                return false;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(BluetoothDeviceListAdapter adapter, Device item);
    }

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

    // Provide a suitable constructor (depends on the kind of dataset)
    public BluetoothDeviceListAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BluetoothDeviceListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bluetooth_item, parent, false);
        MyViewHolder vh = new MyViewHolder(linearLayout);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
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

    private Device getItem(int position){
        return mDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void updateItem(BluetoothDevice device, int status, String error){
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

    private int insertInCorrectOrder(Device item){
        int position = Collections.binarySearch(mDataset, item);
        if (position < 0){
            position = -position - 1;
            mDataset.add(position, item);
            return position;
        }
        return -1;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void clear(){
        mDataset.clear();
        notifyDataSetChanged();
    }

}
