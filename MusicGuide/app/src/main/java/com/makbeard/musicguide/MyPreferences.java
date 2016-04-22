package com.makbeard.musicguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Student2 on 22.04.2016.
 */
public class MyPreferences {
    private static final String TAG = "MyPreferences";
    private static MyPreferences ourInstance;
    private final SharedPreferences prefs;
    private static final String PREFS_NAME = "PREFS_NAME";

    public static MyPreferences getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MyPreferences(context);
        }
        return ourInstance;
    }

    private MyPreferences(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, 0);

        prefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: " + key);
            }
        });
    }

    public static final String FIELD_KEY = "FIELD_KEY";

    public void setField(String field) {
        prefs.edit().putString(FIELD_KEY, field).commit();
    }

    public String getField() {
        return prefs.getString(FIELD_KEY, "");
    }
}
