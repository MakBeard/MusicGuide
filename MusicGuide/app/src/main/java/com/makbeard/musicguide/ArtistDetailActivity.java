package com.makbeard.musicguide;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makbeard.musicguide.model.Artist;

public class ArtistDetailActivity extends AppCompatActivity {

    private static final String TAG = "ArtistDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Intent intent = getIntent();

        String artistName = intent.getStringExtra(Artist.NAME);
        String artistGenres = intent.getStringExtra(Artist.GENRES);
        String artistTracks = intent.getStringExtra(Artist.TRACKS);
        String artistAlbums = intent.getStringExtra(Artist.ALBUMS);
        String artistDescription = intent.getStringExtra(Artist.DESCRIPTION);
        String artistBigCover = intent.getStringExtra(Artist.BIGCOVER);

        setTitle(artistName);

        TextView genresTextView = (TextView) findViewById(R.id.genres_detail_tetview);
        genresTextView.setText(artistGenres);

        // TODO: 22.04.2016  Убрать строку " альбомов"
        TextView albumsTextView = (TextView) findViewById(R.id.albums_detail_textview);
        albumsTextView.setText(artistAlbums + " альбомов, ");

        // TODO: 22.04.2016 Убрать строку " треков, "
        TextView tracksTextView = (TextView) findViewById(R.id.tracks_detail_textview);
        tracksTextView.setText(artistTracks + " треков");

        TextView descriptionTextView = (TextView) findViewById(R.id.description_detail_textview);
        descriptionTextView.setText(artistDescription);

        ImageView bigCoverImageView = (ImageView) findViewById(R.id.bigcover_imageview);

        // TODO: 22.04.2016 Изменить размер изображения
        Glide
                .with(this)
                .load(artistBigCover)
                .placeholder(R.drawable.placeholder_224_1000)
                .error(R.drawable.placeholder_224_1000)
                .into(bigCoverImageView);
    }
}
