package com.example.androidble.ViewModels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.androidble.MainActivity;
import com.example.androidble.MyBleManager;
import com.example.androidble.Utils.Utils;

import java.util.List;

import no.nordicsemi.android.mesh.MeshManagerApi;
import no.nordicsemi.android.mesh.MeshManagerCallbacks;
import no.nordicsemi.android.mesh.MeshNetwork;
import no.nordicsemi.android.mesh.MeshProvisioningStatusCallbacks;
import no.nordicsemi.android.mesh.MeshStatusCallbacks;
import no.nordicsemi.android.mesh.provisionerstates.ProvisioningState;
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.mesh.transport.ControlMessage;
import no.nordicsemi.android.mesh.transport.MeshMessage;
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode;

public class MainActivityViewModel extends AndroidViewModel implements MeshManagerCallbacks, MeshProvisioningStatusCallbacks, MeshStatusCallbacks {

    private MutableLiveData<List<BluetoothDevice>> deviceList;
    public MeshManagerApi mMeshManagerApi;
    public MyBleManager bleManager;
    final String TAG = "MESH_PROVISIONING_DATA";



    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mMeshManagerApi = new MeshManagerApi(application);
        mMeshManagerApi.setMeshManagerCallbacks(this);
        mMeshManagerApi.setProvisioningStatusCallbacks(this);
        mMeshManagerApi.setMeshStatusCallbacks(this);
        mMeshManagerApi.loadMeshNetwork();
        bleManager = new MyBleManager(application);
    }

    public MutableLiveData<List<BluetoothDevice>> getDeviceList() {
        if (deviceList == null) {
            deviceList = new MutableLiveData<>();
        }
        return deviceList;
    }

    @Override
    public void onNetworkLoaded(MeshNetwork meshNetwork) {

        Utils.log(TAG, "Network Name = " + meshNetwork.getMeshName());
        Utils.log(TAG, "Network Name = " + meshNetwork.getMeshUUID());
        for (int i=0; i<meshNetwork.getAppKeys().size(); i++){
            Utils.log(TAG, "App Key : "+(i+1)+" = " + meshNetwork.getAppKeys().get(i).getName());
            Utils.log(TAG, "App Key : "+(i+1)+" = " + meshNetwork.getAppKeys().get(i).getKey());
        }

    }

    @Override
    public void onNetworkUpdated(MeshNetwork meshNetwork) {

    }

    @Override
    public void onNetworkLoadFailed(String error) {

    }

    @Override
    public void onNetworkImported(MeshNetwork meshNetwork) {

    }

    @Override
    public void onNetworkImportFailed(String error) {

    }

    @Override
    public void sendProvisioningPdu(UnprovisionedMeshNode meshNode, byte[] pdu) {

    }

    @Override
    public void onMeshPduCreated(byte[] pdu) {

    }

    @Override
    public int getMtu() {
        return 0;
    }

    @Override
    public void onProvisioningStateChanged(UnprovisionedMeshNode meshNode, ProvisioningState.States state, @Nullable byte[] data) {

    }

    @Override
    public void onProvisioningFailed(UnprovisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {

    }

    @Override
    public void onProvisioningCompleted(ProvisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {

    }

    @Override
    public void onTransactionFailed(int dst, boolean hasIncompleteTimerExpired) {

    }

    @Override
    public void onUnknownPduReceived(int src, byte[] accessPayload) {

    }

    @Override
    public void onBlockAcknowledgementProcessed(int dst, @NonNull ControlMessage message) {

    }

    @Override
    public void onBlockAcknowledgementReceived(int src, @NonNull ControlMessage message) {

    }

    @Override
    public void onMeshMessageProcessed(int dst, @NonNull MeshMessage meshMessage) {

    }

    @Override
    public void onMeshMessageReceived(int src, @NonNull MeshMessage meshMessage) {

    }

    @Override
    public void onMessageDecryptionFailed(String meshLayer, String errorMessage) {

    }
}
