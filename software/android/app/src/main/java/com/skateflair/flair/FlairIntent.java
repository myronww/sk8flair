package com.skateflair.flair;

/**
 * Created by myron on 3/5/16.
 */
public final class FlairIntent {

    public final static class PAYLOADS {
        public final static String FLAIR_DEVICE_ID = "FLAIR-DEVICE";
        public final static String FLAIR_GROUP_DEVICES = "FLAIR-GROUP-DEVICES";
        public final static String FLAIR_PROFILE_NAME = "FLAIR-PROFILE-NAME";
        public final static String FLAIR_PROFILE_CONTENT = "FLAIR-PROFILE-CONTENT";
        public final static String FLAIR_PROFILE_SWITCH = "FLAIR-PROFILE-SWITCH";
    }

    public final static class ACTIONS {
        public final static class SERVICE {
            public final static String CONNECT_DEVICES = "com.flair.controllerservice.connect_devices";
            public final static String DEVICE_CONNECTION_FAILURE = "com.flair.controllerservice.device_connection_failure";
            public final static String DEVICE_CONNECTION_SUCCESS = "com.flair.controllerservice.device_connection_success";
            public final static String RESET_DEVICES = "com.flair.controllerservice.reset_devices";
            public final static String FLAIR_PROFILE_CHANGE = "com.flair.controllerservice.flair_profile_change";
            public final static String FLAIR_PROFILE_UPDATE = "com.flair.controllerservice.flair_profile_update";
            public final static String FLAIR_SYNC_TIME = "com.flair.controllerservice.flair_sync_time";
        }
    }


}
