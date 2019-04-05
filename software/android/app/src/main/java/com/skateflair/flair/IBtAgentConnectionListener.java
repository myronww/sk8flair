package com.skateflair.flair;

/**
 * Created by myron on 3/5/16.
 */
public interface IBtAgentConnectionListener {
    void onConnectionResult(BtFlairDeviceAgent agent, boolean success);
}
