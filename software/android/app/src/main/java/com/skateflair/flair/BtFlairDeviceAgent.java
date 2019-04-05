package com.skateflair.flair;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by myron on 2/14/16.
 */
public class BtFlairDeviceAgent {

    public static final String TAG = "BtFlairDeviceAgent";

    public static final String ERROR_NOCONNECTION = String.format("%s: Connection must be called first to open a socket.", TAG);

    private String m_flair_name;
    private String m_flair_addr;

    private BluetoothAdapter m_adapter = null;
    private BluetoothDevice m_device = null;
    private BluetoothSocket m_socket = null;

    private Boolean m_connected = false;

    private long m_RetryDelay = 0;

    public BtFlairDeviceAgent(BluetoothAdapter adapter, DatumFlairDevice flair) {
        m_adapter = adapter;
        m_flair_name = flair.getName();
        m_flair_addr = flair.getAddress();
    }

    public BtFlairDeviceAgent(BluetoothAdapter adapter, DatumFlairDevice flair, BluetoothDevice device) {
        m_adapter = adapter;
        m_flair_name = flair.getName();
        m_flair_addr = flair.getAddress();

        m_device = device;
    }

    public String getAddress() {
        return m_flair_addr;
    }

    public boolean getConnected() {
        return m_connected;
    }

    public String getName() {
        return m_flair_name;
    }

    public long getRetryDelay() {
        return m_RetryDelay;
    }

    public void setRetryDelay(long retry_delay) {
        m_RetryDelay = retry_delay;
    }

    public void Connect() throws IllegalArgumentException, IOException
    {
        if ( m_device == null) {
            m_device = m_adapter.getRemoteDevice(m_flair_addr);
        }

        m_socket = m_device.createRfcommSocketToServiceRecord(FlairConstants.UUIDS.FLAIR_CONTROL_SERVICE_UUID);
        m_socket.connect();

        read_greating();

        m_connected = true;
    }

    public void Disconnect()
    {
        try {
            m_socket.close();
            m_socket = null;
        } catch (IOException closeException) { }
    }

    public String Query_Echo(String msg) throws IOException, JSONException {
        String reply = query_command(String.format("%s %s", FlairProtocol.Commands.ECHO, msg));

        return reply;
    }

    public BtFlairDeviceInfo Query_FlairInfo() throws IOException, JSONException {
        String reply = query_command(FlairProtocol.Commands.FLAIRINFO);

        BtFlairDeviceInfo dev_info = BtFlairDeviceInfo.fromJSon(reply);

        return dev_info;
    }

    public String Send_Calibrate() throws IOException {
        return send_command(FlairProtocol.Commands.CALIBRATE);
    }

    public String Send_Goodbye() throws IOException {
        return send_command(FlairProtocol.Commands.GOODBYE);
    }

    public String Send_Hotspot_OPEN(String ssid) throws IOException {
        return send_command(String.format("%s %s %s", FlairProtocol.Commands.HOTSPOT, ssid, FlairProtocol.SecMode.OPEN));
    }

    public String Send_Hotspot_WPA2(String ssid, String passkey) throws IOException {
        return send_command(String.format("%s %s %s %s", FlairProtocol.Commands.HOTSPOT, ssid, FlairProtocol.SecMode.WPA2, passkey));
    }

    public String Send_LightsOff() throws IOException {
        return send_command(FlairProtocol.Commands.LIGHTS_OFF);
    }

    public String Send_LightsOn() throws IOException {
        return send_command(FlairProtocol.Commands.LIGHTS_ON);
    }

    public String Send_ProfileChange(String profile) throws IOException {
        return send_command(String.format("%s %s", FlairProtocol.Commands.PROFILE, profile));
    }

    public String Send_ProfileUpdate(String profile, String content) throws IOException {
        return send_command(String.format("%s %s %s", FlairProtocol.Commands.PROFILE, profile, content));
    }

    public String Send_Reboot() throws IOException {
        return send_command(FlairProtocol.Commands.REBOOT);
    }

    public String Send_Record() throws IOException {
        return send_command(FlairProtocol.Commands.RECORD);
    }

    public String Send_Reset() throws IOException {
        return send_command(FlairProtocol.Commands.RESET);
    }

    public String Send_WifiOff(String profile) throws IOException {
        return send_command(FlairProtocol.Commands.WIFI_OFF);
    }

    public String Send_WifiOn(String profile) throws IOException {
        return send_command(FlairProtocol.Commands.WIFI_ON);
    }

    public String Send_TimeSet(long time_millis) throws IOException {
        long seconds = (long)((time_millis) / 1000);
        long nano_seconds = (time_millis - (seconds * 1000)) * 1000000;

        return send_command(String.format("%s %d %d", FlairProtocol.Commands.TIMESET, seconds, nano_seconds));
    }

    private void log_response(String response) {
        Log.d(TAG, "RESPONSE (" + m_flair_name + "): " + response);
    }

    private void read_greating() throws IOException
    {
        InputStream in_stream = m_socket.getInputStream();
        InputStreamReader in_reader = new InputStreamReader(in_stream);

        char[] resp_buffer = new char[1024];

        int count = in_reader.read(resp_buffer);
        String greeting = new String(resp_buffer, 0, count);
    }

    private String query_command(String command) throws IOException {

        String response = null;

        if (m_socket == null) {
            throw new IOException(ERROR_NOCONNECTION);
        }

        char[] resp_buffer = new char[1024];

        InputStream in_stream = m_socket.getInputStream();
        InputStreamReader in_reader = new InputStreamReader(in_stream);

        OutputStream out_stream = m_socket.getOutputStream();
        OutputStreamWriter out_writer = new OutputStreamWriter(out_stream);
        out_writer.write(command);
        out_writer.flush();

        int count = in_reader.read(resp_buffer);
        response = new String(resp_buffer, 0, count);

        log_response(response);

        return response;
    }

    private String send_command(String command) throws IOException {

        String response = null;

        if (m_socket == null) {
            throw new IOException(ERROR_NOCONNECTION);
        }

        char[] resp_buffer = new char[1024];

        InputStream in_stream = m_socket.getInputStream();
        InputStreamReader in_reader = new InputStreamReader(in_stream);

        OutputStream out_stream = m_socket.getOutputStream();
        OutputStreamWriter out_writer = new OutputStreamWriter(out_stream);
        out_writer.write(command);
        out_writer.flush();

        int count = in_reader.read(resp_buffer);
        response = new String(resp_buffer, 0, count);

        log_response(response);

        return response;
    }


}
