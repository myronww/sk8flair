package com.skateflair.gizmos;

/**
 * Created by myron on 3/6/16.
 */
public class GizmoProfileException extends Exception {
    public GizmoProfileException() {

    }

    public GizmoProfileException(String message) {
        super (message);
    }

    public GizmoProfileException(Throwable cause) {
        super (cause);
    }

    public GizmoProfileException(String message, Throwable cause) {
        super (message, cause);
    }
}
