package com.makbeard.musicguide;

import com.makbeard.musicguide.model.Artist;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Интерфейс для парсинга JSON
 */

public interface YandexArtistApi {
    @GET("artists.json")
    Call<List <Artist>> getArtistsList();
}
