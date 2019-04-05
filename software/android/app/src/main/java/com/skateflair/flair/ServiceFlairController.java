package com.skateflair.flair;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.skateflair.gizmos.GizmoFactory;
import com.skateflair.gizmos.IGizmo;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by myron on 2/21/16.
 */
public class ServiceFlairController extends Service {

    public static final String TAG = "ServiceFlairCtlr";

    private class BtAgentConnector extends Thread {

        private BtFlairDeviceAgent m_agent;
        private IBtAgentConnectionListener m_listener;

        public BtAgentConnector(BtFlairDeviceAgent agent, IBtAgentConnectionListener listener) {
            m_agent = agent;
            m_listener = listener;
        }

        @Override
        public void run() {

            boolean success = false;
            try {
                m_agent.Connect();
                success = true;
            }
            catch (IOException io_err) {
                Log.d(TAG, "IOException: '" + io_err.toString() + "'...");
            }
            catch (IllegalArgumentException arg_err) {
                Log.d(TAG, "IOException: '" + arg_err.toString() + "'...");
            }

            m_listener.onConnectionResult(m_agent, success);

            return;
        }
    }

    public class FlairControllerReceiver extends BroadcastReceiver implements IBtAgentConnectionListener {

        public static final String TAG = "FlairCtlrReceiver";

        private class BtAgentReConnector extends Thread {

            public final int RECONNECT_DELAY = 5000; //5 seconds

            private boolean m_running;
            private long m_last_delay;

            public BtAgentReConnector() {
                m_running = false;
                m_last_delay = System.currentTimeMillis();
            }

            @Override
            public void run() {

                m_running = true;

                try {
                    while (true) {
                        m_ConnErrSemaphore.acquire();
                        if (!m_running) {
                            break;
                        }

                        BtFlairDeviceAgent agent = null;

                        Lock wlock = m_TableLock.writeLock();
                        wlock.lock();
                        try {
                            String dev_addr = m_ConnErrQueue.take();
                            agent = m_AgentErrTable.remove(dev_addr);
                        }
                        finally {
                            wlock.unlock();
                        }

                        boolean success = reconnect_agent(agent);
                        if (success) {
                            agent.setRetryDelay(0);
                            mark_btagent_up(agent);
                        }
                        else {
                            agent.setRetryDelay(RECONNECT_DELAY);
                            mark_btagent_down(agent);
                        }

                    }
                } catch (InterruptedException xcpt) {
                }
            }

            public void shutdown() {
                m_running = false;
                m_ConnErrSemaphore.release();
            }

            private boolean reconnect_agent(BtFlairDeviceAgent agent) {
                boolean success = false;

                try {
                    long retry_delay = agent.getRetryDelay();
                    if (retry_delay > 0) {
                        delay_retry(retry_delay);
                    }

                    agent.Disconnect();
                    agent.Connect();
                    success = true;
                } catch (Exception xcpt) {
                }

                return success;
            }

            private void delay_retry(long retry_delay)
            {
                long current_time = System.currentTimeMillis();
                long time_diff = current_time - m_last_delay;
                if (time_diff > retry_delay) {
                    try {
                        sleep(retry_delay);
                    } catch (InterruptedException xcpt) {
                    }
                    m_last_delay = System.currentTimeMillis();
                }
            }
        }

        protected Hashtable<String, BtFlairDeviceAgent> m_AgentTable;
        protected Hashtable<String, BtFlairDeviceAgent> m_AgentErrTable;
        protected Hashtable<String, DatumFlairDevice> m_DeviceTable;

        protected ReentrantReadWriteLock m_TableLock;
        protected LinkedBlockingQueue<String> m_ConnErrQueue;
        protected Semaphore m_ConnErrSemaphore;

        BtAgentReConnector m_ReConnector;

        public FlairControllerReceiver() {
            m_AgentTable = new Hashtable<>();
            m_AgentErrTable = new Hashtable<>();
            m_ConnErrQueue = new LinkedBlockingQueue<String>();
            m_ConnErrSemaphore = new Semaphore(0);
            m_DeviceTable = new Hashtable<>();
            m_TableLock = new ReentrantReadWriteLock();

            m_ReConnector = new BtAgentReConnector();
            m_ReConnector.start();

        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();

            m_ReConnector.shutdown();
            m_ReConnector = null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "Processing command '" + action + "'...");

