package com.skateflair.flair;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by myron on 2/10/16.
 */
public class DatumFlairGroup implements Parcelable  {
    private Long m_id;
    private String m_name;
    private Boolean m_active;

    public DatumFlairGroup(Long id, String name, Boolean active) {
        m_id = id;
        m_name = name;
        m_active = active;
    }

    private DatumFlairGroup(Parcel in)
    {
        readFromParcel(in);
    }

    public Boolean getActive() { return m_active; }

    public void setActive(Boolean active) { this.m_active = active; }

    public Long getId() {
        return m_id;
    }

    public void setId(Long id) {
        this.m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        this.m_name = name;
    }

    public String[] getAsColumns()
    {
        String[] columns = new String[] {m_id.toString(), m_name};
        return columns;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void readFromParcel(Parcel source) {
        m_id = source.readLong();
        m_name = source.readString();
        m_active = source.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(m_id);
        dest.writeString(m_name);
        dest.writeInt(m_active ? 1 : 0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DatumFlairGroup createFromParcel(Parcel in) {
            return new DatumFlairGroup(in);
        }

        public DatumFlairGroup[] newArray(int size) {
            return new DatumFlairGroup[size];
        }
    };

}
