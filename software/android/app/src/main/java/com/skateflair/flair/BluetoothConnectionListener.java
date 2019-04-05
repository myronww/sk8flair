package com.skateflair.flair;

import android.bluetooth.BluetoothSocket;

/**
 * Created by myron on 2/10/16.
 */
public abstract class BluetoothConnectionListener
{
    public abstract void onConnectionSuccess(BluetoothSocket socket);
}