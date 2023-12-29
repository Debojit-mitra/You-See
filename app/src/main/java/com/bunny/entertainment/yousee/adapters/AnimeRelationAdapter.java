package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.bunny.entertainment.yousee.models.anime.AnimeRelationsModel;

import java.util.ArrayList;

public class AnimeRelationAdapter extends RecyclerView.Adapter<AnimeRelationAdapter.Holder>{

    ArrayList<AnimeRelationsModel> animeRelationsModelArrayList;
    Context context;
    public AnimeRelationAdapter() {
    }
    public AnimeRelationAdapter(ArrayList<AnimeRelationsModel> animeRelationsModelArrayList, Context context) {
        this.animeRelationsModelArrayList = animeRelationsModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnimeRelationAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.anime_relation_layout, parent, false);
        return new AnimeRelationAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeRelationAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        Glide.with(context).load(animeRelationsModelArrayList.get(actualPosition).getAnimeImage()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                }).error(R.drawable.image_not_available)
                .into(holder.animeThumbnail);

        holder.animeName.setText(animeRelationsModelArrayList.get(actualPosition).getAnimeTitle());

        String totalEP = animeRelationsModelArrayList.get(actualPosition).getAnimeEpisodes();
        totalEP = totalEP + " Episodes";

        holder.animeTotalEpisodes.setText(totalEP);
        holder.animeRelation.setText(animeRelationsModelArrayList.get(actualPosition).getAnimeRelationType().toUpperCase());

        String rating = animeRelationsModelArrayList.get(actualPosition).getAnimeRating();
        if(rating != null) {
            if (rating.contains("n/a") || rating.contains("N/A") || rating.contains("?")) {
                rating = "- ";
                holder.animeRating.setText(rating);
            } else {
                holder.animeRating.setText(rating);
            }
        }

        holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnimeActivity.class);
                intent.putExtra("malId", animeRelationsModelArrayList.get(actualPosition).getMyAnimeListID());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return animeRelationsModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        RelativeLayout relative_root_layout;
        ImageView animeThumbnail;
        ProgressBar thumbnailProgress;
        TextView animeRating, animeName, animeRelation, animeTotalEpisodes;

        public Holder(@NonNull View itemView) {
            super(itemView);
            animeThumbnail = itemView.findViewById(R.id.animeThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            animeRating = itemView.findViewById(R.id.animeRating);
            animeName = itemView.findViewById(R.id.animeName);
            animeRelation = itemView.findViewById(R.id.animeRelation);
            animeTotalEpisodes = itemView.findViewById(R.id.animeTotalEpisodes);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }
}
