package com.skateflair.flair;

import android.app.Activity;

import java.util.UUID;

public final class FlairConstants {

    public final static UUID FLAIR_DEVICE_SERVICE_ID = UUID.fromString("C88CC88C-C88C-C88C-C88C-000000000000");

    public final static class PAYLOADS {
        public final static String FLAIR_DEVICE_ID = "FLAIR-DEVICE";
        public final static String FLAIR_GROUP_DEVICES = "FLAIR-GROUP-DEVICES";
        public final static String FLAIR_PROFILE_NAME = "FLAIR-PROFILE-NAME";
        public final static String FLAIR_PROFILE_CONTENT = "FLAIR-PROFILE-CONTENT";
        public final static String FLAIR_PROFILE_SWITCH = "FLAIR-PROFILE-SWITCH";
    }

    public final static class ACTIONS {
        public final static class SERVICE {
            public final static String CONNECT_DEVICES = "com.flair.bleservice.connect_devices";
            public final static String DISCONNECT_DEVICES = "com.flair.bleservice.reset_devices";

            public final static String FLAIR_PROFILE_CHANGE = "com.flair.bleservice.flair_profile_change";
            public final static String FLAIR_PROFILE_UPDATE = "com.flair.bleservice.flair_profile_update";
            public final static String FLAIR_SYNC_TIME = "com.flair.bleservice.flair_sync_time";
        }

        public final static class ACTIVITY {
            public final static String UPDATE_CONNECTED_DEVICES = "com.flair.flairactivity.update_connected_devices";
        }

        public final static class BILLBOARD {
            public final static String DEVICE_CONNECTION_FAILURE = "com.flair.flairbillboard.device_connection_failure";
            public final static String DEVICE_CONNECTION_SUCCESS = "com.flair.flairbillboard.device_connection_success";
        }
    }

    public static final class ACTIVITIES {
        public static final int ACTIVITY_BLUETOOTH_ERROR_MESSAGE = 0;
        public static final int ACTIVITY_CREATE_FLAIR_GROUP = 1;
        public static final int ACTIVITY_REQUEST_BLUETOOTH_ENABLE = 2;
        public static final int ACTIVITY_REMOTE_SCREENLOCK_CONTROL = 3;

    }

    public static final class MODES {
        public static final String AMBIENT = "ambient";
        public static final String COMPASS = "compass";
        public static final String RAINBOW = "rainbow";
    }

    public static final class RESULTS {
        public final static int RESULT_SETTINGS_MODIFIED = 200;

        public final static int RESULT_FATAL_ERROR = Activity.RESULT_FIRST_USER + 600;
        public final static int RESULT_BLUETOOTH_ERROR = RESULT_FATAL_ERROR + 1;
        public final static int RESULT_CREATE_FLAIR_GROUP_CANCEL = RESULT_FATAL_ERROR + 2;
    }

    public static final class UUIDS {
        public static final UUID FLAIR_DEVICE_UUID              = UUID.fromString("88888888-8888-8888-8888-888888888888");
        public static final UUID FLAIR_CONTROL_SERVICE_UUID     = UUID.fromString("c88cc88c-c88c-c88c-c88c-000000000000");
        public static final UUID FLAIR_DEVICETYPE_UUID          = UUID.fromString("c88cc88c-c88c-c88c-0000-111111111111");
        public static final UUID FLAIR_FLAIR_MODE_IO_UUID       = UUID.fromString("c88cc88c-c88c-c88c-0000-222222222222");
        public static final UUID FLAIR_WIFI_MODE_IO_UUID        = UUID.fromString("c88cc88c-c88c-c88c-0000-333333333333");
        public static final UUID FLAIR_CONTROL_READ_IO_UUID     = UUID.fromString("c88cc88c-c88c-c88c-0000-444444444444");
        public static final UUID FLAIR_CONTROL_WRITE_IO_UUID    = UUID.fromString("c88cc88c-c88c-c88c-0000-555555555555");
    }
}
