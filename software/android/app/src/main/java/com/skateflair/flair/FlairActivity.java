package com.skateflair.flair;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.skateflair.flair.datum.DatabaseHelperFlairDB;
import com.skateflair.flair.datum.DatumFlairDevice;
import com.skateflair.flair.datum.DatumFlairGroup;
import com.skateflair.flair.service.ServiceFlairController;
import com.skateflair.gizmos.GizmoFactory;
import com.skateflair.gizmos.GizmoProfileException;
import com.skateflair.gizmos.IGizmo;
import com.skateflair.gizmos.IGizmoChangedListener;
import com.skateflair.gizmos.IGizmoFragment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class FlairActivity extends FragmentActivity implements IGizmoChangedListener {

    public static final String TAG = "FlairActivity";

    private Button m_BtnSync;
    private Button m_BtnNavNext;
    private Button m_BtnNavPrev;
    private Button m_BtnSettings;

    private FlairActivityIntentReceiver m_ActivityReceiver;
    private DatabaseHelperFlairDB m_FlairData;

    private FlairBillboard m_FlairBillboard;
    private DatumFlairGroup m_FlairGroup;
    private List<DatumFlairDevice> m_GroupMembers;

    private IGizmo m_CurrentGizmo;

    private boolean m_FirstStart = true;

    class FlairControlsPagerAdapter extends FragmentStatePagerAdapter {

        private IGizmo[] m_GizmoArray;
        private Fragment[] m_FragmentMap = new Fragment[4];
        private int m_FragmentCount = 4;

        public FlairControlsPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);

            m_GizmoArray = GizmoFactory.loadGizmos();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);

            m_FragmentMap[position] = null;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment gizmo = null;

            gizmo = m_GizmoArray[position].attachFullFragment();

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


    private FlairControlsPagerAdapter m_FragmentAdapter;
    private ViewPager m_FragmentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flair);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // We have to make sure to request location permissions for BLE discovery to work
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
        }

        m_FlairData = new DatabaseHelperFlairDB(this);

        Intent start_service_intent = new Intent(FlairActivity.this, ServiceFlairController.class);
        startService(start_service_intent);

        // Create an activity receiver so we can receive responses from the Flair Service
        m_ActivityReceiver = new FlairActivityIntentReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(FlairConstants.ACTIONS.ACTIVITY.UPDATE_CONNECTED_DEVICES);

        registerReceiver(m_ActivityReceiver, filter);

        m_BtnSettings = (Button)findViewById(R.id.btnSettings);
        m_BtnSettings.setOnClickListener(onButtonSettingsClick);

        m_BtnSync = (Button)findViewById(R.id.btnSync);
        m_BtnSync.setOnClickListener(onButtonSyncClick);

        m_BtnNavNext = (Button)findViewById(R.id.btnNavNext);
        m_BtnNavNext.setOnClickListener(onButtonNavNextClick);

        m_BtnNavPrev = (Button)findViewById(R.id.btnNavPrev);
        m_BtnNavPrev.setOnClickListener(onButtonNavPrevClick);

        m_FragmentAdapter = new FlairControlsPagerAdapter(this.getSupportFragmentManager());
        m_FragmentPager = (ViewPager)findViewById(R.id.vpControlsPager);
        m_FragmentPager.setAdapter(m_FragmentAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (m_FirstStart) {
            m_FirstStart = false;

            refresh_active_flair_group();
        }
    }

    private void refresh_active_flair_group() {

        List<DatumFlairGroup> active_group_list = m_FlairData.FlairGroup_Select_Active();
        if (active_group_list.size() <= 0) {
            Intent activityIntent = new Intent(this, FlairCreateGroupActivity.class);

            startActivityForResult(activityIntent, FlairConstants.ACTIVITIES.ACTIVITY_CREATE_FLAIR_GROUP);
        }
        else {
            m_FlairData.Trace_Devices();
            m_FlairData.Trace_Groups();
            m_FlairData.Trace_Members();

            m_FlairGroup = active_group_list.get(0);
            m_GroupMembers = m_FlairData.FlairGroup_Select_Members(m_FlairGroup);

            Window w = getWindow();
            String new_title = "Flair - " + m_FlairGroup.getName();
            w.setTitle(new_title);
            this.setTitle(new_title);

            m_FlairBillboard = (FlairBillboard)getSupportFragmentManager().findFragmentById(R.id.fragFlairBillboard);
            m_FlairBillboard.setFlairGroupInfo(m_FlairGroup.getName(), m_GroupMembers);

            Intent update_connected_intent = new Intent(FlairConstants.ACTIONS.ACTIVITY.UPDATE_CONNECTED_DEVICES);
            sendBroadcast(update_connected_intent);

            Log.i(TAG, "Connect Devices intent sent to the Flair Service.");
        }
    }

    private Button.OnClickListener onButtonSettingsClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

            //TODO: Implement the listener for the settings button
            // Intent start_scan_intent = new Intent();
            // start_scan_intent.setAction(FlairConstants.ACTIONS.SERVICE.SCAN_START);
            // start_scan_intent.addCategory(Intent.CATEGORY_DEFAULT);
            // sendBroadcast(start_scan_intent);
        }
    };

    private Button.OnClickListener onButtonNavNextClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentPage = m_FragmentPager.getCurrentItem();
            int totalPages = m_FragmentPager.getAdapter().getCount();

            int nextPage = (currentPage + 1) % totalPages;

            m_FragmentPager.setCurrentItem(nextPage, true);
        }
    };

    private Button.OnClickListener onButtonNavPrevClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int currentPage = m_FragmentPager.getCurrentItem();
            int totalPages = m_FragmentPager.getAdapter().getCount();

            int previousPage = (currentPage - 1) % totalPages;

            m_FragmentPager.setCurrentItem(previousPage, true);
        }
    };

    private Button.OnClickListener onButtonSyncClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = m_FragmentPager.getCurrentItem();
            FlairControlsPagerAdapter adapter = (FlairControlsPagerAdapter)m_FragmentPager.getAdapter();

            try {
                IGizmoFragment curr_gizmo_frag = (IGizmoFragment)adapter.getFragment(index);
                IGizmo curr_gizmo = curr_gizmo_frag.getGizmo();
                String profile_name = curr_gizmo.getProfileName();
                String profile_content = curr_gizmo.getProfileJSON();

                Intent time_sync_intent = new Intent();
                time_sync_intent.setAction(FlairConstants.ACTIONS.SERVICE.FLAIR_SYNC_TIME);
                time_sync_intent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(time_sync_intent);

                Intent profile_sync_intent = new Intent();
                profile_sync_intent.setAction(FlairConstants.ACTIONS.SERVICE.FLAIR_PROFILE_UPDATE);
                profile_sync_intent.addCategory(Intent.CATEGORY_DEFAULT);
                profile_sync_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_NAME, profile_name);
                profile_sync_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_CONTENT, profile_content);
                profile_sync_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_SWITCH, true);

                sendBroadcast(profile_sync_intent);

                Serializable sobj = curr_gizmo.saveProfile();
                if (sobj != null) {
                    String content = serialize_object(sobj);
                    if (content != null) {
                        UUID gizmo_uuid = curr_gizmo.getGizmoUUID();
                        String group_name = m_FlairGroup.getName();
                        m_FlairData.GizmoState_Insert_Or_Update(gizmo_uuid, group_name, content);
                    }
                }
            }
            catch(GizmoProfileException xcpt) {
            }

        }
    };

    @Override
    public void onGizmoActivate(IGizmo gizmo) {

        m_CurrentGizmo = gizmo;

        String profile_name = gizmo.getProfileName();

        Intent profile_sw_intent = new Intent();
        profile_sw_intent.setAction(FlairConstants.ACTIONS.SERVICE.FLAIR_PROFILE_CHANGE);
        profile_sw_intent.addCategory(Intent.CATEGORY_DEFAULT);
        profile_sw_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_PROFILE_NAME, profile_name);

        sendBroadcast(profile_sw_intent);
    }

    @Override
    public void onGizmoDirty(IGizmo gizmo) {
        m_BtnSync.setEnabled(true);
    }


    @Override
    public void onGizmoLoad(IGizmo gizmo)
    {
        String group_name = m_FlairGroup.getName();
        UUID gizmo_uuid = gizmo.getGizmoUUID();

        String gizmo_state = m_FlairData.GizmoState_Select(gizmo_uuid, group_name);
        if (gizmo_state != null) {
            Serializable state_obj = deserialize_object(gizmo_state);
            if (state_obj != null) {
                gizmo.restoreProfile(state_obj);
            }
        }
    }

    private Serializable deserialize_object(String content) {
        Serializable sobj = null;

        ByteArrayInputStream instream = new ByteArrayInputStream(content.getBytes());
        try {
            ObjectInputStream ois = new ObjectInputStream(instream);
            try {
                sobj = (Serializable) ois.readObject();
            }
            finally {
                ois.close();
            }
        } catch (ClassNotFoundException xcp) {
            Log.e(TAG, xcp.toString());
        } catch (IOException xcp) {
            Log.e(TAG, xcp.toString());
        }

        return sobj;
    }

    private String serialize_object(Serializable sobj) {
        String content = null;

        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ostream);
            try {
                oos.writeObject(sobj);
            } finally {
                oos.close();
            }
            ostream.reset();
            content = ostream.toString("UTF-8");
        }
        catch (IOException xcp) {
            Log.e(TAG, xcp.toString());
        }

        return content;
    }

    public class FlairActivityIntentReceiver extends BroadcastReceiver {

        public static final String TAG = "FlairActIntentReceiver";

        public FlairActivityIntentReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "Processing command '" + action + "'...");

            if (action.equals(FlairConstants.ACTIONS.ACTIVITY.UPDATE_CONNECTED_DEVICES)) {
                Intent connect_devices_intent = new Intent(FlairConstants.ACTIONS.SERVICE.CONNECT_DEVICES);
                connect_devices_intent.addCategory(Intent.CATEGORY_DEFAULT);
                connect_devices_intent.putExtra(FlairConstants.PAYLOADS.FLAIR_GROUP_DEVICES, m_GroupMembers.toArray(new Parcelable[m_GroupMembers.size()]));
                sendBroadcast(connect_devices_intent);
            }
        }
    }
}
