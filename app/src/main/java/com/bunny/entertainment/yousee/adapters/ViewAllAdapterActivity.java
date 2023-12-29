package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.cardview.widget.CardView;
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
import com.bunny.entertainment.yousee.activities.ShowsActivity_2;
import com.bunny.entertainment.yousee.models.DramaListModel;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ViewAllAdapterActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<DramaListModel> dramaListModelArrayList;
    Context context;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_END = 2;
    boolean isLoading = false;
    boolean showEnd = false;
    SharedPreferences dramaPreferences;

    public ViewAllAdapterActivity() {
    }

    public ViewAllAdapterActivity(ArrayList<DramaListModel> dramaListModelArrayList, Context context) {
        this.dramaListModelArrayList = dramaListModelArrayList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
       // return isLoading && position == dramaListModelArrayList.size() ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        if (isLoading && position == dramaListModelArrayList.size()) {
            return VIEW_TYPE_LOADING;
        } else if (showEnd && position == dramaListModelArrayList.size()) {
            return VIEW_TYPE_END;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        dramaPreferences = context.getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.drama_layout, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View loadingView = LayoutInflater.from(context).inflate(R.layout.drama_loading, parent, false);
            return new LoadingViewHolder(loadingView);
        } else if (viewType == VIEW_TYPE_END) {
            View endView = LayoutInflater.from(context).inflate(R.layout.drama_end, parent, false);
            return new EndViewHolder(endView);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            int actualPosition = itemViewHolder.getAbsoluteAdapterPosition() % dramaListModelArrayList.size();;

            itemViewHolder.card_root_layout.startAnimation(AnimationUtils.loadAnimation(itemViewHolder.card_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));

            Glide.with(context).load(dramaListModelArrayList.get(actualPosition).getDramaThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            itemViewHolder.thumbnailProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .error(R.drawable.image_not_available)
                    .into(itemViewHolder.dramaThumbnail);

            Glide.with(context)
                    .load(dramaListModelArrayList.get(actualPosition).getDramaThumbnail())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new BlurTransformation(20, 2))
                    .into(itemViewHolder.ImageBackground);


            itemViewHolder.dramaName.setText(dramaListModelArrayList.get(actualPosition).getDramaName());


            String lastEpWatched = dramaPreferences.getString(dramaListModelArrayList.get(actualPosition).getDramaId()+"lastEpWatched", null );
            if(lastEpWatched != null) {
                lastEpWatched = lastEpWatched.substring(0, lastEpWatched.indexOf(","));
                String outOff = lastEpWatched+"/"+dramaListModelArrayList.get(actualPosition).getTotalEpisodes()+" watched";
                itemViewHolder.dramaTotalEpisodes.setText(outOff);
            }else {
                String totalEpisodes = dramaListModelArrayList.get(actualPosition).getTotalEpisodes();
                if(dramaListModelArrayList.get(actualPosition).getCountrySpecifiedDrama().toLowerCase(Locale.ENGLISH).contains("movie") || dramaListModelArrayList.get(actualPosition).getCountrySpecifiedDrama().toLowerCase(Locale.ENGLISH).contains("movies")) {
                    itemViewHolder.dramaTotalEpisodes.setVisibility(View.GONE);
                }else {
                    if(totalEpisodes == null || totalEpisodes.trim().equals("") || totalEpisodes.equalsIgnoreCase("unknown")){
                        totalEpisodes = "-  Episodes";
                    }else {
                        totalEpisodes = totalEpisodes+" Episodes";
                    }
                    itemViewHolder.dramaTotalEpisodes.setText(totalEpisodes);
                }
            }

            
            if (dramaListModelArrayList.get(actualPosition).getDramaRating().isEmpty()) {
                itemViewHolder.dramaRating.setText("0.0");
            } else {
                itemViewHolder.dramaRating.setText(dramaListModelArrayList.get(actualPosition).getDramaRating());
            }
            itemViewHolder.dramaCountry.setText(dramaListModelArrayList.get(actualPosition).getCountrySpecifiedDrama());
            itemViewHolder.dramaNumber.setText(dramaListModelArrayList.get(actualPosition).getDataNumber());

            itemViewHolder.card_root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String showMediaType = dramaListModelArrayList.get(actualPosition).getTotalResults();
                    if(showMediaType.contains("tv") || showMediaType.contains("movie")){
                        Intent intent = new Intent(context, ShowsActivity_2.class);
                        intent.putExtra("dramaId", dramaListModelArrayList.get(actualPosition).getDramaId());
                        intent.putExtra("mediaType", showMediaType);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }else if (showMediaType.contains("Anime")){
                        Intent intent = new Intent(context, AnimeActivity.class);
                        intent.putExtra("malId", dramaListModelArrayList.get(actualPosition).getDramaId());
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if(showMediaType.contains("Drama")){
                        Intent intent = new Intent(context, ShowActivity.class);
                        intent.putExtra("dramaId", dramaListModelArrayList.get(actualPosition).getDramaId());
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });


        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            //loadingViewHolder.top_airing_ProgressBar.setVisibility(View.VISIBLE);
        } else if (holder instanceof EndViewHolder){
            EndViewHolder endViewHolder = (EndViewHolder) holder;
        }

    }

    @Override
    public int getItemCount() {
        return dramaListModelArrayList.size() + (isLoading || showEnd ? 1 : 0);
    }

    public void showLoading() {
        isLoading = true;
        notifyItemInserted(dramaListModelArrayList.size());
    }
    public void hideLoading() {
        isLoading = false;
        notifyItemRemoved(dramaListModelArrayList.size());
    }
    public void showEnd() {
        showEnd = true;
        notifyItemInserted(dramaListModelArrayList.size());
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar top_airing_ProgressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            top_airing_ProgressBar = itemView.findViewById(R.id.top_airing_ProgressBar);
        }
    }

    public class EndViewHolder extends RecyclerView.ViewHolder {
        TextView end_of_list;
        public EndViewHolder(@NonNull View itemView) {
            super(itemView);
            end_of_list = itemView.findViewById(R.id.end_of_list);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ImageBackground;
        CardView card_root_layout;
        RelativeLayout top_airing_button;
        ImageView dramaThumbnail;
        ProgressBar thumbnailProgress;
        TextView dramaRating, dramaName, dramaTotalEpisodes, dramaCountry, dramaNumber;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageBackground = itemView.findViewById(R.id.ImageBackground);
            card_root_layout = itemView.findViewById(R.id.card_root_layout);
            top_airing_button = itemView.findViewById(R.id.top_airing_button);
            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            dramaRating = itemView.findViewById(R.id.dramaRating);
            dramaName = itemView.findViewById(R.id.dramaName);
            dramaTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            dramaCountry = itemView.findViewById(R.id.dramaCountry);
            dramaNumber = itemView.findViewById(R.id.dramaNumber);
        }
    }
}
