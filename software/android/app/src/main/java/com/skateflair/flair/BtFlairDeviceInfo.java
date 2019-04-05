package com.skateflair.flair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by myron on 2/14/16.
 */
public class BtFlairDeviceInfo {

    private String m_flair_type;
    private List<String> m_plugins;

    public BtFlairDeviceInfo(String flair_type, List<String> plugins) {
        m_flair_type = flair_type;
        m_plugins = plugins;
    }

    public String getFlairType() {
        return m_flair_type;
    }

    public void setFlairType(String flair_type) {
        m_flair_type = flair_type;
    }

    public List<String> getPlugins() {
        return m_plugins;
    }

    public void setPlugins(List<String> plugins) {
        m_plugins = plugins;
    }

    public static String toJSon(BtFlairDeviceInfo flair_info) throws JSONException {
        String flair_type = flair_info.getFlairType();
        List<String> plugins = flair_info.getPlugins();

        JSONObject jObj = new JSONObject();
        jObj.put(FlairProtocol.JSONKeys.FLAIR_TYPE, flair_type);

        JSONArray plugins_jObj = new JSONArray();
        for (String pin : plugins) {
            plugins_jObj.put(pin);
        }

        String json_str = jObj.toString();

        return json_str;
    }

    public static BtFlairDeviceInfo fromJSon(String json_str) throws JSONException {
        JSONObject jObj = new JSONObject(json_str);

        String flair_type = jObj.getString(FlairProtocol.JSONKeys.FLAIR_TYPE);

        JSONArray plugin_array = jObj.getJSONArray(FlairProtocol.JSONKeys.PLUGINS);

        ArrayList<String> plugins = new ArrayList<String>();
        for (int i = 0; i < plugin_array.length(); i++) {
            String plugin_item = plugin_array.getString(i);
            plugins.add(plugin_item);
        }

        BtFlairDeviceInfo rtn_obj = new BtFlairDeviceInfo(flair_type, plugins);

        return rtn_obj;
    }
}
