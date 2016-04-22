package com.makbeard.musicguide;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Класс Application
 * Вызывается перед запуском Activity
 */
public class App extends Application {

    private static final String TAG = "APP";
    // TODO: 22.04.2016 Храним флаг загрузки
    private SharedPreferences mSharedPreferences;
    public static final String PREF_DB_UPLOADED = "PREF_DB_UPLOADED";
    //Флаг загрузки БД
    public static final String DB_UPLOADED = "DB_UPLOADED";

    @Override
    public void onCreate() {
        super.onCreate();

        mSharedPreferences = getSharedPreferences(PREF_DB_UPLOADED, MODE_PRIVATE);
        Boolean dbUploaded = mSharedPreferences.getBoolean(DB_UPLOADED, false);
        Log.d(TAG, "onCreate: APP dbUploaded " + dbUploaded);
        mSharedPreferences.getAll().clear();
/*

        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: KEY" + key);
            }
        };

        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
*/

        //Если данные в БД раньше не загружались, выполняем парсинг и сохранение
        if(!dbUploaded)
        {
            // TODO: 22.04.2016 Запускаем загрузку здесь
    //      ArtistsJsonParser artistsJsonParser = new ArtistsJsonParser(this);
    //      artistsJsonParser.getArtistsList();
            Log.d(TAG, "dbUploaded: FALSE");
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(DB_UPLOADED, true);
            editor.commit();

        } else {
            Log.d(TAG, "dbUploaded: TRUE");
        }
                // TODO: 22.04.2016 Listener на изменение флага (Вызываем в MainActivity что бы пересоздать курсор)
    }
}
