package com.skateflair.flair;

/**
 * Created by myron on 2/14/16.
 */
public class FlairProtocol
{
    public static final class JSONKeys {
        public final static String FLAIR_TYPE = "flair-type";
        public final static String PLUGINS = "plugins";
    }

    public static final class Commands {
        public final static String CALIBRATE = "CALIBRATE";
        public final static String ECHO = "ECHO";
        public final static String FLAIRINFO = "FLAIRINFO";
        public final static String GOODBYE = "GOODBYE";
        public final static String HOTSPOT = "HOTSPOT";
        public final static String LIGHTS_OFF = "LIGHTS OFF";
        public final static String LIGHTS_ON = "LIGHTS ON";
        public final static String PROFILE = "PROFILE";
        public final static String PROFILES = "PROFILES";
        public final static String REBOOT = "REBOOT";
        public final static String RECORD = "RECORD";
        public final static String RESET = "RESET";
        public final static String TIMESET = "TIMESET";
        public final static String WIFI_OFF = "WIFI OFF";
        public final static String WIFI_ON = "WIFI ON";

    }

    public static final class SecMode {
        public final static String OPEN = "OPEN";
        public final static String WPA2 = "WPA2";
    }
}
