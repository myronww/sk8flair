package com.skateflair.flair.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

import com.skateflair.flair.FlairConstants;
import com.skateflair.flair.datum.DatumFlairDevice;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServiceFlairController extends Service {

    public static final String TAG = "ServiceFlairController";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    protected ReentrantReadWriteLock m_TableLock = new ReentrantReadWriteLock();
    protected Hashtable<String, DatumFlairDevice> m_DeviceTable = new Hashtable<>();

    private FlairServiceBoardcastReceiver m_IntentReceiver;

    private String m_DeviceMode;

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean bluetooth_initialize() {

        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        bluetooth_initialize();

        m_IntentReceiver = new FlairServiceBoardcastReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(FlairConstants.ACTIONS.SERVICE.CONNECT_DEVICES);
        filter.addAction(FlairConstants.ACTIONS.SERVICE.DISCONNECT_DEVICES);
        filter.addAction(FlairConstants.ACTIONS.SERVICE.FLAIR_PROFILE_CHANGE);
        filter.addAction(FlairConstants.ACTIONS.SERVICE.FLAIR_PROFILE_UPDATE);
        filter.addAction(FlairConstants.ACTIONS.SERVICE.FLAIR_SYNC_TIME);

        registerReceiver(m_IntentReceiver, filter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        //TODO do something useful
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void broadcast_device_connection_error(String dev_addr)
    {
        //Send a broadcast message that a flair device connection was unsuccessful
        Intent status_intent = new Intent();

        status_intent.setAction(FlairConstants.ACTIONS.BILLBOARD.DEVICE_CONNECTION_FAILURE);
        status_intent.addCategory(Intent.CATEGORY_DEFAULT);
        status_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_DEVICE_ID, dev_addr);

        sendBroadcast(status_intent);

        Log.d(TAG, "Error connecting to bluetooth device - '" + dev_addr + "'...");
    }

    private void broadcast_device_connection_success(String dev_addr)
    {
        //Send a broadcast message that a flair device connection was successful
        Intent status_intent = new Intent();
        status_intent.setAction(FlairConstants.ACTIONS.BILLBOARD.DEVICE_CONNECTION_SUCCESS);
        status_intent.addCategory(Intent.CATEGORY_DEFAULT);
        status_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_DEVICE_ID, dev_addr);

        sendBroadcast(status_intent);

        Log.d(TAG, "Successfully connected to bluetooth device - '" + dev_addr + "'...");
    }

    protected void active_device_change_profile(String fd_addr, String mode)
    {
        m_DeviceMode = mode;

        BluetoothDevice nxtdev = mBluetoothAdapter.getRemoteDevice(fd_addr);
        nxtdev.connectGatt(this, true, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                String devaddr = gatt.getDevice().getAddress();

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i(TAG, "Mode change successfully connected to device=" + devaddr);
                    gatt.discoverServices();
                }
                else if (newState == BluetoothProfile.STATE_CONNECTING) {
                    Log.i(TAG, "Mode change connecting to device=" + devaddr);
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.i(TAG, "Mode change disconnecting from device=" + devaddr);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                BluetoothGattService gattsvc = gatt.getService(FlairConstants.UUIDS.FLAIR_CONTROL_SERVICE_UUID);
                if (gattsvc != null) {
                    Log.i(TAG, "Changing flair mode=" + ServiceFlairController.this.m_DeviceMode);

                    BluetoothGattCharacteristic btchar = gattsvc.getCharacteristic(FlairConstants.UUIDS.FLAIR_FLAIR_MODE_IO_UUID);
                    btchar.setValue(ServiceFlairController.this.m_DeviceMode);
                    gatt.beginReliableWrite();
                    gatt.writeCharacteristic(btchar);
                    gatt.executeReliableWrite();

                    gatt.disconnect();
                }
            }
        });
    }

    protected void active_group_change_profile(String profile_name)
    {
        ArrayList<String> address_list = new ArrayList<String>();

        Lock wlock = m_TableLock.writeLock();
        wlock.lock();
        try {
            for (DatumFlairDevice fdobj: m_DeviceTable.values()) {
                address_list.add(fdobj.getAddress());
            }
        }
        finally {
            wlock.unlock();
        }

        for (String dev_addr: address_list) {
            active_device_change_profile(dev_addr, profile_name);
        }

        return;
    }

    protected void active_group_connect_devices(Parcelable[] flair_devices_list)
    {
        Lock wlock = m_TableLock.writeLock();
        wlock.lock();
        try {
            m_DeviceTable.clear();

            for (Parcelable fdobj : flair_devices_list) {
                DatumFlairDevice fd_datum = (DatumFlairDevice) fdobj;
                String fd_addr = fd_datum.getAddress();
                m_DeviceTable.put(fd_addr, fd_datum);

                BluetoothDevice nxtdev = mBluetoothAdapter.getRemoteDevice(fd_addr);
                nxtdev.connectGatt(this, true, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        String devaddr = gatt.getDevice().getAddress();

                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            broadcast_device_connection_success(devaddr);

                            Log.i(TAG, "Successfully connected to device=" + devaddr);
                        }
                        else if (newState == BluetoothProfile.STATE_CONNECTING) {
                            Log.i(TAG, "Connecting to device=" + devaddr);
                        }
                        else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            Log.i(TAG, "Disconnecting from device=" + devaddr);
                        }
                    }


                });
            }
        }
        finally {
            wlock.unlock();
        }
    }

    protected void active_group_disconnect_devices()
    {
        Lock wlock = m_TableLock.writeLock();
        wlock.lock();
        try {
            for (DatumFlairDevice fdobj: m_DeviceTable.values()) {
                //TODO: Notify the Flair Device Billboard about the Disconnect Status
            }
            m_DeviceTable.clear();
        }
        finally {
            wlock.unlock();
        }
    }

    private class FlairServiceBoardcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "Processing command '" + action + "'...");

            if (action.equals(FlairConstants.ACTIONS.SERVICE.CONNECT_DEVICES)) {
                Parcelable[] flair_devices_list = intent.getParcelableArrayExtra(FlairConstants.PAYLOADS.FLAIR_GROUP_DEVICES);
                active_group_connect_devices(flair_devices_list);

            } else if (action.equals(FlairConstants.ACTIONS.SERVICE.DISCONNECT_DEVICES)) {
                active_group_disconnect_devices();

            } else if (action.equals(FlairConstants.ACTIONS.SERVICE.FLAIR_PROFILE_CHANGE)) {
                String profile_name = intent.getStringExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_NAME);
                ServiceFlairController.this.active_group_change_profile(profile_name);

            } else if (action.equals(FlairConstants.ACTIONS.SERVICE.FLAIR_PROFILE_UPDATE)) {
                String profile_name = intent.getStringExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_NAME);
                String profile_content = intent.getStringExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_CONTENT);
                boolean switch_profile = intent.getBooleanExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_SWITCH, false);

            } else if (action.equals(FlairConstants.ACTIONS.SERVICE.FLAIR_SYNC_TIME)) {


            }
        }
    };

}