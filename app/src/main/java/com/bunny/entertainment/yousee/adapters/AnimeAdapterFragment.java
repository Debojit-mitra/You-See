package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
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

import java.util.ArrayList;

public class AnimeAdapterFragment extends RecyclerView.Adapter<AnimeAdapterFragment.Holder>{

    ArrayList<AnimeListModel> animeArrayList;
    Context context;

    public AnimeAdapterFragment() {
    }

    public AnimeAdapterFragment(ArrayList<AnimeListModel> animeArrayList, Context context) {
        this.animeArrayList = animeArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnimeAdapterFragment.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_layout_2, parent, false);
        return new AnimeAdapterFragment.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeAdapterFragment.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        holder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));

        Glide.with(context).load(animeArrayList.get(actualPosition).getAnimeThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                }).error(R.drawable.image_not_available)
                .into(holder.animeThumbnail);

        holder.animeName.setText(animeArrayList.get(actualPosition).getAnimeTitle());

        String totalEP = animeArrayList.get(actualPosition).getAnimeEpisodes();
        if(totalEP.contains("TV") || totalEP.contains("Movie")){
            totalEP = totalEP.replace("eps", "Episodes");
        } else {
            totalEP = totalEP + " Episodes";
        }
        holder.animeTotalEpisodes.setText(totalEP);

        String rating = animeArrayList.get(actualPosition).getAnimeRating();
        if(rating.contains("n/a") || rating.contains("N/A")){
            rating = "- ";
            holder.animeRating.setText(rating);
        } else {
            holder.animeRating.setText(rating);
        }

        holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnimeActivity.class);
                intent.putExtra("malId", animeArrayList.get(actualPosition).getMalID());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


    }

    @Override
    public int getItemCount() {
        return animeArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        RelativeLayout relative_root_layout;
        ImageView animeThumbnail;
        ProgressBar thumbnailProgress;
        TextView animeRating, animeName, animeTotalEpisodes;

        public Holder(@NonNull View itemView) {
            super(itemView);
            animeThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            animeRating = itemView.findViewById(R.id.dramaRating);
            animeName = itemView.findViewById(R.id.dramaName);
            animeTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }
}
