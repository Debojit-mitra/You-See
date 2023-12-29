package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.bunny.entertainment.yousee.activities.ShowActivity;
import com.bunny.entertainment.yousee.activities.ShowsActivity_2;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class TopAiringAdapterFragment extends RecyclerView.Adapter<TopAiringAdapterFragment.Holder> {

    ArrayList<DramaListModel> topAiringArrayList;
    Context context;

    public TopAiringAdapterFragment() {
    }

    public TopAiringAdapterFragment(ArrayList<DramaListModel> topAiringArrayList, Context context) {
        this.topAiringArrayList = topAiringArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TopAiringAdapterFragment.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_airing_layout, parent, false);
        return new TopAiringAdapterFragment.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAiringAdapterFragment.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition() % topAiringArrayList.size(); //this percentage is necessary to avoid crash due to unlimited swipes

        Glide.with(context).load(topAiringArrayList.get(actualPosition).getDramaThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.thumbnailProgress.setVisibility(View.GONE);
                return false;
            }})
                .error(R.drawable.image_not_available)
                .into(holder.dramaThumbnail);

        Glide.with(context)
                .load(topAiringArrayList.get(actualPosition).getDramaThumbnail())
                .transform(new BlurTransformation( 20, 2))
                .into(holder.animatedImageBackground);

        holder.dramaName.setText(topAiringArrayList.get(actualPosition).getDramaName());
        String totalEpisodes = topAiringArrayList.get(actualPosition).getTotalEpisodes()+" Episodes";
        if(topAiringArrayList.get(actualPosition).getTotalEpisodes().isEmpty()){
            holder.dramaTotalEpisodes.setVisibility(View.GONE);
        }
        holder.dramaTotalEpisodes.setText(totalEpisodes);
        holder.dramaRating.setText(topAiringArrayList.get(actualPosition).getDramaRating());
        holder.dramaCountry.setText(topAiringArrayList.get(actualPosition).getCountrySpecifiedDrama());

        String mediaType = topAiringArrayList.get(actualPosition).getTotalResults();
        if(mediaType != null && mediaType.contains("tv") || mediaType != null && mediaType.contains("movie")) {
            holder.releasing_drama.setVisibility(View.GONE);
        }


        holder.top_airing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaType != null && mediaType.contains("tv") || mediaType != null && mediaType.contains("movie")){
                    Intent intent = new Intent(context, ShowsActivity_2.class);
                    intent.putExtra("dramaId", topAiringArrayList.get(actualPosition).getDramaId());
                    intent.putExtra("mediaType", mediaType);
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else {
                    Intent intent = new Intent(context, ShowActivity.class);
                    intent.putExtra("dramaId", topAiringArrayList.get(actualPosition).getDramaId());
                    context.startActivity(intent);
                    ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
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
        ImageView dramaThumbnail;
        ProgressBar thumbnailProgress;
        TextView dramaRating, dramaName, dramaTotalEpisodes, dramaCountry, releasing_drama;

        public Holder(@NonNull View itemView) {
            super(itemView);

            animatedImageBackground = itemView.findViewById(R.id.animatedImageBackground);
            top_airing_button = itemView.findViewById(R.id.top_airing_button);
            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            dramaRating = itemView.findViewById(R.id.dramaRating);
            dramaName = itemView.findViewById(R.id.dramaName);
            dramaTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            dramaCountry = itemView.findViewById(R.id.dramaCountry);
            releasing_drama = itemView.findViewById(R.id.releasing_drama);


        }
    }
}
