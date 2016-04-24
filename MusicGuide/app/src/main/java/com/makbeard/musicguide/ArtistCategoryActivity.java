package com.makbeard.musicguide;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.makbeard.musicguide.model.Artist;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Стартовая Activity категорий,
 * отвечающая за вывод списа артистов
 */
public class ArtistCategoryActivity extends AppCompatActivity {

    private static final String TAG = "ArtistCategoryActivity";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_category);

        ArtistDatabaseHelper artistDatabaseHelper = ArtistDatabaseHelper.getInstance(this);

        final List<Artist> artistList = new ArrayList<>();

        final ArtistRecyclerViewAdapter dataAdapter =
                new ArtistRecyclerViewAdapter(this, artistList);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.artists_recycler);

        //Запрашиваем все данные из БД
        Cursor cursor = artistDatabaseHelper.fullDataQuery();

        //Если cursor.moveToFirst() true, обрабатываем данные из БД. Иначе заполняем БД
        if (cursor.moveToFirst()) {
            Log.d(TAG, "onCreate: берём данные из базы");

            //Если в БД есть данные, заполняем ими adapter
            List<Artist> databaseList = artistDatabaseHelper.getArtistsListFromDb();
            dataAdapter.updateAll(databaseList);

        } else {

            //Если база пустая
            if (isOnline()) {
                //Если есть интернет парсим JSON, обновляем adapter и сохраняем в БД
                Log.d(TAG, "onCreate: берём данные из парсера");
                // TODO: 23.04.2016 Обработать медленный интернет
                AdapterLoadingAsyncTask adapterLoadingAsyncTask =
                        new AdapterLoadingAsyncTask(this, dataAdapter);
                adapterLoadingAsyncTask.execute();

            } else {
                //Если интернета нет и БД пустая

                if (recyclerView != null) {
                    Snackbar.make(recyclerView, R.string.no_internet_connection,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.settings, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(
                                            new Intent(Settings.ACTION_SETTINGS));
                                }
                            })
                            .show();
                }
            }
        }


        dataAdapter.setListener(new ArtistRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ArtistCategoryActivity.this, ArtistDetailActivity.class);
                Artist clickedElement = (Artist) dataAdapter.getItem(position);

                intent.putExtra(Artist.NAME, clickedElement.getName());
                intent.putExtra(Artist.GENRES, clickedElement.getGenresAsString());
                intent.putExtra(Artist.ALBUMS, clickedElement.getAlbums());
                intent.putExtra(Artist.TRACKS, clickedElement.getTracks());
                intent.putExtra(Artist.DESCRIPTION, clickedElement.getDescription());
                intent.putExtra(Artist.BIGCOVER, clickedElement.getBigCover());

                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //Создаём divider
        RecyclerViewDivider divider = new RecyclerViewDivider(
                ContextCompat.getColor(this, R.color.colorMaterialGrey), 1);


        if (recyclerView != null) {

            recyclerView.addItemDecoration(divider);
            //Добавляем анимацию появления элемента
            recyclerView.setItemAnimator(new FadeInAnimator());

            //Оборачиваем адаптер с данными в адаптер анимации
            final ScaleInAnimationAdapter animationAdapter =
                    new ScaleInAnimationAdapter(dataAdapter);

            //В случае изменения данных обновляем адаптер анимации
            dataAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    animationAdapter.notifyDataSetChanged();
                }
            });

            animationAdapter.setInterpolator(new FastOutLinearInInterpolator());
            animationAdapter.setDuration(500);

            // TODO: 24.04.2016 Вернуть animationAdapter
            recyclerView.setAdapter(animationAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        } else {
            Log.d(TAG, "onCreate: RECYCLER == NULL!!!");
        }

    }

    /**
     * Метод проверяет наличие соединения с интернетом
     * @return true если есть подключение, иначе false
     */
    private boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Класс для вызова загрузки и сохранения данных в отдельном потоке
     */
    private class AdapterLoadingAsyncTask extends AsyncTask<Void, Integer, List<Artist>> {

        private Context mContext;
        private ArtistRecyclerViewAdapter mAdapter;
        private View mIndicator;

        public AdapterLoadingAsyncTask(Context context, ArtistRecyclerViewAdapter adapter) {
            mContext = context;
            mAdapter = adapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mIndicator = findViewById(R.id.avloadingindicatorview);
            mIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Artist> doInBackground(Void... params) {
            ArtistsJsonParser artistsJsonParser = new ArtistsJsonParser(mContext);

            // TODO: 24.04.2016 Обрабоать SockedTimeoutException
            final List<Artist> resultList = artistsJsonParser.getArtistsList();

            //Запускаем сохранение в БД в отдельном потоке
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArtistDatabaseHelper artistDatabaseHelper =
                            ArtistDatabaseHelper.getInstance(mContext);
                    artistDatabaseHelper.insertArtists(resultList);
                }
            }).start();


            return resultList;
        }

        @Override
        protected void onPostExecute(List<Artist> resultList) {
            super.onPostExecute(resultList);
            mIndicator.setVisibility(View.GONE);
            mAdapter.updateAll(resultList);
        }
    }
}
