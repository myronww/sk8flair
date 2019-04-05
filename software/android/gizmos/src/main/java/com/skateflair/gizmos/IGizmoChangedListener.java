package com.skateflair.gizmos;

/**
 * Created by myron on 3/12/16.
 */
public interface IGizmoChangedListener {
    void onGizmoLoad(IGizmo gizmo);
    void onGizmoDirty(IGizmo gizmo);
    void onGizmoActivate(IGizmo gizmo);
}
