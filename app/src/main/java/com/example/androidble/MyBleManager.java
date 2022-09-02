package com.example.androidble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.androidble.Utils.Utils;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;

public class MyBleManager extends ObservableBleManager implements DataReceivedCallback, DataSentCallback {
    private static final String TAG = "MyBleManager";

    private static final int MTU_SIZE_DEFAULT = 23;
    private static final int MTU_SIZE_MAX = 517;

    /**
     * Mesh provisioning service UUID
     */
    public final static UUID MESH_PROVISIONING_UUID = UUID.fromString("00001827-0000-1000-8000-00805F9B34FB");
    /**
     * Mesh provisioning data in characteristic UUID
     */
    private final static UUID MESH_PROVISIONING_DATA_IN = UUID.fromString("00002ADB-0000-1000-8000-00805F9B34FB");
    /**
     * Mesh provisioning data out characteristic UUID
     */
    private final static UUID MESH_PROVISIONING_DATA_OUT = UUID.fromString("00002ADC-0000-1000-8000-00805F9B34FB");

    /**
     * Mesh provisioning service UUID
     */
    public final static UUID MESH_PROXY_UUID = UUID.fromString("00001828-0000-1000-8000-00805F9B34FB");

    /**
     * Mesh provisioning data in characteristic UUID
     */
    private final static UUID MESH_PROXY_DATA_IN = UUID.fromString("00002ADD-0000-1000-8000-00805F9B34FB");

    /**
     * Mesh provisioning data out characteristic UUID
     */
    private final static UUID MESH_PROXY_DATA_OUT = UUID.fromString("00002ADE-0000-1000-8000-00805F9B34FB");

    private BluetoothGattCharacteristic mMeshProvisioningDataInCharacteristic;
    private BluetoothGattCharacteristic mMeshProvisioningDataOutCharacteristic;
    private BluetoothGattCharacteristic mMeshProxyDataInCharacteristic;
    private BluetoothGattCharacteristic mMeshProxyDataOutCharacteristic;

    private boolean isProvisioningComplete;
    private boolean mIsDeviceReady;
    private boolean mNodeReset;


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


            final BluetoothGattService meshProxyService = gatt.getService(MESH_PROXY_UUID);
            if (meshProxyService != null) {
                isProvisioningComplete = true;
                mMeshProxyDataInCharacteristic = meshProxyService.getCharacteristic(MESH_PROXY_DATA_IN);
                mMeshProxyDataOutCharacteristic = meshProxyService.getCharacteristic(MESH_PROXY_DATA_OUT);

                return mMeshProxyDataInCharacteristic != null &&
                        mMeshProxyDataOutCharacteristic != null &&
                        hasNotifyProperty(mMeshProxyDataOutCharacteristic) &&
                        hasWriteNoResponseProperty(mMeshProxyDataInCharacteristic);
            }
            final BluetoothGattService meshProvisioningService = gatt.getService(MESH_PROVISIONING_UUID);
            if (meshProvisioningService != null) {
                isProvisioningComplete = false;
                mMeshProvisioningDataInCharacteristic = meshProvisioningService.getCharacteristic(MESH_PROVISIONING_DATA_IN);
                mMeshProvisioningDataOutCharacteristic = meshProvisioningService.getCharacteristic(MESH_PROVISIONING_DATA_OUT);

                return mMeshProvisioningDataInCharacteristic != null &&
                        mMeshProvisioningDataOutCharacteristic != null &&
                        hasNotifyProperty(mMeshProvisioningDataOutCharacteristic) &&
                        hasWriteNoResponseProperty(mMeshProvisioningDataInCharacteristic);
            }
            return false;



            // Here get instances of your characteristics.
            // Return false if a required service has not been discovered.
//            BluetoothGattService fluxCapacitorService = gatt.getService(UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E"));
//            if (fluxCapacitorService != null) {
//                fluxCapacitorControlPoint = fluxCapacitorService.getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"));
//            }
//            return fluxCapacitorControlPoint != null;
        }

        @Override
        protected void initialize() {
            requestMtu(MTU_SIZE_MAX).enqueue();

            // This callback will be called each time a notification is received.
            DataReceivedCallback callback = new DataReceivedCallback() {
                @Override
                public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                    Utils.log(TAG, data.toString());
                }
            };

            // Set the notification callback and enable notification on Data In characteristic.
            final BluetoothGattCharacteristic characteristic = isProvisioningComplete ?
                    mMeshProxyDataOutCharacteristic : mMeshProvisioningDataOutCharacteristic;
            setNotificationCallback(characteristic).with(callback);
            enableNotifications(characteristic).enqueue();



        }

        @Override
        protected void onServicesInvalidated() {
            overrideMtu(MTU_SIZE_DEFAULT);
            mIsDeviceReady = false;
            isProvisioningComplete = false;
            mMeshProvisioningDataInCharacteristic = null;
            mMeshProvisioningDataOutCharacteristic = null;
            mMeshProxyDataInCharacteristic = null;
            mMeshProxyDataOutCharacteristic = null;

        }

        @Override
        protected void onDeviceDisconnected() {
            super.onDeviceDisconnected();
        }
    }

    private boolean hasWriteNoResponseProperty(@NonNull final BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0;
    }

    private boolean hasNotifyProperty(@NonNull final BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }


    // Here you may add some high level methods for your device:
    public void enableFluxCapacitor() {
        // Do the magic.
        writeCharacteristic(fluxCapacitorControlPoint, Data.from("on"), BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                .enqueue();
    }

}