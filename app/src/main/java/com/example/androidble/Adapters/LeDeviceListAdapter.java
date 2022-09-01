package com.example.androidble.Adapters;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidble.Interfaces.DeviceSelectedCallback;
import com.example.androidble.MainActivity;
import com.example.androidble.databinding.DeviceItemListBinding;

import java.util.List;


public class LeDeviceListAdapter extends RecyclerView.Adapter<LeDeviceListAdapter.ViewHolder> {

    private Context context;
    private List<BluetoothDevice> deviceList;
    private DeviceSelectedCallback listener;

    public LeDeviceListAdapter(Context context, List<BluetoothDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        this.listener = (DeviceSelectedCallback) context;

    }


    @NonNull
    @Override
    public LeDeviceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DeviceItemListBinding binding = DeviceItemListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull LeDeviceListAdapter.ViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);

        holder.title.setText(device.getName() == null ? "No Name" : device.getName());
        holder.address.setText(device.getAddress());

        holder.itemView.setOnClickListener(view -> {
            listener.ICallback(position);
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, address;

        public ViewHolder(@NonNull final DeviceItemListBinding binding) {
            super(binding.getRoot());

            title = binding.title;
            address = binding.address;
        }
    }
}
