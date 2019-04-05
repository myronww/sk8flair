package com.skateflair.flair;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


/**
 * Created by myron on 2/13/16.
 */
public class BluetoothFlairSelectionFragment extends Fragment {

    public static final String TAG = "BtFlairSelFragment";

    BluetoothAdapter mBluetoothAdapter;

    private Hashtable<String, DatumFlairDevice> m_PairedFlairDevices = new Hashtable<String, DatumFlairDevice>();

    public ArrayList<DatumFlairDevice> getSelected() {
        ArrayList<DatumFlairDevice> selected_devices = new ArrayList<DatumFlairDevice>();

        for (DatumFlairDevice fdevice : m_PairedFlairDevices.values()) {
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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            error_bluetooth_adapter_error();
        }

        update_bluetooth_flair_devices();

        Activity activity = getActivity();

        ListView list_view = (ListView)view.findViewById(R.id.lstBluetoothDeviceSelection);

        update_device_listview(list_view);
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

            DatumFlairDevice sel_device = m_PairedFlairDevices.get(device_address);
            sel_device.setSelected(!is_checked);

            item_model.chkSelected.setChecked(!is_checked);
        }

    };

    private void error_bluetooth_adapter_error()
    {
        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        activity.setResult(FlairConstants.Results.RESULT_BLUETOOTH_ERROR, intent);
        activity.finish();
    }

    private void update_bluetooth_flair_devices()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                String dev_name = device.getName();
                String dev_address = device.getAddress();
                if (!m_PairedFlairDevices.containsKey(dev_address)) {

                    boolean has_spp_uuid = false;

                    ParcelUuid dev_uuids[] = device.getUuids();
                    for (ParcelUuid p_uuid : dev_uuids){
                        String uuid = p_uuid.getUuid().toString();
                        String flair_uuid = FlairConstants.UUIDS.FLAIR_CONTROL_SERVICE_UUID.toString();
                        if (uuid.equals(flair_uuid)) {
                            has_spp_uuid = true;
                            break;
                        }
                    }

                    if (has_spp_uuid) {
                        try {
                            BluetoothSocket dsock = device.createRfcommSocketToServiceRecord(FlairConstants.UUIDS.FLAIR_CONTROL_SERVICE_UUID);
                            dsock.connect();
                        } catch (IOException e1) {
                            Log.e(TAG, "Unable to connect to 'Flair Service' on device '" + dev_name + "'.");
                        }
                        DatumFlairDevice dev_obj = new DatumFlairDevice(dev_address, dev_name);

                        m_PairedFlairDevices.put(dev_address, dev_obj);

                        Log.d(TAG, "New 'Flair' device found: " + dev_name + "/" + dev_address);
                    }
                }
            }
        }
    }

    private void update_device_listview(ListView list_view)
    {
        Activity activity = getActivity();

        List<DatumFlairDevice> devices = new ArrayList<DatumFlairDevice>(m_PairedFlairDevices.values());

        ArrayAdapterFlairDevice list_adapter = new ArrayAdapterFlairDevice(activity, R.layout.view_flair_device, devices);

        list_view.setAdapter(list_adapter);

        list_view.setOnItemClickListener(handler_flairdevice_list_item_clicked);
    }
}
