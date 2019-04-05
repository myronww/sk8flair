package com.skateflair.flair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by myron on 2/10/16.
 */
public class DatabaseHelperFlairDB extends SQLiteOpenHelper {

    public static final String TAG = "DatabaseHelperFlairDB";

    private static final String DATABASE_NAME = "FlairDB";
    private static final int DATABASE_VERSION = 2;

    /* ============================== FLAIRDEVICE TABLE ============================== */
    private static final String FLAIRDEVICE_TABLE_NAME = "FLAIR_DEVICE";
    private static final String FLAIRDEVICE_NAME = "DEVICE_NAME";
    private static final String FLAIRDEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final String FLAIRDEVICE_TABLE_CREATE =
            "CREATE TABLE " + FLAIRDEVICE_TABLE_NAME + "(" +
                    FLAIRDEVICE_ADDRESS + " TEXT," +
                    FLAIRDEVICE_NAME + " TEXT, PRIMARY KEY (" + FLAIRDEVICE_ADDRESS + "));";

    /* ============================== FLAIRGROUP TABLE ============================== */
    private static final String FLAIRGROUP_TABLE_NAME = "FLAIR_GROUP";
    private static final String FLAIRGROUP_ID = "GROUP_ID";
    private static final String FLAIRGROUP_NAME = "GROUP_NAME";
    private static final String FLAIRGROUP_ACTIVE = "GROUP_ACTIVE";

    private static final String FLAIRGROUP_TABLE_CREATE =
            "CREATE TABLE " + FLAIRGROUP_TABLE_NAME + " (" +
                    FLAIRGROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FLAIRGROUP_NAME + " TEXT, " +
                    FLAIRGROUP_ACTIVE + " SHORT);";

    /* ============================== GIZMOSTATE TABLE ============================== */
    private static final String GIZMOSTATE_TABLE_NAME = "GIZMOSTATE";
    private static final String GIZMOSTATE_UUID = "GIZMO_UUID";
    private static final String GIZMOSTATE_GROUP_NAME = "GROUP_NAME";
    private static final String GIZMOSTATE_STATE = "GIZMO_STATE";


    /* ============================== FLAIRMEMBERSHIP TABLE ============================== */
    private static final String FLAIRGROUP_MEMBERSHIP_TABLE_NAME = "FLAIR_GROUP_MEMBERSHIP";

    private static final String FLAIRGROUP_MEMBERSHIP_TABLE_CREATE =
            "CREATE TABLE " + FLAIRGROUP_MEMBERSHIP_TABLE_NAME + " (" +
                    FLAIRGROUP_ID + " INTEGER, " +
                    FLAIRDEVICE_ADDRESS + " TEXT, PRIMARY KEY(" + FLAIRGROUP_ID + "," + FLAIRDEVICE_ADDRESS + "));";

    /* ============================== SELECT QUERIES ============================== */
    private static final String  SELECT_ALL_FLAIRDEVICE = "SELECT * FROM " + FLAIRDEVICE_TABLE_NAME;
    private static final String  SELECT_ACTIVE_FLAIRGROUPS = "SELECT TOP 1 * FROM " + FLAIRGROUP_TABLE_NAME + " WHERE " + FLAIRGROUP_ACTIVE + "=1";
    private static final String  SELECT_ALL_FLAIRGROUPS = "SELECT * FROM " + FLAIRGROUP_TABLE_NAME;
    private static final String  SELECT_ALL_MEMBERS = "SELECT * FROM " + FLAIRGROUP_MEMBERSHIP_TABLE_NAME;
    private static final String  SELECT_FLAIRGROUP_MEMBERS = "SELECT * FROM " + FLAIRDEVICE_TABLE_NAME + " INNER JOIN " + FLAIRGROUP_MEMBERSHIP_TABLE_NAME + " ON " +
            FLAIRDEVICE_TABLE_NAME + "." + FLAIRDEVICE_ADDRESS + "=" + FLAIRGROUP_MEMBERSHIP_TABLE_NAME + "." + FLAIRDEVICE_ADDRESS +
            " WHERE " + FLAIRGROUP_ID + " = " + "?;" ;
    private static final String SELECT_GIZMO_STATE = "SELECT " + GIZMOSTATE_STATE + " FROM " + GIZMOSTATE_TABLE_NAME + " WHERE " + GIZMOSTATE_UUID +"=? AND " + GIZMOSTATE_GROUP_NAME + "=?";

