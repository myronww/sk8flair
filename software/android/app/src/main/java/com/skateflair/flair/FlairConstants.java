package com.skateflair.flair;

import android.app.Activity;

import java.util.UUID;

/**
 * Created by myron on 2/10/16.
 */
public class FlairConstants {
    public static final class Results {
        public final static int RESULT_FATAL_ERROR = Activity.RESULT_FIRST_USER + 100;
        public final static int RESULT_BLUETOOTH_ERROR = RESULT_FATAL_ERROR + 1;
        public final static int RESULT_CREATE_FLAIR_GROUP_CANCEL = RESULT_FATAL_ERROR + 2;
    }

    public static final class Activities {
        public static final int ACTIVITY_BLUETOOTH_ERROR_MESSAGE = 0;
        public static final int ACTIVITY_CREATE_FLAIR_GROUP = 1;
        public static final int ACTIVITY_REQUEST_BLUETOOTH_ENABLE = 2;
        public static final int ACTIVITY_REMOTE_SCREENLOCK_CONTROL = 3;

    }

    public static final class UUIDS {
        public static final UUID FLAIR_CONTROL_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    }
}
