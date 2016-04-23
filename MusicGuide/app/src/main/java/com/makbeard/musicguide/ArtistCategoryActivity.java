package com.makbeard.musicguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.Toast;

import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.makbeard.musicguide.model.Artist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.AnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

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
                // TODO: 23.04.2016 Переделать ожидание возварщениея
                //Попробовать вынести из метода OnCreate в OnStart
                artistList.addAll(dataLoadingAsyncTask.execute().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.artists_recycler);
        //Создаём свой Adapter для RecyclerView
        ArtistRecyclerViewAdapter dataAdapter =
                new ArtistRecyclerViewAdapter(this, artistList);

        dataAdapter.setListener(new ArtistRecyclerViewAdapter.Listener() {
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

        //Создаём divider
        RecyclerViewDivider divider = new RecyclerViewDivider(
                getResources().getColor(R.color.colorMaterialGrey), 1);

        if (recyclerView != null) {

            recyclerView.addItemDecoration(divider);
            //Добавляем анимацию появления элемента
            recyclerView.setItemAnimator(new FadeInAnimator());

            //Задаём анимацию для адаптера
            ScaleInAnimationAdapter animationAdapter =
                    new ScaleInAnimationAdapter(dataAdapter);

            animationAdapter.setInterpolator(new FastOutLinearInInterpolator());
            animationAdapter.setDuration(500);

            recyclerView.setAdapter(animationAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            Log.e(TAG, "onCreate: RECYCLER == NULL!!!");
        }
    }

    /**
     * Класс для вызова загрузки данных в отдельном потоке
     */
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
