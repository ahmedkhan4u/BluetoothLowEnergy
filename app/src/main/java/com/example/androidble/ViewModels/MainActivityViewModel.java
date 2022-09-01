package com.example.androidble.ViewModels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<BluetoothDevice>> deviceList;

    public MutableLiveData<List<BluetoothDevice>> getDeviceList() {
        if (deviceList == null) {
            deviceList = new MutableLiveData<>();
        }
        return deviceList;
    }
}
