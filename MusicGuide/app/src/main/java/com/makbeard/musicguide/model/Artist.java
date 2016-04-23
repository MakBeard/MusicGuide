package com.makbeard.musicguide.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс-модель, соответствущий структуре JSON
 */
public class Artist {
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String GENRES = "genres";
    public static final String TRACKS = "tracks";
    public static final String ALBUMS = "albums";
    public static final String BIGCOVER = "bigcover";

    @SerializedName("name")
    private String mName;

    @SerializedName("id")
    private String mId;

    @SerializedName("genres")
    private List<String> mGenres;

    @SerializedName("tracks")
    private int mTracks;

    @SerializedName("albums")
    private int mAlbums;

    @SerializedName("link")
    private String mLink;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("cover")
    private Cover mCover;

    private class Cover {

        @SerializedName("small")
        private String mSmall;

        @SerializedName("big")
        private String mBig;

        public Cover(String small, String big) {
            this.mSmall = small;
            this.mBig = big;
        }

        public String getSmall() {
            return mSmall;
        }

        public String getBig() {
            return mBig;
        }
    }

    public Artist(String name, String id, List<String> genres, int tracks,
                  int albums, String link, String description, Cover cover) {
        this.mName = name;
        this.mId = id;
        this.mGenres = genres;
        this.mTracks = tracks;
        this.mAlbums = albums;
        this.mLink = link;
        this.mDescription = description;
        this.mCover = cover;
    }

    public Artist(String name, String id, String genres, int tracks, int albums,
                  String link, String description, String coverSmall, String coverBig) {
        mName = name;
        mId = id;
        mGenres = new ArrayList<>();
        mGenres.add(genres);
        mTracks = tracks;
        mAlbums = albums;
        mLink = link;
        mDescription = description;
        mCover = new Cover(coverSmall, coverBig);
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    // TODO: 21.04.2016 Изменить возврат на String
    public List<String> getGenres() {
        return mGenres;
    }

    public String getGenresAsString() {
        StringBuilder genresSb = new StringBuilder();
        for (int i = 0; i < mGenres.size(); i++) {
            genresSb.append(mGenres.get(i));
            if (i < mGenres.size() - 1) {
                genresSb.append(", ");
            }
        }
        return genresSb.toString();
    }

    public int getTracks() {
        return mTracks;
    }

    // TODO: 22.04.2016 Сделать формирование окончания (треков, трек)
    public String getFormattedTracks() {
        return Integer.toString(mTracks);
    }

    // TODO: 22.04.2016 Сделать формирование окончания (альбом, альбомов)
    public int getAlbums() {
        return mAlbums;
    }

    public String getFormattedAlbums() {
        return Integer.toString(mAlbums);
    }

    public String getLink() {
        return mLink;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getSmallCover() {
        return mCover.getSmall();
    }

    public String getBigCover() {
        return mCover.getBig();
    }
}