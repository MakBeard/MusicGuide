package com.makbeard.musicguide;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makbeard.musicguide.model.ArtistModel;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс производит парсинг JSON-ссылки и сохраняет значения в виде объектов ArtistModel
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
    public List<ArtistModel> getArtistsList() {

        //Запускаем обработку JSON в отдельном потоке
        Call<List<ArtistModel>> call = yandexArtistApi.getArtistsList();
        try {
            final Response<List<ArtistModel>> response = call.execute();
            if (response.isSuccessful()) {
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
