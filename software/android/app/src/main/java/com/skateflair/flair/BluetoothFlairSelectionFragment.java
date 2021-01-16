package com.skateflair.flair;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.skateflair.flair.datum.DatumFlairDevice;
import com.skateflair.flair.service.ServiceFlairController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Created by myron on 2/13/16.
 */
public class BluetoothFlairSelectionFragment extends Fragment {

    public static final String TAG = "BtFlairSelFragment";

    private ListView mListView;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanFilter mFlairDeviceFilter;

    protected ReentrantReadWriteLock m_FoundBtTableLock = new ReentrantReadWriteLock();
    private Hashtable<String, DatumFlairDevice> m_FlairDevices = new Hashtable<String, DatumFlairDevice>();

    public ArrayList<DatumFlairDevice> getSelected() {
        ArrayList<DatumFlairDevice> selected_devices = new ArrayList<DatumFlairDevice>();

        for (DatumFlairDevice fdevice : m_FlairDevices.values()) {
            if (fdevice.getSelected()) {
                selected_devices.add(fdevice);
            }
        }

        return selected_devices;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bluetooth_flair_selection, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView)view.findViewById(R.id.lstBluetoothDeviceSelection);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            error_bluetooth_adapter_error();
        }

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        ScanSettings.Builder builderScanSettings = new ScanSettings.Builder();
        builderScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        builderScanSettings.setReportDelay(0);

        ScanFilter.Builder filterBuilder = new ScanFilter.Builder();
        //filterBuilder.setServiceUuid(new ParcelUuid(FlairConstants.FLAIR_DEVICE_SERVICE_ID));
        mFlairDeviceFilter = filterBuilder.build();

        Vector<ScanFilter> filters = new Vector<ScanFilter>();
        filters.add(mFlairDeviceFilter);

        mBluetoothLeScanner.startScan(filters, builderScanSettings.build(), scanCallback);

        update_device_listview(mListView);
    }

    private AdapterView.OnItemClickListener handler_flairdevice_list_item_clicked = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int position, long id)
        {
            Activity activity = getActivity();

            v.setSelected(true);

            ArrayAdapterFlairDevice.ItemViewModel item_model = (ArrayAdapterFlairDevice.ItemViewModel)v.getTag();

            String device_address = item_model.tvAddress.getText().toString();
            Boolean is_checked = item_model.chkSelected.isChecked();

            DatumFlairDevice sel_device = m_FlairDevices.get(device_address);
            sel_device.setSelected(!is_checked);

            item_model.chkSelected.setChecked(!is_checked);
        }

    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device = result.getDevice();
            addBluetoothDevice(device);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);

            for(ScanResult result : results){
                BluetoothDevice device = result.getDevice();
                addBluetoothDevice(device);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);

            Log.e(TAG, String.format("Bluetooth scan failure. errorCode=%d", errorCode));
        }

        private void addBluetoothDevice(BluetoothDevice device){

            String devaddr = device.getAddress();

            boolean hasdevice = false;
            Lock rlock = m_FoundBtTableLock.readLock();
            rlock.lock();
            try {
                if(m_FlairDevices.contains(device)){
                    hasdevice = true;
                }
            }
            finally {
                rlock.unlock();
            }

            if(!hasdevice)
            {
                BluetoothClass btclass = device.getBluetoothClass();
                int devClass = btclass.getDeviceClass();
                int majorDevClass = btclass.getMajorDeviceClass();
                if (majorDevClass == BluetoothClass.Device.Major.UNCATEGORIZED) {
                    String addr = device.getAddress();
                    String name = device.getName();

                    Context context = BluetoothFlairSelectionFragment.this.getContext();
                    device.connectGatt(context, true, new BluetoothGattCallback() {

                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                BluetoothDevice device = gatt.getDevice();
                                String name = device.getName();
                                String addr = device.getAddress();
                                Log.i(TAG, String.format("Connected to GATT client. name=%s, addr=%s", name, addr));
                                gatt.discoverServices();
                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                Log.i(TAG, "Disconnected from GATT client");
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            if (status != BluetoothGatt.GATT_SUCCESS) {
                                // Handle the error
                                return;
                            }

                            String flair_device_uuid = "c88cc88c-c88c-c88c-c88c-000000000000";
                            List<BluetoothGattService> servicesList = gatt.getServices();
                            for (int sindex = 0; sindex < servicesList.size(); sindex++) {
                                BluetoothGattService nxt_service =  servicesList.get(sindex);
                                String service_uuid = nxt_service.getUuid().toString();
                                if (service_uuid.compareTo(flair_device_uuid) == 0) {
                                    BluetoothDevice btdev = gatt.getDevice();
                                    String name = btdev.getName();
                                    String addr = btdev.getAddress();

                                    // We found a flair device
                                    Lock rlock = m_FoundBtTableLock.readLock();
                                    rlock.lock();
                                    try {
                                        if (!m_FlairDevices.containsKey(addr)) {
                                            DatumFlairDevice foundDev = new DatumFlairDevice(addr, name);
                                            m_FlairDevices.put(addr, foundDev);

                                            Log.i(TAG, String.format("'Flair' device found. name=%s addr=%s svc=%s ", name, addr, service_uuid));

                                            Activity activity = BluetoothFlairSelectionFragment.this.getActivity();
                                            activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    BluetoothFlairSelectionFragment.this.update_device_listview(BluetoothFlairSelectionFragment.this.mListView);
                                                }
                                            });
                                        }
                                    }
                                    finally {
                                        rlock.unlock();
                                    }

                                }
                            }
                        }

                    });

                }
            }
        }
    };

    private void error_bluetooth_adapter_error()
    {
        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        activity.setResult(FlairConstants.RESULTS.RESULT_BLUETOOTH_ERROR, intent);
        activity.finish();
    }

    private void update_device_listview(ListView list_view)
    {
        Activity activity = getActivity();

        List<DatumFlairDevice> devices = null;
        Lock rlock = m_FoundBtTableLock.readLock();
        rlock.lock();
        try
        {
            devices = new ArrayList<DatumFlairDevice>(m_FlairDevices.values());
        }
        finally {
            rlock.unlock();
        }

        ArrayAdapterFlairDevice list_adapter = new ArrayAdapterFlairDevice(activity, R.layout.view_flair_device, devices);

        list_view.setAdapter(list_adapter);

        list_view.setOnItemClickListener(handler_flairdevice_list_item_clicked);
    }
}
