package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.AnimeActivity;
import com.bunny.entertainment.yousee.activities.ShowActivity;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class TopAiringAnimeAdapterFragment extends RecyclerView.Adapter<TopAiringAnimeAdapterFragment.Holder> {

    ArrayList<AnimeListModel> topAiringAnimeArrayList;
    Context context;
    //String releasing = "yes";

    public TopAiringAnimeAdapterFragment() {
    }

    public TopAiringAnimeAdapterFragment(ArrayList<AnimeListModel> topAiringAnimeArrayList, Context context) {
        this.topAiringAnimeArrayList = topAiringAnimeArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TopAiringAnimeAdapterFragment.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_airing_layout, parent, false);
        return new TopAiringAnimeAdapterFragment.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAiringAnimeAdapterFragment.Holder holder, int position) {

        int itemPosition = holder.getAbsoluteAdapterPosition() % topAiringAnimeArrayList.size(); //this percentage is necessary to avoid crash due to unlimited swipes

        Glide.with(context).load(topAiringAnimeArrayList.get(itemPosition).getAnimeThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.image_not_available)
                .into(holder.animeThumbnail);

        Glide.with(context)
                .load(topAiringAnimeArrayList.get(itemPosition).getAnimeThumbnail())
                .transform(new BlurTransformation(20, 2))
                .into(holder.animatedImageBackground);

        holder.animeName.setText(topAiringAnimeArrayList.get(itemPosition).getAnimeTitle());

        String episodes = topAiringAnimeArrayList.get(itemPosition).getAnimeEpisodes();
        if (episodes.equals("?")) {
            episodes = "-" + " Episodes";
        } else {
            episodes = episodes + " Episodes";
        }
        holder.animeTotalEpisodes.setText(episodes);

        String rating = topAiringAnimeArrayList.get(itemPosition).getAnimeRating();
        if (rating.equals("0")) {
            rating = "- ";
            holder.animeRating.setText(rating);
        } else {
            holder.animeRating.setText(rating);
        }

        holder.releasing_anime.setVisibility(View.GONE);

        String genres = "";
        if (topAiringAnimeArrayList.get(itemPosition).getGenresList() != null && !topAiringAnimeArrayList.get(itemPosition).getGenresList().isEmpty()) {
            for (int i = 0; i < topAiringAnimeArrayList.get(itemPosition).getGenresList().size(); i++) {
                genres = topAiringAnimeArrayList.get(itemPosition).getGenresList().get(i);
            }
            holder.animeGenres.setText(genres);
            holder.animeGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        holder.top_airing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnimeActivity.class);
                intent.putExtra("malId", topAiringAnimeArrayList.get(itemPosition).getMalID());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; //to make it horizontal recyclerview swipe unlimited times
    }

    public class Holder extends RecyclerView.ViewHolder {

        KenBurnsView animatedImageBackground;
        RelativeLayout top_airing_button;
        ImageView animeThumbnail;
        ProgressBar thumbnailProgress;
        TextView animeRating, animeName, animeTotalEpisodes, animeGenres, releasing_anime;

        public Holder(@NonNull View itemView) {
            super(itemView);

            animatedImageBackground = itemView.findViewById(R.id.animatedImageBackground);
            top_airing_button = itemView.findViewById(R.id.top_airing_button);
            animeThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            animeRating = itemView.findViewById(R.id.dramaRating);
            animeName = itemView.findViewById(R.id.dramaName);
            animeTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            animeGenres = itemView.findViewById(R.id.dramaCountry);
            releasing_anime = itemView.findViewById(R.id.releasing_drama);

        }
    }

    public void setData(ArrayList<AnimeListModel> topAiringAnimeArrayList) {
        this.topAiringAnimeArrayList = topAiringAnimeArrayList;
    }
}
