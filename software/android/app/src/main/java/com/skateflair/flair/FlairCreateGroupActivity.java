package com.skateflair.flair;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.skateflair.flair.datum.DatabaseHelperFlairDB;
import com.skateflair.flair.datum.DatumFlairDevice;
import com.skateflair.flair.datum.DatumFlairGroup;

import java.util.ArrayList;

public class FlairCreateGroupActivity extends Activity {

    DatabaseHelperFlairDB m_FlairData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flair_create_group);

        m_FlairData = new DatabaseHelperFlairDB(this);

        initialize_controls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flair_create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.closeButton) {
            Intent intent = this.getIntent();
            this.setResult(Activity.RESULT_CANCELED, intent);
            this.finish();
        }
        else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialize_controls() {

        Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        Button btnSave = (Button)this.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity activity = FlairCreateGroupActivity.this;

                FragmentManager fman = activity.getFragmentManager();

                EditText et_GroupName = (EditText)activity.findViewById(R.id.txtGroupName);
                String grp_name = et_GroupName.getText().toString();

                BluetoothFlairSelectionFragment frag_bt_devices = (BluetoothFlairSelectionFragment) fman.findFragmentById(R.id.fragFlairDevicesSelection);

                DatumFlairGroup fgroup = new DatumFlairGroup((long)0, grp_name, true);
                Long grp_id = m_FlairData.FlairGroup_Insert(fgroup);

                ArrayList<DatumFlairDevice> selected_devices = frag_bt_devices.getSelected();
                for (DatumFlairDevice fdevice : selected_devices) {
                    m_FlairData.FlairDevice_InsertOrReplace(fdevice);
                }

                m_FlairData.FlairGroup_Update_Members(fgroup, selected_devices);

                Intent intent = getIntent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
