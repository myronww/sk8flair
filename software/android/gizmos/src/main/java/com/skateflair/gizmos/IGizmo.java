package com.skateflair.gizmos;

import android.app.Fragment;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by myron on 3/6/16.
 */
public interface IGizmo {
    public Fragment attachFullFragment();
    public Fragment attachThumbFragment();

    public UUID getGizmoUUID();
    public String getProfileName();
    public String getProfileXML() throws GizmoProfileException;
    public Serializable saveProfile();
    public void restoreProfile(Serializable obj);
}
