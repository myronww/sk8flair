package com.skateflair.gizmos;

/**
 * Created by myron on 7/31/16.
 */
public final class GizmoFactory {
    public static IGizmo[] loadGizmos() {

        IGizmo[] gizmos = new IGizmo[4];

        gizmos[0] = new GizmoSolid();
        gizmos[1] = new GizmoWheel();
        gizmos[2] = new GizmoCompass();
        gizmos[3] = new GizmoDJ();

        return gizmos;
    }
}
