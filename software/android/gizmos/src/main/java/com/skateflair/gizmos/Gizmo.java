package com.skateflair.gizmos;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by myron on 3/13/16.
 */
public abstract class Gizmo implements IGizmo {

    protected IGizmoChangedListener m_Listener;


    /* ================================================================================
                                 IGizmo Implementations
       ================================================================================ */


    public abstract Fragment attachFullFragment();

    public abstract Fragment attachThumbFragment();

    public abstract UUID getGizmoUUID();

    public abstract String getProfileXML() throws GizmoProfileException;

    public abstract Serializable saveProfile();

    public abstract void restoreProfile(Serializable obj);


    /* ================================================================================
                                    Gizmo Fragment Base
       ================================================================================ */

    public abstract static class GizmoFragmentBase extends Fragment implements IGizmoFragment {

        protected Gizmo m_gizmo;
        protected Activity m_activity;

        public GizmoFragmentBase() {
        }

        public IGizmo getGizmo() {
            return m_gizmo;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            Activity activity = getActivity();
            try {
                this.m_gizmo.m_Listener = (IGizmoChangedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement IGizmoChangedListener");
            }
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            try {
                this.m_gizmo.m_Listener = (IGizmoChangedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement IGizmoChangedListener");
            }
        }

        @Override
        public void onDetach() {
            super.onDetach();
            this.m_gizmo.m_Listener = null;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if ((isVisibleToUser) && (this.m_gizmo != null)) {
                this.m_gizmo.notifyGizmoListenerOfActivation();
            }
        }
    }

    protected void notifyGizmoListenerOfDirty() {
        if (m_Listener != null) {
            m_Listener.onGizmoDirty(Gizmo.this);
        }
    }

    protected void notifyGizmoListenerOfActivation() {
        if (m_Listener != null) {
            m_Listener.onGizmoActivate(Gizmo.this);
        }
    }

}
