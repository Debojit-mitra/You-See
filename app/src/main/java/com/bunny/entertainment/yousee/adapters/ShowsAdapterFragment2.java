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
import com.bunny.entertainment.yousee.activities.ShowActivity;
import com.bunny.entertainment.yousee.activities.ShowsActivity_2;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.ShowsListModel;

import java.util.ArrayList;

public class ShowsAdapterFragment2 extends RecyclerView.Adapter<ShowsAdapterFragment2.Holder> {

    ArrayList<ShowsListModel> viewAllShowsArrayList;
    Context context;
    public ShowsAdapterFragment2(ArrayList<ShowsListModel> viewAllShowsArrayList, Context context) {
        this.viewAllShowsArrayList = viewAllShowsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ShowsAdapterFragment2.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_layout_2, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowsAdapterFragment2.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition(); //% viewAllShowsArrayList.size();

        holder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));


        Glide.with(context).load(viewAllShowsArrayList.get(actualPosition).getShowThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
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
                .into(holder.dramaThumbnail);

        holder.dramaName.setText(viewAllShowsArrayList.get(actualPosition).getShowName());

        holder.dramaTotalEpisodes.setVisibility(View.GONE);


        if (viewAllShowsArrayList.get(actualPosition).getShowRating().isEmpty()) {
            holder.dramaRating.setText("0.0");
        } else {
            holder.dramaRating.setText(viewAllShowsArrayList.get(actualPosition).getShowRating());
        }

            holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mediaType = viewAllShowsArrayList.get(actualPosition).getMediaType();
                    if(mediaType.contains("tv") || mediaType.contains("movie")){
                        Intent intent = new Intent(context, ShowsActivity_2.class);
                        intent.putExtra("dramaId", viewAllShowsArrayList.get(actualPosition).getShowId());
                        intent.putExtra("mediaType", mediaType);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } /*else {
                        Intent intent = new Intent(context, ShowActivity.class);
                        intent.putExtra("dramaId", viewAllShowsArrayList.get(actualPosition).getShowId());
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }*/
                }
            });

    }

    @Override
    public int getItemCount() {
        return viewAllShowsArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        RelativeLayout top_airing_button, relative_root_layout;
        ImageView dramaThumbnail;
        ProgressBar thumbnailProgress;
        TextView dramaRating, dramaName, dramaTotalEpisodes, dramaCountry;

        public Holder(@NonNull View itemView) {
            super(itemView);

            top_airing_button = itemView.findViewById(R.id.top_airing_button);
            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            dramaRating = itemView.findViewById(R.id.dramaRating);
            dramaName = itemView.findViewById(R.id.dramaName);
            dramaTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }
    public boolean containsOnlyNumbers(String input) {
        return input.matches("\\d+");
    }
}