    /* ============================== INSERT QUERIES ============================== */
    private static final String INSERT_OR_REPLACE_FLAIRDEVICE = "INSERT OR REPLACE INTO " + FLAIRDEVICE_TABLE_NAME +
            " (" + FLAIRDEVICE_ADDRESS + "," + FLAIRDEVICE_NAME + ") VALUES (?, ?)";
    private static final String INSERT_FLAIRGROUP = "INSERT INTO " + FLAIRGROUP_TABLE_NAME +
            " (" + FLAIRGROUP_NAME + ", " + FLAIRGROUP_ACTIVE + ") VALUES (?, ?)";
    private static final String INSERT_OR_REPLACE_GIZMOSTATE =  "INSERT OR REPLACE INTO " + GIZMOSTATE_TABLE_NAME +
            " (" + GIZMOSTATE_UUID + ", " + GIZMOSTATE_GROUP_NAME + ", " + GIZMOSTATE_STATE + ") VALUES (?, ?, ?)";

    /* ============================== UPDATE QUERIES ============================== */
    private static final String UPDATE_FLAIRGROUP = "UPDATE " + FLAIRGROUP_TABLE_NAME +
            " SET " + FLAIRGROUP_NAME + "=@GROUPNAME WHERE " + FLAIRGROUP_ID + "=@GROUPID";


    DatabaseHelperFlairDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FLAIRDEVICE_TABLE_CREATE);
        db.execSQL(FLAIRGROUP_TABLE_CREATE);
        db.execSQL(FLAIRGROUP_MEMBERSHIP_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public void FlairDevice_InsertOrReplace(DatumFlairDevice device)
    {
        SQLiteDatabase database = getWritableDatabase();

        database.execSQL(INSERT_OR_REPLACE_FLAIRDEVICE, device.getAsColumns());
    }

    public List<DatumFlairDevice> FlairDevice_SelectAll() {
        List<DatumFlairDevice> itemList = new ArrayList<DatumFlairDevice>();

        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(SELECT_ALL_FLAIRDEVICE, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String dev_address = cursor.getString(0);
                    String dev_name = cursor.getString(1);

                    itemList.add(new DatumFlairDevice(dev_address, dev_name));
                } while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }

        return itemList;
    }

    public Long FlairGroup_Insert(DatumFlairGroup group)
    {
        SQLiteDatabase database = getWritableDatabase();

        String group_name = group.getName();
        String group_active = group.getActive() ? "1" : "0";

        ContentValues values = new ContentValues();

        values.put("GROUP_NAME", group_name);
        values.put("GROUP_ACTIVE", group_active);

        Long grp_id = database.insert(FLAIRGROUP_TABLE_NAME, null, values);
        group.setId(grp_id);

        return grp_id;
    }

    public List<DatumFlairGroup> FlairGroup_Select_Active() {
        List<DatumFlairGroup> itemList = new ArrayList<DatumFlairGroup>();

        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(SELECT_ALL_FLAIRGROUPS, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Long grp_id = cursor.getLong(0);
                    String grp_name = cursor.getString(1);
                    Boolean grp_active = cursor.getShort(2) == 1;

                    itemList.add(new DatumFlairGroup(grp_id, grp_name, grp_active));
                } while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }

        return itemList;
    }

    public List<DatumFlairGroup> FlairGroup_SelectAll() {
        List<DatumFlairGroup> itemList = new ArrayList<DatumFlairGroup>();

        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(SELECT_ALL_FLAIRGROUPS, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Long grp_id = cursor.getLong(0);
                    String grp_name = cursor.getString(1);
                    Boolean grp_active = cursor.getShort(2) == 1;

                    itemList.add(new DatumFlairGroup(grp_id, grp_name, grp_active));
                } while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }

        return itemList;
    }

    public void FlairGroup_Set_Active(DatumFlairGroup group)
    {
        SQLiteDatabase database = getWritableDatabase();

        // Clear any groups marked as active
        ContentValues values = new ContentValues();

        values.put(FLAIRGROUP_ACTIVE, 0);

        database.update(FLAIRGROUP_TABLE_NAME, values, null, null);

        // Set the appropriate group to active
        values.put(FLAIRGROUP_ACTIVE, 1);

        Long group_id = group.getId();

        database.update(FLAIRGROUP_TABLE_NAME, values, FLAIRGROUP_ID + "=?", new String[]{group_id.toString()});
    }

    public void FlairGroup_Update(DatumFlairGroup group)
    {
        SQLiteDatabase database = getWritableDatabase();

        Long group_id = group.getId();
        String group_name = group.getName();

        ContentValues update_data = new ContentValues();

        update_data.put(FLAIRGROUP_NAME, group_name);

        database.update(FLAIRGROUP_TABLE_NAME, update_data, FLAIRGROUP_ID + "=?", new String[]{group_id.toString()});
    }

    public List<DatumFlairDevice> FlairGroup_Select_Members(DatumFlairGroup group) {
        // initialize the list
        List<DatumFlairDevice> itemList = new ArrayList<DatumFlairDevice>();

        SQLiteDatabase database = getReadableDatabase();
        try {
            Long group_id = group.getId();

            String[] args = new String[] {group_id.toString()};
            String select_members = SELECT_FLAIRGROUP_MEMBERS;
            Cursor cursor = database.rawQuery(select_members, args);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        String dev_address = cursor.getString(0);
                        String dev_name = cursor.getString(1);

                        itemList.add(new DatumFlairDevice(dev_address, dev_name));
                    } while (cursor.moveToNext());
                }
            }
            finally {
                cursor.close();
            }
        }
        finally {
            database.close();
        }

        return itemList;
    }

    public void FlairGroup_Update_Members(DatumFlairGroup group, List<DatumFlairDevice> members) {

        SQLiteDatabase database = getReadableDatabase();
        try {
            Long group_id = group.getId();

            String[] args = new String[] {group_id.toString()};

            database.delete(FLAIRGROUP_MEMBERSHIP_TABLE_NAME, "GROUP_ID=?", args);

            ContentValues values = new ContentValues();
            for (DatumFlairDevice device : members) {
                String address = device.getAddress();

                values.put(FLAIRGROUP_ID, group_id.toString());
                values.put(FLAIRDEVICE_ADDRESS, address);

                database.insert(FLAIRGROUP_MEMBERSHIP_TABLE_NAME, null, values);
            }

        }
        finally {
            database.close();
        }

        return;
    }

    public String GizmoState_Select(UUID gizmo_uuid, String group_name) {
        String gizmo_state = null;

        SQLiteDatabase database = getReadableDatabase();
        try {
            String guuid_str = gizmo_uuid.toString();

            String[] args = new String[] {guuid_str, group_name};
            String select_members = SELECT_FLAIRGROUP_MEMBERS;
            Cursor cursor = database.rawQuery(select_members, args);
            if (cursor.moveToFirst()) {
                do {
                    gizmo_state = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        }
        finally {
            database.close();
        }

        return gizmo_state;
    }

    public void GizmoState_Insert_Or_Update(UUID gizmo_uuid, String group_name, String gizmo_state) {
        SQLiteDatabase database = getWritableDatabase();

        String gizmo_uuid_str = gizmo_uuid.toString();

        database.execSQL(INSERT_OR_REPLACE_GIZMOSTATE, new String[]{gizmo_uuid_str, group_name, gizmo_state});
    }

    public void Trace_Devices()
    {
        SQLiteDatabase database = getReadableDatabase();

        Integer index = 0;
        Cursor cursor = database.rawQuery(SELECT_ALL_FLAIRDEVICE, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String dev_address = cursor.getString(0);
                    String dev_name = cursor.getString(1);

                    Log.d(TAG, "DatumFlairDevice[" + index + "] " + dev_name + ", " + dev_address);

                    index += 1;
                } while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }
    }

    public void Trace_Groups()
    {
        SQLiteDatabase database = getReadableDatabase();

        Integer index = 0;
        Cursor cursor = database.rawQuery(SELECT_ALL_FLAIRGROUPS, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Long grp_id = cursor.getLong(0);
                    String grp_name = cursor.getString(1);
                    Boolean grp_active = cursor.getShort(2) == 1 ? true : false;

                    Log.d(TAG, "DatumFlairGroup[" + index + "] " + grp_id + ", " + grp_name + ", " + grp_active);

                    index += 1;
                } while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }
    }

    public void Trace_Members()
    {
        SQLiteDatabase database = getReadableDatabase();

        Integer index = 0;
        Cursor cursor = database.rawQuery(SELECT_ALL_MEMBERS, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Long grp_id = cursor.getLong(0);
                    String dev_address = cursor.getString(1);

                    Log.d(TAG, "FlairMember[" + index + "] " + grp_id + ", " + dev_address);

                    index += 1;
                } while (cursor.moveToNext());
            }
        }
        finally {
            cursor.close();
        }
    }

}
