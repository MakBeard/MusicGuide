package com.makbeard.musicguide.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makbeard.musicguide.FormatStringHelper;
import com.makbeard.musicguide.R;
import com.makbeard.musicguide.model.ArtistModel;

public class ArtistDetailActivity extends AppCompatActivity {

    private static final String TAG = "ArtistDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_detail);

        Intent intent = getIntent();

        String artistName = intent.getStringExtra(ArtistModel.NAME);
        String artistGenres = intent.getStringExtra(ArtistModel.GENRES);
        int artistTracks = intent.getIntExtra(ArtistModel.TRACKS, 0);
        int artistAlbums = intent.getIntExtra(ArtistModel.ALBUMS, 0);
        String artistDescription = intent.getStringExtra(ArtistModel.DESCRIPTION);
        String artistBigCover = intent.getStringExtra(ArtistModel.BIGCOVER);

        setTitle(artistName);

        TextView genresTextView = (TextView) findViewById(R.id.genres_detail_tetview);
        genresTextView.setText(artistGenres);

        TextView albumsTextView = (TextView) findViewById(R.id.albums_detail_textview);
        albumsTextView.setText(FormatStringHelper.getFormattedAlbums(artistAlbums));

        TextView tracksTextView = (TextView) findViewById(R.id.tracks_detail_textview);
        tracksTextView.setText(FormatStringHelper.getFormattedTracks(artistTracks));


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
