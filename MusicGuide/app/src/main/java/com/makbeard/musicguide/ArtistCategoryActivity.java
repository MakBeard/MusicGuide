package com.makbeard.musicguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.makbeard.musicguide.model.Artist;

import java.util.ArrayList;
import java.util.List;

/**
 * Стартовая Activity категорий,
 * отвечающая за вывод списа артистов
 */
public class ArtistCategoryActivity extends AppCompatActivity {

    private static final String TAG = "ArtistCategoryActivity";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_category);

        mSharedPreferences = getSharedPreferences(App.PREF_DB_UPLOADED, MODE_PRIVATE);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: KEY " + key);
            }
        });
        /*
        SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                Log.d(TAG, "onSharedPreferenceChanged: KEY" + key);
            }
        };
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        */
        ArtistDatabaseHelper artistDatabaseHelper = ArtistDatabaseHelper.getInstance(this);
        Cursor cursor = artistDatabaseHelper.fullDataQuery();

        final List<Artist> artistList = new ArrayList<>();

        //Если cursor.moveToFirst() true, обрабатываем данные из Db. Иначе заполняем Db
        if (cursor.moveToFirst()) {

            artistList.addAll(artistDatabaseHelper.getArtistsListFromDb());

        } else {
            ArtistsJsonParser artistsJsonParser = new ArtistsJsonParser(this);
            artistsJsonParser.getArtistsList();
            //SharedPreferencesListener перехватывает результат artistsJsonParser.getArtistsList()
            //И передаёт в курсор
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.artists_recycler);
        ArtistRecyclerViewAdapter artistRecyclerViewAdapter =
                new ArtistRecyclerViewAdapter(this, artistList);
        artistRecyclerViewAdapter.setListener(new ArtistRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ArtistCategoryActivity.this, ArtistDetailActivity.class);
                intent.putExtra(Artist.NAME, artistList.get(position).getName());
                intent.putExtra(Artist.GENRES, artistList.get(position).getGenresAsString());
                intent.putExtra(Artist.ALBUMS, artistList.get(position).getFormattedAlbums());
                intent.putExtra(Artist.TRACKS, artistList.get(position).getFormattedTracks());
                intent.putExtra(Artist.DESCRIPTION, artistList.get(position).getDescription());
                intent.putExtra(Artist.BIGCOVER, artistList.get(position).getBigCover());
                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        if (recyclerView != null) {
            // TODO: 22.04.2016 Настроить анимацию
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            recyclerView.setItemAnimator(itemAnimator);
            recyclerView.setAdapter(artistRecyclerViewAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            Log.e(TAG, "onCreate: RECYCLER == NULL!!!");
        }
    }
}
