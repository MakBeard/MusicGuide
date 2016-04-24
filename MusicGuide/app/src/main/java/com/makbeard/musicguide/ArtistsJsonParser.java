package com.makbeard.musicguide;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makbeard.musicguide.model.Artist;

import java.io.IOException;
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
    public List<Artist> getArtistsList() {

        //Запускаем обработку JSON в отдельном потоке
        Call<List<Artist>> call = yandexArtistApi.getArtistsList();
        try {
            final Response<List<Artist>> response = call.execute();
            if (response.isSuccessful()) {

                /*
                // TODO: 24.04.2016 Вынести ArtistCategory Activity
                //Запускаем сохранение в БД в отдельном потоке
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArtistDatabaseHelper artistDatabaseHelper =
                                ArtistDatabaseHelper.getInstance(mContext);
                        artistDatabaseHelper.insertArtists(response.body());
                    }
                }).start();
                */

                return response.body();
            }
        } catch (IOException e) {
            // TODO: 22.04.2016 Обработать IOException
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
