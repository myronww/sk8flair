package com.skateflair.flair;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by myron on 2/10/16.
 */
public class BluetoothConnector extends Thread
{
    private final BluetoothAdapter m_Adapter;
    private final BluetoothDevice m_Device;
    private final BluetoothSocket m_Socket;
    private final BluetoothConnectionListener m_Listener;


    public BluetoothConnector(BluetoothAdapter adapter, BluetoothDevice device, UUID service_uuid, BluetoothConnectionListener listener) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;

        m_Adapter = adapter;
        m_Device = device;
        m_Listener = listener;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(service_uuid);
        } catch (IOException e) { }

        m_Socket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        m_Adapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            m_Socket.connect();

            m_Listener.onConnectionSuccess(m_Socket);

        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                m_Socket.close();
            } catch (IOException closeException) { }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            m_Socket.close();
        } catch (IOException e) { }
    }
}
