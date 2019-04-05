package com.skateflair.gizmos;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by myron on 2/21/16.
 */
public class GizmoDJ extends Gizmo {

    public static final String TAG = "GizmoDJ";

    public static final String PROFILE_NAME = "dj";

    public static final UUID GIZMO_UUID = UUID.fromString("21106c6e-8084-455d-8a0d-2b07e19decad");

    public GizmoDJ() {
        // Required empty public constructor
    }

    /* ================================================================================
                                    Gizmo Full Fragment
       ================================================================================ */

    public static class GizmoFragmentFull extends Gizmo.GizmoFragmentBase {

        public static GizmoFragmentBase newInstance(Gizmo gizmo) {
            GizmoFragmentBase frag = new GizmoFragmentFull();
            frag.m_gizmo = gizmo;
            return frag;
        }

        public GizmoFragmentFull() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.gizmo_dj, container, false);
        }

    }

    /* ================================================================================
                                    Gizmo Thumb Fragment
       ================================================================================ */

    public static class GizmoFragmentThumb extends Gizmo.GizmoFragmentBase {

        public static GizmoFragmentBase newInstance(Gizmo gizmo) {
            GizmoFragmentBase frag = new GizmoFragmentThumb();
            frag.m_gizmo = gizmo;
            return frag;
        }

        public GizmoFragmentThumb() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.gizmo_dj, container, false);
        }

    }

    /* ================================================================================
                                 IGizmo Implementations
       ================================================================================ */

    @Override
    public Fragment attachFullFragment() {
        GizmoFragmentFull frag = new GizmoFragmentFull();
        frag.m_gizmo = this;

        return frag;
    }

    @Override
    public Fragment attachThumbFragment() {
        GizmoFragmentThumb frag = new GizmoFragmentThumb();
        frag.m_gizmo = this;

        return frag;
    }

    @Override
    public UUID getGizmoUUID() {
        return GIZMO_UUID;
    }

    @Override
    public String getProfileName() {
        return PROFILE_NAME;
    }

    @Override
    public String getProfileXML() throws GizmoProfileException
    {
        return "";
    }

    @Override
    public void restoreProfile(Serializable obj){

    }

    @Override
    public Serializable saveProfile() {
        return null;
    }


}
