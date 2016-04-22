package com.makbeard.musicguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.makbeard.musicguide.model.Artist;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Класс-помощник для доступа к SQLite базе
 * содержащей данные об артистах
 */
public class ArtistDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "ArtistDatabaseHelper";
    private static ArtistDatabaseHelper instance;

    public static ArtistDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new ArtistDatabaseHelper(context);
        }

        return instance;
    }

    public ArtistDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private static final String DB_NAME = "MusicGuide";

    private static final int DB_VERSION = 1;
    private static final String ARTISTS_TABLE = "artists";
    private static final String KEY_ID = "_id";
    private static final String KEY_JSONID = "jsonid";
    private static final String KEY_GENRES = "genres";
    private static final String KEY_NAME = "name";
    private static final String KEY_TRACKS = "tracks";
    private static final String KEY_ALBUMS = "albums";
    private static final String KEY_LINK = "link";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SMALLCOVER = "small";
    private static final String KEY_BIGCOVER = "big";

    private static final String DATABASE_CREATE = "create table " + ARTISTS_TABLE + " (" +
            KEY_ID + " integer primary key autoincrement, " +
            KEY_JSONID + " INTEGER, " +
            KEY_NAME + " TEXT, " +
            KEY_GENRES + " TEXT, " +
            KEY_TRACKS + " INTEGER, " +
            KEY_ALBUMS + " INTEGER, " +
            KEY_DESCRIPTION + " TEXT, " +
            KEY_SMALLCOVER + " TEXT, " +
            KEY_BIGCOVER + " TEXT, " +
            KEY_LINK + " TEXT);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + ARTISTS_TABLE);
        db.execSQL(DATABASE_CREATE);
    }

    /**
     * Метод для получения списка объектов из Db
     * @return список классов Artist
     */
    public List<Artist> getArtistsListFromDb() {
        List<Artist> artistsList = new ArrayList<>();
        Cursor cursor = fullDataQuery();

        int nameIdx =        cursor.getColumnIndex(KEY_NAME);
        int jsonIdIdx =      cursor.getColumnIndex(KEY_JSONID);
        int albumsIdx =      cursor.getColumnIndex(KEY_ALBUMS);
        int tracksIdx =      cursor.getColumnIndex(KEY_TRACKS);
        int genresIdx =      cursor.getColumnIndex(KEY_GENRES);
        int linkIdx =        cursor.getColumnIndex(KEY_LINK);
        int descriptionIdx = cursor.getColumnIndex(KEY_DESCRIPTION);
        int smallCoverIdx =  cursor.getColumnIndex(KEY_SMALLCOVER);
        int bigCiverIdx =    cursor.getColumnIndex(KEY_BIGCOVER);

        if (cursor.moveToFirst()) {

            do {
                String jsonId = cursor.getString(jsonIdIdx);
                String name = cursor.getString(nameIdx);
                int albums = cursor.getInt(albumsIdx);
                int tracks = cursor.getInt(tracksIdx);
                String genres = cursor.getString(genresIdx);
                String link = cursor.getString(linkIdx);
                String description = cursor.getString(descriptionIdx);
                String smallCover = cursor.getString(smallCoverIdx);
                String bigCover = cursor.getString(bigCiverIdx);

                artistsList.add(new Artist(name, jsonId, genres,
                        tracks, albums, link,
                        description, smallCover, bigCover));

            } while (cursor.moveToNext());
        }

        return artistsList;
    }

    // TODO: 22.04.2016 Разбить на 2 запроса: categoryQuery и detailQuery
    /**
     * Метод возвращает курсор со всеми полями из БД
     * @return cursor
     */
    public Cursor fullDataQuery() {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(
                ARTISTS_TABLE,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    /**
     * Метод записывает полученный в JSON список артистов в БД
     * @param artistList массив Artist
     */
    public void insertArtists(List<Artist> artistList) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();

            for (Artist artist : artistList) {
                ContentValues cv = new ContentValues();
                cv.put(KEY_JSONID, artist.getId());
                cv.put(KEY_NAME, artist.getName());
                cv.put(KEY_TRACKS, artist.getTracks());
                cv.put(KEY_ALBUMS, artist.getAlbums());
                cv.put(KEY_DESCRIPTION, artist.getDescription());
                cv.put(KEY_LINK, artist.getLink());
                cv.put(KEY_SMALLCOVER, artist.getSmallCover());
                cv.put(KEY_BIGCOVER, artist.getBigCover());

                StringBuilder genresSb = new StringBuilder();
                for (int i = 0; i < artist.getGenres().size(); i++) {
                    genresSb.append(artist.getGenres().get(i));
                    if (i < artist.getGenres().size() - 1) {
                        genresSb.append(", ");
                    }
                }

                cv.put(KEY_GENRES, genresSb.toString());
                //Log.d(TAG, "insertArtists: " + artist.getName());
                db.insert(ARTISTS_TABLE, null, cv);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "insertArtists: SUCCESS!");
        } finally {
            db.endTransaction();
        }
    }
}
