package com.skateflair.flair;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FlairBillboard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FlairBillboard extends Fragment {

    public static final String TAG = "FlairBillboard";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DEVICES = "devices";
    private static final String ARG_GROUPNAME = "groupname";

    // TODO: Rename and change types of parameters
    private String m_GroupName = "unknown";
    private DatumFlairDevice[] m_Devices;
    private Hashtable<String, FlairStatusView> m_DevicesViews;

    private LinearLayout m_DeviceLayout;

    private FlairNotificationReceiver m_NotificationReceiver;

    public class FlairNotificationReceiver extends BroadcastReceiver {

        public static final String TAG = "FlairNotifReceiver";

        public FlairNotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(FlairIntent.ACTIONS.SERVICE.DEVICE_CONNECTION_SUCCESS)) {
                String dev_id = intent.getStringExtra(FlairIntent.PAYLOADS.FLAIR_DEVICE_ID);

                FlairStatusView sview = m_DevicesViews.get(dev_id);
                sview.setFlairStatus(true);
            }
            else if (action.equals(FlairIntent.ACTIONS.SERVICE.DEVICE_CONNECTION_FAILURE)) {
                String dev_id = intent.getStringExtra(FlairIntent.PAYLOADS.FLAIR_DEVICE_ID);

                FlairStatusView sview = m_DevicesViews.get(dev_id);
                sview.setFlairStatus(false);
            }
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param devices List of 'DatumFlairDevice' objects.
     * @return A new instance of fragment FlairBillboard.
     */
    // TODO: Rename and change types and number of parameters
    public static FlairBillboard newInstance(String group_name, DatumFlairDevice[] devices) {

        FlairBillboard fragment = new FlairBillboard();

        Bundle args = new Bundle();
        args.putParcelableArray(ARG_DEVICES, devices);
        args.putParcelableArray(ARG_GROUPNAME, devices);

        fragment.setArguments(args);

        return fragment;
    }

    public FlairBillboard() {
        // Required empty public constructor
        m_Devices = new DatumFlairDevice[] {};
        m_DevicesViews = new Hashtable<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {

            Parcelable[] parcel_array  = args.getParcelableArray(ARG_DEVICES);
            m_Devices = Arrays.copyOf(parcel_array, parcel_array.length, DatumFlairDevice[].class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fview = inflater.inflate(R.layout.fragment_flair_billboard, container, false);

        m_DeviceLayout = (LinearLayout)fview.findViewById(R.id.layFlairListLayout);

        //Button sync_button = (Button)fview.findViewById(R.id.btnSync);
        //sync_button.setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        FlairBillboard.this.onButtonPressed(URI_BUTTON_SetTINGS);
        //    }
        //});

        m_NotificationReceiver = new FlairNotificationReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.DEVICE_CONNECTION_SUCCESS);
        filter.addAction(FlairIntent.ACTIONS.SERVICE.DEVICE_CONNECTION_FAILURE);

        Activity activity = getActivity();
        activity.registerReceiver(m_NotificationReceiver, filter);

        return fview;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setFlairDevices(Collection<DatumFlairDevice> devices) {
        m_Devices = devices.toArray(new DatumFlairDevice[devices.size()]);

        refresh_flair_devices();
    }

    private void refresh_flair_devices() {
        m_DeviceLayout.removeAllViewsInLayout();
        m_DevicesViews.clear();

        Context context = m_DeviceLayout.getContext();

        for (DatumFlairDevice dev : m_Devices) {
            Bitmap dev_icon = lookup_device_icon(dev);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;

            FlairStatusView statusView = new FlairStatusView(context);
            statusView.setLayoutParams(params);
            statusView.setFlairIcon(dev_icon);

            m_DeviceLayout.addView(statusView);
            m_DevicesViews.put(dev.getAddress(), statusView);
        }
    }

    private Bitmap lookup_device_icon(DatumFlairDevice device) {
        Resources res = getResources();

        return BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
    }
}
