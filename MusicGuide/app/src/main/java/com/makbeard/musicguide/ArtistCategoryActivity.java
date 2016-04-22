package com.makbeard.musicguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
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
import java.util.concurrent.ExecutionException;

/**
 * Стартовая Activity категорий,
 * отвечающая за вывод списа артистов
 */
public class ArtistCategoryActivity extends AppCompatActivity {

    private static final String TAG = "ArtistCategoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_category);

        ArtistDatabaseHelper artistDatabaseHelper = ArtistDatabaseHelper.getInstance(this);
        Cursor cursor = artistDatabaseHelper.fullDataQuery();

        final List<Artist> artistList = new ArrayList<>();

        //Если cursor.moveToFirst() true, обрабатываем данные из Db. Иначе заполняем Db
        if (cursor.moveToFirst()) {
            Log.d(TAG, "onCreate: берём данные из базы");
            artistList.addAll(artistDatabaseHelper.getArtistsListFromDb());
        } else {
            // TODO: 22.04.2016 Проверка на наличие интернета в случае пустой ДБ
            Log.d(TAG, "onCreate: берём данные из парсера");
            DataLoadingAsyncTask dataLoadingAsyncTask = new DataLoadingAsyncTask(this);
            try {
                artistList.addAll(dataLoadingAsyncTask.execute().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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

    private class DataLoadingAsyncTask extends AsyncTask<Void, Integer, List<Artist>> {

        private Context mContext;

        public DataLoadingAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected List<Artist> doInBackground(Void... params) {
            ArtistsJsonParser artistsJsonParser = new ArtistsJsonParser(mContext);
            return artistsJsonParser.getArtistsList();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
