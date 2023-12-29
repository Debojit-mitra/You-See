package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.AnimeActivity;
import com.bunny.entertainment.yousee.activities.ShowActivity;
import com.bunny.entertainment.yousee.models.ShowsListModel;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;

import java.util.ArrayList;

public class AnimeSearchAdapterActivity extends RecyclerView.Adapter<AnimeSearchAdapterActivity.Holder>{

    ArrayList<AnimeListModel> animeSearchArrayList;
    Context context;

    public AnimeSearchAdapterActivity() {
    }
    public AnimeSearchAdapterActivity(ArrayList<AnimeListModel> animeSearchArrayList, Context context) {
        this.animeSearchArrayList = animeSearchArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnimeSearchAdapterActivity.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_search_layout, parent, false);
        return new AnimeSearchAdapterActivity.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeSearchAdapterActivity.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        holder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_left_right_fast));
        Glide.with(context).load(animeSearchArrayList.get(actualPosition).getAnimeThumbnail())
                .error(R.drawable.image_not_available)
                .into(holder.animeThumbnail);
        holder.thumbnailProgress.setVisibility(View.GONE);
        holder.animeName.setText(animeSearchArrayList.get(actualPosition).getAnimeTitle());
        holder.animeName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        String totalEP = animeSearchArrayList.get(actualPosition).getAnimeEpisodes();
        if(totalEP.contains("TV") || totalEP.contains("Movie")){
            totalEP = totalEP.replace("eps", " Episodes");
        } else {
            totalEP = totalEP + " Episodes";
        }
        holder.animeTotalEpisodes.setText(totalEP);

        holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AnimeActivity.class);
                intent.putExtra("malId", animeSearchArrayList.get(actualPosition).getMalID());
                context.startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    @Override
    public int getItemCount() {
        return animeSearchArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        RelativeLayout top_airing_button, relative_root_layout;
        ImageView animeThumbnail;
        ProgressBar thumbnailProgress;
        TextView animeRating, animeName, animeTotalEpisodes;

        public Holder(@NonNull View itemView) {
            super(itemView);
            top_airing_button = itemView.findViewById(R.id.top_airing_button);
            animeThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            animeRating = itemView.findViewById(R.id.dramaRating);
            animeName = itemView.findViewById(R.id.dramaName);
            animeTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }
}
