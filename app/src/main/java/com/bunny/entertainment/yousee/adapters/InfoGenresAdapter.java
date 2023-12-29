package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.utils.Constants;
import com.bunny.entertainment.yousee.utils.DataHolder;

import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class InfoGenresAdapter extends RecyclerView.Adapter<InfoGenresAdapter.Holder> {

    ArrayList<String> genresArrayList;
    Context context;
    String fromWhere;

    public InfoGenresAdapter() {
    }

    public InfoGenresAdapter(ArrayList<String> genresArrayList, Context context, String fromWhere) {
        this.genresArrayList = genresArrayList;
        this.context = context;
        this.fromWhere = fromWhere;
    }

    @NonNull
    @Override
    public InfoGenresAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_genres_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoGenresAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        Random random = new Random();
        int randomNumber = random.nextInt(20);

        if (genresArrayList != null) {
            if (fromWhere.toLowerCase().contains("drama")){
                if (DataHolder.getTopAiringDramaListModelArrayList() != null) {
                    Glide.with(context)
                            .load(DataHolder.getTopAiringDramaListModelArrayList().get(randomNumber).getDramaThumbnail())
                            .transform(new BlurTransformation(8, 2))
                            .into(holder.dramaThumbnail);
                } else {
                    setDefaultImage(holder);
                }
            } else if (fromWhere.toLowerCase().contains("shows")) {
                if (DataHolder.getTopRatedShowsListModelArrayList() != null) {
                    Glide.with(context)
                            .load(DataHolder.getTopRatedShowsListModelArrayList().get(randomNumber).getShowThumbnail())
                            .transform(new BlurTransformation(8, 2))
                            .into(holder.dramaThumbnail);
                } else {
                    setDefaultImage(holder);
                }
            } else if (fromWhere.toLowerCase().contains("anime")) {
                if (DataHolder.getTopAiringAnimeListModelArrayList() != null) {
                    Glide.with(context)
                            .load(DataHolder.getTopAiringAnimeListModelArrayList().get(randomNumber).getAnimeThumbnail())
                            .transform(new BlurTransformation(8, 2))
                            .into(holder.dramaThumbnail);
                } else {
                    setDefaultImage(holder);
                }
            }

            holder.textView_genres.setText(genresArrayList.get(actualPosition));
    }


    }

    private void setDefaultImage(Holder holder) {
        Glide.with(context)
                .load(Constants.RANDOM_WALLPAPER_300DP)
                .transform(new BlurTransformation(8, 2))
                .into(holder.dramaThumbnail);
    }

    @Override
    public int getItemCount() {
        return genresArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView dramaThumbnail;
        TextView textView_genres;

        public Holder(@NonNull View itemView) {
            super(itemView);

            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            textView_genres = itemView.findViewById(R.id.textView_genres);
        }
    }
}
