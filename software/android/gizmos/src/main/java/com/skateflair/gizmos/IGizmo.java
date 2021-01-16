package com.skateflair.gizmos;

import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by myron on 3/6/16.
 */
public interface IGizmo {
    Fragment attachFullFragment();
    Fragment attachThumbFragment();

    UUID getGizmoUUID();
    String getProfileName();
    String getProfileJSON() throws GizmoProfileException;
    Serializable saveProfile();
    void restoreProfile(Serializable obj);
}
