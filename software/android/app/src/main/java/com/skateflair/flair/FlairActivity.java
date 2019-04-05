package com.skateflair.flair;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RemoteViews;

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

public class FlairActivity extends Activity implements IGizmoChangedListener {

    public final String TAG = "FlairActivity";

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
        public void destroyItem(View container, int position, Object object) {
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

    private BluetoothAdapter m_BluetoothAdapter;

    private DatabaseHelperFlairDB m_FlairData;

    private DatumFlairGroup m_FlairGroup;
    private List<DatumFlairDevice> m_GroupMembers;

    private boolean m_FirstStart = true;

    private FlairBillboard m_FlairBillboard;

    private Button m_BtnSync;
    private Button m_BtnNavNext;
    private Button m_BtnNavPrev;
    private Button m_BtnSettings;

    private IGizmo m_CurrentGizmo;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case FlairConstants.Activities.ACTIVITY_BLUETOOTH_ERROR_MESSAGE:
                finish();
                break;
            case FlairConstants.Activities.ACTIVITY_CREATE_FLAIR_GROUP:
                if (resultCode != Activity.RESULT_OK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Flair Group Error");
                    builder.setMessage("You must create at lease one flair group to enable the applications functionality.");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            refresh_active_flair_group();
                        }
                    });
                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                break;
            case FlairConstants.Activities.ACTIVITY_REQUEST_BLUETOOTH_ENABLE:
                if (resultCode != Activity.RESULT_OK) {
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Bluetooth Error")
                            .setMessage("Bluetooth must be enabled for 'Sk8Flair' run.")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }})
                            .create();
                    dialog.show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flair);

        m_FragmentAdapter = new FlairControlsPagerAdapter(getFragmentManager());
        m_FragmentPager = (ViewPager)findViewById(R.id.vpControlsPager);
        m_FragmentPager.setAdapter(m_FragmentAdapter);

        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (m_BluetoothAdapter == null) {
            error_bluetooth_adapter_error();
        }

        m_FlairData = new DatabaseHelperFlairDB(this);

        m_FlairBillboard = (FlairBillboard)getFragmentManager().findFragmentById(R.id.fragFlairBillboard);

        m_BtnSync = (Button)findViewById(R.id.btnSync);
        m_BtnSync.setEnabled(false);
        m_BtnSync.setOnClickListener(onButtonSyncClick);

        m_BtnNavPrev = (Button)findViewById(R.id.btnNavPrev);
        m_BtnNavPrev.setOnClickListener(onButtonNavPrevClick);

        m_BtnNavNext = (Button)findViewById(R.id.btnNavNext);
        m_BtnNavNext.setOnClickListener(onButtonNavNextClick);

        m_BtnSettings = (Button)findViewById(R.id.btnSettings);
        m_BtnSettings.setOnClickListener(onButtonSettingsClick);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //remote_hide_notification();

        stopService(new Intent(this, ServiceFlairController.class));
    }

    @Override
    public void onGizmoActivate(IGizmo gizmo) {

        m_CurrentGizmo = gizmo;

        String profile_name = gizmo.getProfileName();

        Intent time_sync_intent = new Intent();
        time_sync_intent.setAction(FlairIntent.ACTIONS.SERVICE.FLAIR_SYNC_TIME);
        time_sync_intent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(time_sync_intent);

        Intent profile_sw_intent = new Intent();
        profile_sw_intent.setAction(FlairIntent.ACTIONS.SERVICE.FLAIR_PROFILE_CHANGE);
        profile_sw_intent.addCategory(Intent.CATEGORY_DEFAULT);
        profile_sw_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_NAME, profile_name);

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

    @Override
    protected void onStart() {
        super.onStart();

        if (m_FirstStart) {
            m_FirstStart = false;

            refresh_active_flair_group();

            //remote_show_notification();
        }
    }

    private void refresh_active_flair_group() {
        List<DatumFlairGroup> active_group_list = m_FlairData.FlairGroup_Select_Active();
        if (active_group_list.size() <= 0) {
            Intent activityIntent = new Intent(this, FlairCreateGroupActivity.class);

            startActivityForResult(activityIntent, FlairConstants.Activities.ACTIVITY_CREATE_FLAIR_GROUP);
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

            Intent connect_devices_intent = new Intent(FlairIntent.ACTIONS.SERVICE.CONNECT_DEVICES, null, this, ServiceFlairController.class);
            connect_devices_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_GROUP_DEVICES, m_GroupMembers.toArray(new Parcelable[m_GroupMembers.size()]));

            m_FlairBillboard.setFlairDevices(m_GroupMembers);

            startService(connect_devices_intent);
        }
    }

    private void error_bluetooth_adapter_error()
    {
        LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View bt_error_popup = layoutInflater.inflate(R.layout.activity_bluetooth_initialize_error, null);

        final PopupWindow popupWindow = new PopupWindow(bt_error_popup,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button btnDismiss = (Button)bt_error_popup.findViewById(R.id.btnExit);
        btnDismiss.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
            }});

        popupWindow.showAsDropDown(bt_error_popup);

        finish();
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

    protected void remote_hide_notification() {
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancel(FlairConstants.Activities.ACTIVITY_REMOTE_SCREENLOCK_CONTROL);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void remote_show_notification() {

        // Set Notification Title
        String remote_title = getString(R.string.title_flair_remote);

        Context context = FlairActivity.this;

        //PendingIntent activityPendingIntent = getActivityPendingIntent();
        //Intent not_intent = new Intent(context, FlairRemoteActivity.class);
        // Send data to NotificationView Class
        //not_intent.putExtra("title", remote_title);
        //not_intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // Open NotificationView.java Activity
        //PendingIntent pIntent = PendingIntent.getActivity(
        //        FlairActivity.this,
        //        FlairConstants.Activities.ACTIVITY_REMOTE_SCREENLOCK_CONTROL,
        //        not_intent,
        //        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //Notifications must have these three
        builder.setSmallIcon(R.mipmap.ic_launcher);

        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.view_flair_remote);

        // Locate and set the Image into customnotificationtext.xml ImageViews
        remoteViews.setImageViewResource(R.id.imageNotifLeft, R.mipmap.ic_launcher);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.lbltitle, getString(R.string.title_flair_remote));

        builder.setContent(remoteViews);

        builder.setCategory(Notification.CATEGORY_SERVICE);

        // Make sure the notification stays at the top of the list
        builder.setPriority(Notification.PRIORITY_MAX);

        // Moke sure the notification cannot be swipped away
        builder.setOngoing(true);

        // Open NotificationView Class on Notification Click

        // Open NotificationView.java Activity
        //PendingIntent pIntent = PendingIntent.getActivity(
        //        FlairActivity.this,
        //        FlairConstants.Activities.ACTIVITY_REMOTE_SCREENLOCK_CONTROL,
        //        not_intent,
        //        PendingIntent.FLAG_UPDATE_CURRENT);


        //builder.setContentTitle(strtitle);
        //builder.setContentText("Solid");

        // Set the notification content
        //builder.setContent(remoteViews);

        //builder.setContentIntent(pIntent);
        // Set Icon

        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        // Set Ticker Message
        //builder.setTicker(context.getString(R.string.app_name));

        // Dismiss Notification
        //

        Notification notification = builder.build();

        notification.bigContentView = remoteViews;

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Build Notification with Notification Manager
        notificationmanager.notify(FlairConstants.Activities.ACTIVITY_REMOTE_SCREENLOCK_CONTROL, notification);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flair, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Button.OnClickListener onButtonSyncClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = m_FragmentPager.getCurrentItem();
            FlairControlsPagerAdapter adapter = (FlairControlsPagerAdapter)m_FragmentPager.getAdapter();

            try {
                IGizmoFragment curr_gizmo_frag = (IGizmoFragment)adapter.getFragment(index);
                IGizmo curr_gizmo = curr_gizmo_frag.getGizmo();
                String profile_name = curr_gizmo.getProfileName();
                String profile_content = curr_gizmo.getProfileXML();

                Intent time_sync_intent = new Intent();
                time_sync_intent.setAction(FlairIntent.ACTIONS.SERVICE.FLAIR_SYNC_TIME);
                time_sync_intent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(time_sync_intent);

                Intent profile_sync_intent = new Intent();
                profile_sync_intent.setAction(FlairIntent.ACTIONS.SERVICE.FLAIR_PROFILE_UPDATE);
                profile_sync_intent.addCategory(Intent.CATEGORY_DEFAULT);
                profile_sync_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_NAME, profile_name);
                profile_sync_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_CONTENT, profile_content);
                profile_sync_intent.putExtra(FlairIntent.PAYLOADS.FLAIR_PROFILE_SWITCH, true);

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

    private Button.OnClickListener onButtonSettingsClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO: Implement the listener for the settings button
        }
    };
}