            if (action.equals(FlairIntent.ACTIONS.SERVICE.CONNECT_DEVICES)) {
                Parcelable[] flair_devices_list = intent.getParcelableArrayExtra(FlairIntent.PAYLOADS.FLAIR_GROUP_DEVICES);

                RegisterDevices(flair_devices_list);

                ConnectDevices();

            } else if (action.equals(FlairIntent.ACTIONS.SERVICE.RESET_DEVICES)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                adapter.cancelDiscovery();

                DisconnectDevices();

                Parcelable[] flair_devices_list = intent.getParcelableArrayExtra(FlairIntent.PAYLOADS.FLAIR_GROUP_DEVICES);

                RegisterDevices(flair_devices_list);

                ConnectDevices();

            } else if (action.equals(FlairIntent.ACTIONS.SERVICE.FLAIR_PROFILE_CHANGE)) {
                String profile_name = intent.getStringExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_NAME);

                for (BtFlairDeviceAgent agent : m_AgentTable.values()) {
                    try {
                        agent.Send_ProfileChange(profile_name);
                    } catch (IOException ioerr) {
                        mark_btagent_down(agent);
                    }
                }
            } else if (action.equals(FlairIntent.ACTIONS.SERVICE.FLAIR_PROFILE_UPDATE)) {
                String profile_name = intent.getStringExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_NAME);
                String profile_content = intent.getStringExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_CONTENT);
                boolean switch_profile = intent.getBooleanExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_SWITCH, false);

                for (BtFlairDeviceAgent agent : m_AgentTable.values()) {
                    try {
                        agent.Send_ProfileUpdate(profile_name, profile_content);
                    } catch (IOException ioerr) {
                        mark_btagent_down(agent);
                    }
                }
            } else if (action.equals(FlairIntent.ACTIONS.SERVICE.FLAIR_SYNC_TIME)) {

                for (BtFlairDeviceAgent agent : m_AgentTable.values()) {
                    long now_millis = System.currentTimeMillis();
                    try {
                        agent.Send_TimeSet(now_millis);
                    } catch (IOException ioerr) {
                        mark_btagent_down(agent);
                    }
                }
            }
        }

        @Override
        public void onConnectionResult(BtFlairDeviceAgent agent, boolean success) {
            String dev_addr = agent.getAddress();

            if (success) {
                mark_btagent_up(agent);
            } else {
                mark_btagent_down(agent);
            }
        }

        protected void ConnectDevices()
        {
            DatumFlairDevice[] device_array = null;

            Lock rlock = m_TableLock.readLock();
            rlock.lock();
            try {
                Collection<DatumFlairDevice> dev_coll = m_DeviceTable.values();
                device_array = dev_coll.toArray(new DatumFlairDevice[dev_coll.size()]);
            }
            finally {
                rlock.unlock();
            }

            for (DatumFlairDevice fd_datum :  device_array) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                adapter.cancelDiscovery();

                BtFlairDeviceAgent dev_agent = new BtFlairDeviceAgent(adapter, fd_datum);

                BtAgentConnector connector = new BtAgentConnector(dev_agent, this);
                connector.start();
            }
        }

        protected void DisconnectDevices()
        {
            Lock wlock = m_TableLock.writeLock();
            wlock.lock();
            try {
                for (BtFlairDeviceAgent agent : m_AgentTable.values()) {
                    agent.Disconnect();
                }

                m_AgentTable.clear();
            }
            finally {
                wlock.unlock();
            }
        }

        protected void RegisterDevices(Parcelable[] flair_devices_list)
        {
            Lock wlock = m_TableLock.writeLock();
            wlock.lock();
            try {
                for (Parcelable fdobj : flair_devices_list) {
                    DatumFlairDevice fd_datum = (DatumFlairDevice) fdobj;
                    String fd_addr = fd_datum.getAddress();
                    m_DeviceTable.put(fd_addr, fd_datum);
                }
            }
            finally {
                wlock.unlock();
            }
        }

        private void broadcast_device_connection_error(String dev_addr)
        {
            //Send a broadcast message that a flair device connection was unsuccessful
            Intent status_intent = new Intent();

            status_intent.setAction(FlairIntent.ACTIONS.SERVICE.DEVICE_CONNECTION_FAILURE);
            status_intent.addCategory(Intent.CATEGORY_DEFAULT);
            status_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_DEVICE_ID, dev_addr);

            sendBroadcast(status_intent);

            Log.d(TAG, "Error connecting to bluetooth device - '" + dev_addr + "'...");
        }

        private void broadcast_device_connection_success(String dev_addr)
        {
            //Send a broadcast message that a flair device connection was successful
            Intent status_intent = new Intent();
            status_intent.setAction(FlairIntent.ACTIONS.SERVICE.DEVICE_CONNECTION_SUCCESS);
            status_intent.addCategory(Intent.CATEGORY_DEFAULT);
            status_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_DEVICE_ID, dev_addr);

            sendBroadcast(status_intent);

            Log.d(TAG, "Successfully connected to bluetooth device - '" + dev_addr + "'...");
        }

        protected void mark_btagent_up(BtFlairDeviceAgent agent) {
            String dev_addr = agent.getAddress();

            Lock wlock = m_TableLock.writeLock();

            wlock.lock();
            try {
                m_AgentTable.put(dev_addr, agent);
            }
            finally {
                wlock.unlock();
            }

            broadcast_device_connection_success(dev_addr);
        }

        protected void mark_btagent_down(BtFlairDeviceAgent agent) {
            String dev_addr = agent.getAddress();

            Lock wlock = m_TableLock.writeLock();
            wlock.lock();
            try {
                if (m_AgentTable.containsKey(dev_addr)) {
                    m_AgentTable.remove(dev_addr);
                }
                m_AgentErrTable.put(dev_addr, agent);
                m_ConnErrQueue.add(dev_addr);
                m_ConnErrSemaphore.release();
            }
            finally {
                wlock.unlock();
            }

            broadcast_device_connection_error(dev_addr);
        }

    }

    class FlairRemoteControlsPagerAdapter extends FragmentStatePagerAdapter {

        private IGizmo[] m_GizmoArray;
        private Fragment[] m_FragmentMap = new Fragment[4];
        private int m_FragmentCount = 4;

        public FlairRemoteControlsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);

            m_GizmoArray = GizmoFactory.loadGizmos();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);

            m_FragmentMap[position] = null;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);

            m_FragmentMap[position] = null;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment gizmo = null;

            gizmo = m_GizmoArray[position].attachThumbFragment();

            m_FragmentMap[position] = gizmo;

            return gizmo;
        }

        public Fragment getFragment(int position) {
            return m_FragmentMap[position];
        }

        @Override
        public int getCount() {
            return m_FragmentCount;
        }
    }

    private FlairControllerReceiver m_IntentReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        m_IntentReceiver = new FlairControllerReceiver();

        IntentFilter filter = new IntentFilter();

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.CONNECT_DEVICES);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.RESET_DEVICES);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.FLAIR_PROFILE_CHANGE);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.FLAIR_PROFILE_UPDATE);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.FLAIR_SYNC_TIME);

        registerReceiver(m_IntentReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        m_IntentReceiver.DisconnectDevices();

        unregisterReceiver(m_IntentReceiver);
        m_IntentReceiver = null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getAction();

        Log.d(TAG, "Processing start command '" + action + "'...");

        // Relay this message to the receiver, be broadcasting it
        if (action == FlairIntent.ACTIONS.SERVICE.CONNECT_DEVICES) {
            Parcelable[] flair_devices_list = intent.getParcelableArrayExtra(FlairIntent.PAYLOADS.FLAIR_GROUP_DEVICES);

            Intent relay_intent = new Intent();
            relay_intent.setAction(FlairIntent.ACTIONS.SERVICE.CONNECT_DEVICES);
            relay_intent.addCategory(Intent.CATEGORY_DEFAULT);
            relay_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_GROUP_DEVICES, flair_devices_list);

            sendBroadcast(relay_intent);
        }

        return Service.START_NOT_STICKY;
    }

}
