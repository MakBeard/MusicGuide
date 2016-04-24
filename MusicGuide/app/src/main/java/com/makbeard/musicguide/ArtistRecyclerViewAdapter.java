package com.makbeard.musicguide;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makbeard.musicguide.model.ArtistModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс описывающий адаптера для RecyclerView
 */
public class ArtistRecyclerViewAdapter extends RecyclerView.Adapter<ArtistRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RVAdapter" ;
    private List<ArtistModel> mArtistModelList = new ArrayList<>();
    private Listener mListener;
    private Context mContext;

    public ArtistRecyclerViewAdapter(Context context, List<ArtistModel> artistModelList) {
        mContext = context;
        mArtistModelList.addAll(artistModelList);
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
        //holder.mNameTextView.setText(mArtistModelList.get(position).getName());

        TextView nameTextView = (TextView) cardView.findViewById(R.id.name_textview);
        nameTextView.setText(mArtistModelList.get(position).getName());

        TextView genresTextView = (TextView) cardView.findViewById(R.id.genres_textview);
        genresTextView.setText(mArtistModelList.get(position).getGenresAsString());

        TextView albumsTextView = (TextView) cardView.findViewById(R.id.albums_textview);
        albumsTextView.setText(FormatStringHelper.getFormattedAlbums(
                mArtistModelList.get(position).getAlbums()) + ", ");

        TextView tracksTextView = (TextView) cardView.findViewById(R.id.tracks_textview);
        tracksTextView.setText(FormatStringHelper.getFormattedTracks(
                        mArtistModelList.get(position).getTracks()));

        ImageView smallCoverImageView =
                (ImageView) cardView.findViewById(R.id.smallcover_imageview);

        //Загружаем и кэшируем изображение помощью Glide.
        Glide
                .with(mContext)
                .load(mArtistModelList.get(position).getSmallCover())
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
        return mArtistModelList.size();
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

    /**
     * Метод заменяет данные в адаптере на переданные
     * @param list данные для размещения в адаптере
     */
    public void updateAll(List<ArtistModel> list) {
        mArtistModelList.clear();
        mArtistModelList.addAll(list);
        notifyDataSetChanged();
    }

    public void setFilter(List<ArtistModel> list) {
        mArtistModelList = new ArrayList<>();
        mArtistModelList.addAll(list);
        notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mArtistModelList.get(position);
    }

    public List<ArtistModel> getAll() {
        return mArtistModelList;
    }


}
