package com.makbeard.musicguide;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makbeard.musicguide.model.Artist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry on 21.04.2016.
 */
public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RVAdapter" ;
    private List<Artist> mArtistList = new ArrayList<>();
    private Listener mListener;
    private Context mContext;

    public ArtistRecyclerViewAdapter(Context context, List<Artist> artistList) {
        mContext = context;
        mArtistList.addAll(artistList);
    }

    public interface Listener {
        void onClick(int position);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detail_artist_cardview, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.mCardView;
        //holder.mNameTextView.setText(mArtistList.get(position).getName());

        TextView nameTextView = (TextView) cardView.findViewById(R.id.name_textview);
        nameTextView.setText(mArtistList.get(position).getName());

        TextView genresTextView = (TextView) cardView.findViewById(R.id.genres_textview);
        genresTextView.setText(mArtistList.get(position).getGenresAsString());

        TextView tracksTextView = (TextView) cardView.findViewById(R.id.tracks_textview);
        tracksTextView.setText(mArtistList.get(position).getTracks() + " альбомов, ");

        TextView albumsTextView = (TextView) cardView.findViewById(R.id.albums_textview);
        albumsTextView.setText(mArtistList.get(position).getAlbums() + " песен");

        ImageView smallCoverImageView =
                (ImageView) cardView.findViewById(R.id.smallcover_imageview);

        Glide
                .with(mContext)
                .load(mArtistList.get(position).getSmallCover())
                .placeholder(R.drawable.placehoder_224_300)
                .into(smallCoverImageView);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView mCardView;
        TextView mNameTextView;
        public ViewHolder(CardView cardView) {
            super(cardView);
            mCardView = cardView;
            mNameTextView = (TextView) cardView.findViewById(R.id.name_textview);
        }
    }
}
