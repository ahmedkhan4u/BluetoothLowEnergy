package com.example.androidble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;

class MyBleManager extends ObservableBleManager implements DataReceivedCallback, DataSentCallback {
    private static final String TAG = "MyBleManager";

    private BluetoothGattCharacteristic fluxCapacitorControlPoint;

    public MyBleManager(@NonNull final Context context) {
        super(context);
    }

    @Override
    public int getMinLogPriority() {
        // Use to return minimal desired logging priority.
        return Log.VERBOSE;
    }

    @Override
    public void log(int priority, @NonNull String message) {
        // Log from here.
        Log.println(priority, TAG, message);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new MyGattCallbackImpl();
    }

    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {

    }

    @Override
    public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data) {

    }

    private class MyGattCallbackImpl extends BleManagerGattCallback {
        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {



            // Here get instances of your characteristics.
            // Return false if a required service has not been discovered.
            BluetoothGattService fluxCapacitorService = gatt.getService(UUID.fromString("fe9dd9f3-99cd-4e0f-a76b-bfeab1c25624"));
            if (fluxCapacitorService != null) {
                fluxCapacitorControlPoint = fluxCapacitorService.getCharacteristic(UUID.fromString("ef44bc45-d877-40eb-92b7-0a4a3eae7f8a"));
            }
            return fluxCapacitorControlPoint != null;
        }

        @Override
        protected void initialize() {
            // Initialize your device.
            // This means e.g. enabling notifications, setting notification callbacks,
            // sometimes writing something to some Control Point.
            // Kotlin projects should not use suspend methods here, which require a scope.
            requestMtu(517)
                    .enqueue();
        }

        @Override
        protected void onServicesInvalidated() {
            // This method is called when the services get invalidated, i.e. when the device
            // disconnects.
            // References to characteristics should be nullified here.
            fluxCapacitorControlPoint = null;
        }
    }


    // Here you may add some high level methods for your device:
    public void enableFluxCapacitor() {
        // Do the magic.
        writeCharacteristic(fluxCapacitorControlPoint, Data.from("on"), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
    }
}