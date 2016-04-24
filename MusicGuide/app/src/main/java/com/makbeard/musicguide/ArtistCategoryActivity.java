package com.makbeard.musicguide;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_category);

        ArtistDatabaseHelper artistDatabaseHelper = ArtistDatabaseHelper.getInstance(this);
        Cursor cursor = artistDatabaseHelper.fullDataQuery();

        final List<Artist> artistList = new ArrayList<>();

        final ArtistRecyclerViewAdapter dataAdapter =
                new ArtistRecyclerViewAdapter(this, artistList);

        //Если cursor.moveToFirst() true, обрабатываем данные из БД. Иначе заполняем БД
        if (cursor.moveToFirst()) {
            Log.d(TAG, "onCreate: берём данные из базы");

            //Если в БД есть данные, заполняем ими artistList
            List<Artist> databaseList = artistDatabaseHelper.getArtistsListFromDb();
            dataAdapter.updateAll(databaseList);
            artistList.addAll(databaseList);

        } else {

            //Если база пустая
            if (isOnline()) {
                //Если есть интернет парсим JSON, обновляем адаптер и сохраняем в БД

                Log.d(TAG, "onCreate: берём данные из парсера");
                // TODO: 23.04.2016 Обработать медленный интернет
                AdapterLoadingAsyncTask adapterLoadingAsyncTask =
                        new AdapterLoadingAsyncTask(this, dataAdapter);
                adapterLoadingAsyncTask.execute();

            } else {
                // TODO: 23.04.2016 Обработать отсутствие интернета при пустой базе
                Toast.makeText(this, "Для заполнения БД нужен интернет...", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCreate: Нет инета");
            }
        }

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.artists_recycler);

        // TODO: 24.04.2016 Изменить алгоритм, artistList м/б 0 в начале
        dataAdapter.setListener(new ArtistRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ArtistCategoryActivity.this, ArtistDetailActivity.class);

                if (artistList.size() > 0) {
                    intent.putExtra(Artist.NAME, artistList.get(position).getName());
                    intent.putExtra(Artist.GENRES, artistList.get(position).getGenresAsString());
                    intent.putExtra(Artist.ALBUMS, artistList.get(position).getAlbums());
                    intent.putExtra(Artist.TRACKS, artistList.get(position).getTracks());
                    intent.putExtra(Artist.DESCRIPTION, artistList.get(position).getDescription());
                    intent.putExtra(Artist.BIGCOVER, artistList.get(position).getBigCover());
                }

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
            Log.e(TAG, "onCreate: RECYCLER == NULL!!!");
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
     * Класс для вызова загрузки данных в отдельном потоке
     */
    private class AdapterLoadingAsyncTask extends AsyncTask<Void, Integer, List<Artist>> {

        private Context mContext;
        private ArtistRecyclerViewAdapter mAdapter;

        public AdapterLoadingAsyncTask(Context context, ArtistRecyclerViewAdapter adapter) {
            mContext = context;
            mAdapter = adapter;
        }

        @Override
        protected List<Artist> doInBackground(Void... params) {
            ArtistsJsonParser artistsJsonParser = new ArtistsJsonParser(mContext);
            // TODO: 24.04.2016 Обрабоать SockedTimeoutException

            final List<Artist> resultList = artistsJsonParser.getArtistsList();
/*
            //Запускаем сохранение в БД в отдельном потоке
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArtistDatabaseHelper artistDatabaseHelper =
                            ArtistDatabaseHelper.getInstance(mContext);
                    artistDatabaseHelper.insertArtists(resultList);
                }
            }).start();
*/
            return resultList;
        }

        @Override
        protected void onPostExecute(List<Artist> resultList) {
            super.onPostExecute(resultList);
            Log.d(TAG, "onPostExecute: PARSER RESULT " + resultList.size());
            mAdapter.updateAll(resultList);
        }
    }
}
