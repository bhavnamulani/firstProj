package com.androidframework.pref;

import android.content.Context;
import android.content.SharedPreferences;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Preference class to keep local data
 * NOTE: New instance will not affect the data
 */
public class SharedPref {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Shared preferences file name
    private static final String PREF_NAME = "framework_pref";


    public static final String KEY_USER_NAME = "KEY_USER_NAME";
    public static final String KEY_PWD = "KEY_PWD";


    public SharedPref(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void put(String key, Object value) {
        if (value == null) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Float) {
            editor.putFloat(key, ((Float) value).floatValue());
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, ((Boolean) value).booleanValue());
        }
        boolean saved = editor.commit();

        if (saved) {
            System.out.println(">>saved >>" + key);
        } else {
            System.out.println(">>unable to save >>" + key);
        }
    }

    public boolean getBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public String getString(String key) {
        return pref.getString(key, null);
    }

    public int getInt(String key) {
        return pref.getInt(key, 0);
    }

    public boolean getSettingBool(String key) {
        return pref.getBoolean(key, true);
    }

    public int getIntWithMinusOneAsDefault(String key) {
        return pref.getInt(key, -1);
    }

    public float getFloat(String key) {
        return pref.getFloat(key, 0.0f);
    }


    // method to clear all the values from shared pref. called on logout
    public void clearSharedPref() {
        pref.edit().clear().commit();
    }

    ////////////----- getter setters for distance setting


    public void putMap(Map<String, String> inputMap, String key) {
        JSONObject jsonObject = new JSONObject(inputMap);
        String jsonString = jsonObject.toString();
        editor.remove(key).commit();
        editor.putString(key, jsonString).commit();
    }

    public Map<String, String> getMap(String mapKey) {
        Map<String, String> outputMap = new HashMap<String, String>();
        try {
            String jsonString = pref.getString(mapKey, (new JSONObject()).toString());
            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keysItr = jsonObject.keys();
            while (keysItr.hasNext()) {
                String key = keysItr.next();
                String value = (String) jsonObject.get(key);
                outputMap.put(key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }
}
