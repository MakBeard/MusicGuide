package com.makbeard.musicguide.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.makbeard.musicguide.ArtistDatabaseHelper;
import com.makbeard.musicguide.ArtistRecyclerViewAdapter;
import com.makbeard.musicguide.ArtistsJsonParser;
import com.makbeard.musicguide.R;
import com.makbeard.musicguide.model.ArtistModel;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInAnimator;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;

/**
 * Стартовая Activity категорий,
 * отвечающая за вывод списа артистов
 */
public class ArtistCategoryActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener{

    private static final String TAG = "ArtistCategoryActivity";
    private ArtistRecyclerViewAdapter mDataAdapter;
    private ArrayList<ArtistModel> mArtistModelList;
    private RecyclerView mRecyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Настраиваем кнопку поиска
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(itemSearch);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_category);

        ArtistDatabaseHelper artistDatabaseHelper = ArtistDatabaseHelper.getInstance(this);

        mArtistModelList = new ArrayList<>();

        mDataAdapter =
                new ArtistRecyclerViewAdapter(this, mArtistModelList);

        mRecyclerView = (RecyclerView) findViewById(R.id.artists_recycler);

        //Запрашиваем все данные из БД
        Cursor cursor = artistDatabaseHelper.fullDataQuery();

        //Если cursor.moveToFirst() true, обрабатываем данные из БД. Иначе заполняем БД
        if (cursor.moveToFirst()) {

            //Если в БД есть данные, заполняем ими adapter
            List<ArtistModel> databaseList = artistDatabaseHelper.getArtistsListFromDb();
            mArtistModelList.addAll(databaseList);
            mDataAdapter.updateAll(databaseList);

        } else {

            //Если база пустая
            if (isOnline()) {
                //Если есть интернет парсим JSON, обновляем adapter и сохраняем в БД

                // TODO: 23.04.2016 Обработать медленный интернет
                AdapterLoadingAsyncTask adapterLoadingAsyncTask =
                        new AdapterLoadingAsyncTask(this, mDataAdapter);
                adapterLoadingAsyncTask.execute();

            } else {
                //Если интернета нет и БД пустая

                if (mRecyclerView != null) {
                    //Выводим уведомление
                    Snackbar.make(mRecyclerView, R.string.no_internet_connection,
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

        //Создаём слушать для перехода в DetailActivity
        mDataAdapter.setListener(new ArtistRecyclerViewAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ArtistCategoryActivity.this, ArtistDetailActivity.class);
                ArtistModel clickedElement = (ArtistModel) mDataAdapter.getItem(position);

                intent.putExtra(ArtistModel.NAME, clickedElement.getName());
                intent.putExtra(ArtistModel.GENRES, clickedElement.getGenresAsString());
                intent.putExtra(ArtistModel.ALBUMS, clickedElement.getAlbums());
                intent.putExtra(ArtistModel.TRACKS, clickedElement.getTracks());
                intent.putExtra(ArtistModel.DESCRIPTION, clickedElement.getDescription());
                intent.putExtra(ArtistModel.BIGCOVER, clickedElement.getBigCover());

                startActivity(intent);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        //Создаём divider
        RecyclerViewDivider divider = new RecyclerViewDivider(
                ContextCompat.getColor(this, R.color.colorMaterialGrey), 1);

        if (mRecyclerView != null) {

            mRecyclerView.addItemDecoration(divider);

            //Добавляем анимацию появления элемента
            mRecyclerView.setItemAnimator(new FadeInAnimator());

            //Оборачиваем адаптер с данными в адаптер анимации
            final ScaleInAnimationAdapter animationAdapter =
                    new ScaleInAnimationAdapter(mDataAdapter);

            //В случае изменения адаптера данных обновляем адаптер анимации
            mDataAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    animationAdapter.notifyDataSetChanged();
                }
            });

            animationAdapter.setInterpolator(new FastOutLinearInInterpolator());
            animationAdapter.setDuration(500);

            mRecyclerView.setAdapter(animationAdapter);
            mRecyclerView.setLayoutManager(linearLayoutManager);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        //Настаиваем механизм поиска
        List<ArtistModel> filteredModelList = filter(mArtistModelList, query);
        mDataAdapter.animateTo(filteredModelList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    private List<ArtistModel> filter(List<ArtistModel> list, String query) {
        query = query.toLowerCase();

        final List<ArtistModel> filteredModelList = new ArrayList<>();
        for (ArtistModel model : list) {
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    /**
     * Класс для вызова загрузки и сохранения данных в отдельном потоке
     */
    private class AdapterLoadingAsyncTask extends AsyncTask<Void, Integer, List<ArtistModel>> {

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
            //Делаем видимым индикатор
            mIndicator = findViewById(R.id.avloadingindicatorview);
            if (mIndicator != null) {
                mIndicator.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<ArtistModel> doInBackground(Void... params) {
            ArtistsJsonParser artistsJsonParser = new ArtistsJsonParser();

            // TODO: 24.04.2016 Обрабоать SockedTimeoutException
            final List<ArtistModel> resultList = artistsJsonParser.getArtistsList();

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
        protected void onPostExecute(List<ArtistModel> resultList) {
            super.onPostExecute(resultList);
            //Обновляем лист с данными
            mArtistModelList.addAll(resultList);
            //Прячем индикатор
            mIndicator.setVisibility(View.GONE);
            //Обновляем адаптер
            mAdapter.updateAll(resultList);
        }
    }
}
