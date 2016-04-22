package com.makbeard.musicguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makbeard.musicguide.model.Artist;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс производит парсинг JSON-ссылки и сохраняет значения в виде объектов Artist
 */
public class ArtistsJsonParser {

    private static final String TAG = "ArtistsJsonParser";
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public ArtistsJsonParser(Context context) {
        mContext = context;
    }

    String url = "http://download.cdn.yandex.net/mobilization-2016/";

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .build();

    Gson gson = new GsonBuilder()
            .create();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    YandexArtistApi yandexArtistApi = retrofit.create(YandexArtistApi.class);

    /**
     * Метод возвращает список артистов
     */
    public void getArtistsList() {

        //Запускаем обработку JSON в отдельном потоке
        Call<List<Artist>> call = yandexArtistApi.getArtistsList();
        call.enqueue(new Callback<List<Artist>>() {
            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {
               if (response.isSuccessful()) {
                   ArtistDatabaseHelper artistDatabaseHelper =
                           ArtistDatabaseHelper.getInstance(mContext);
                   artistDatabaseHelper.insertArtists(response.body());
/*
                   SharedPreferences sharedPreferences = mContext.getSharedPreferences(App.PREF_DB_UPLOADED, Context.MODE_PRIVATE);
                   SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putBoolean(App.DB_UPLOADED, true);
                   editor.apply();
               */
               }
            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
            // TODO: 20.04.2016 Обработать ошибку
            }
        });
    }
}
