package com.example.androidble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.androidble.Adapters.LeDeviceListAdapter;
import com.example.androidble.Interfaces.DeviceSelectedCallback;
import com.example.androidble.ViewModels.MainActivityViewModel;
import com.example.androidble.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceSelectedCallback {

    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    private List<ScanFilter> filterList;
    private ScanSettings scanSettings;
    private BluetoothManager bluetoothManager;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private boolean scanning;
    private Handler handler = new Handler();
    private static final long SCAN_PERIOD = 3000;
    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;
    private MyBleManager bleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        filterList = new ArrayList<>();
        ScanFilter.Builder scanFilterBuilder = new ScanFilter.Builder();
        filterList.add(scanFilterBuilder.build());
        scanSettings = new ScanSettings.Builder().build();
        bleManager = new MyBleManager(this);


        clickListener();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        observers();

    }

    private void observers() {

        viewModel.getDeviceList().observe(MainActivity.this, devices -> {

            LeDeviceListAdapter adapter = new LeDeviceListAdapter(MainActivity.this, viewModel.getDeviceList().getValue());
            binding.recyclerView.setAdapter(adapter);

        });

    }

    @SuppressLint("MissingPermission")
    private void clickListener() {
        binding.btnScan.setOnClickListener(view -> {
            if (!deviceList.isEmpty()) deviceList.clear();
            binding.progressBar.setVisibility(View.VISIBLE);
            scanLeDevice();
        });
        binding.btnStop.setOnClickListener(view -> {
            bluetoothLeScanner.stopScan(leScanCallback);
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    @SuppressLint("MissingPermission")
    private void scanLeDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!scanning) {

                    // Stops scanning after a predefined scan period.
                    handler.postDelayed(() -> {
                        scanning = false;

                        bluetoothLeScanner.stopScan(leScanCallback);
                        binding.progressBar.setVisibility(View.GONE);
                    }, SCAN_PERIOD);

                    scanning = true;
                    bluetoothLeScanner.startScan(filterList, scanSettings ,leScanCallback);
                } else {
                    scanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }
        }).start();
    }


    // Device scan callback.
    private ScanCallback leScanCallback =
            new ScanCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);

                    Log.d("ble => ", result.getDevice().getName() + " Address : " + result.getDevice().getAddress());
                    addDevice(result.getDevice());

                }
            };

    private void addDevice(BluetoothDevice device) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().trim().equalsIgnoreCase(device.getAddress().trim())) {
                deviceFound = true;
            }

        }
        if (!deviceFound) {
            deviceList.add(device);
            viewModel.getDeviceList().setValue(deviceList);
        }
    }



    @Override
    public void ICallback(int pos) {
        Toast.makeText(MainActivity.this, pos + "", Toast.LENGTH_SHORT).show();
        BluetoothDevice bleDevice = viewModel.getDeviceList().getValue().get(pos);
        bleManager.connect(bleDevice)
                .retry(5)
                .timeout(15_000)
                .useAutoConnect(true)
                .before(device -> { Log.d("MyBleManager", "Before" + device.toString()); })
                .done(device -> { Log.d("MyBleManager",  "Done" + device.toString()); })
                .then(device -> { Log.d("MyBleManager",  "Then" + device.toString()); })
                .enqueue();
    }
}