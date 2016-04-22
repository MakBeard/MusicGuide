package com.makbeard.musicguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Student2 on 22.04.2016.
 */
public class MyPreferences {
    private static final String TAG = "MyPreferences";
    private static MyPreferences mOurInstance;
    private final SharedPreferences mSharedPreferences;
    public static final String PREF_DB_UPLOADED = "PREF_DB_UPLOADED";
    public static final String DB_UPLOADED = "DB_UPLOADED";

    public static MyPreferences getInstance(Context context) {
        if (mOurInstance == null) {
            mOurInstance = new MyPreferences(context);
        }
        return mOurInstance;
    }

    private MyPreferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREF_DB_UPLOADED, 0);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: " + key);
            }
        });
    }


    public void setField(String field) {
        mSharedPreferences.edit().putString(DB_UPLOADED, field).commit();
    }

    public String getField() {
        return mSharedPreferences.getString(DB_UPLOADED, "");
    }
}
