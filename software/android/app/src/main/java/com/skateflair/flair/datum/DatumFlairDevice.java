package com.skateflair.flair.datum;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by myron on 2/10/16.
 */
public class DatumFlairDevice implements Parcelable {
    private String m_address;
    private String m_name;
    private boolean m_selected;
    private Drawable m_icon;

    public DatumFlairDevice(String address, String name) {
        m_address = address;
        m_name = name;
        m_selected = false;
        m_icon = null;
    }

    private DatumFlairDevice(Parcel in)
    {
        readFromParcel(in);
    }

    public String getAddress() {
        return m_address;
    }

    public void setAddress(String address) {
        this.m_address = address;
    }

    public Drawable getIcon() { return m_icon; }

    public void setIcon(Drawable icon) { this.m_icon = icon; }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public Boolean getSelected() {
        return m_selected;
    }

    public void setSelected(Boolean selected) {
        this.m_selected = selected;
    }

    public String[] getAsColumns()
    {
        String[] columns = new String[] {m_address, m_name};
        return columns;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel source) {
        m_address = source.readString();
        m_name = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_address);
        dest.writeString(m_name);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DatumFlairDevice createFromParcel(Parcel in) {
            return new DatumFlairDevice(in);
        }

        public DatumFlairDevice[] newArray(int size) {
            return new DatumFlairDevice[size];
        }
    };

}
