package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import androidx.fragment.app.FragmentActivity;
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

public class ContinueWatchingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<DramaListModel> continueWatchingDramaDetailsArrayList;
    SharedPreferences dramaPreferences, animePreferences;
    String showMediaType;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    boolean isLoading = false;
    private OnDataLoadedListener onDataLoadedListener;
    public interface OnDataLoadedListener {
        void onDataLoaded();
    }
    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        this.onDataLoadedListener = listener;
    }
    public ContinueWatchingAdapter(ArrayList<DramaListModel> continueWatchingDramaDetailsArrayList, Context context) {
        this.context = context;
        this.continueWatchingDramaDetailsArrayList = continueWatchingDramaDetailsArrayList;
        dramaPreferences = context.getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        animePreferences = context.getSharedPreferences("animePreferences", Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading && position == continueWatchingDramaDetailsArrayList.size()) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.drama_layout_2, parent, false);
            return new ItemViewHolder(itemView);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View loadingView = LayoutInflater.from(context).inflate(R.layout.drama_loading_vertical, parent, false);
            return new LoadingViewHolder(loadingView);
        }
        throw new IllegalArgumentException("Invalid view type");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemViewHolder) {

            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            int actualPosition = holder.getAbsoluteAdapterPosition();

            itemViewHolder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(itemViewHolder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_left_right));

            itemViewHolder.rating_layout.setVisibility(View.GONE);
            Glide.with(context).load(continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    itemViewHolder.thumbnailProgress.setVisibility(View.GONE);
                    return false;
                }
            }).error(R.drawable.image_not_available).into(itemViewHolder.dramaThumbnail);
            itemViewHolder.dramaName.setText(continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaName());

            showMediaType = continueWatchingDramaDetailsArrayList.get(actualPosition).getTotalResults();
            String lastEpWatched = "";
            if(showMediaType.contains("Anime")){
                lastEpWatched = animePreferences.getString(continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaId()+"lastEpWatched", null );
            } else {
                lastEpWatched = dramaPreferences.getString(continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaId()+"lastEpWatched", null );
            }

            if(lastEpWatched != null) {
                lastEpWatched = lastEpWatched.substring(0, lastEpWatched.indexOf(","));
                String totalEp = continueWatchingDramaDetailsArrayList.get(actualPosition).getTotalEpisodes();
                if (totalEp.toLowerCase().contains("unknown")){
                    totalEp = "?";
                }
                String outOff = lastEpWatched+"/"+totalEp+" watched";
                itemViewHolder.dramaTotalEpisodes.setText(outOff);
            }else {
                itemViewHolder.dramaTotalEpisodes.setVisibility(View.GONE);
            }
            itemViewHolder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String  showMediaType = continueWatchingDramaDetailsArrayList.get(actualPosition).getTotalResults();
                    if(showMediaType.contains("tv") || showMediaType.contains("movie")){
                        Intent intent = new Intent(context, ShowsActivity_2.class);
                        intent.putExtra("dramaId", continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaId());
                        intent.putExtra("mediaType", showMediaType);
                        System.out.println("hereInContinueWatchingAdapter    " + continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaId() +"   "+ showMediaType);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if (showMediaType.contains("Anime")){
                        Intent intent = new Intent(context, AnimeActivity.class);
                        intent.putExtra("malId", continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaId());
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else if(showMediaType.contains("Drama")){
                        Intent intent = new Intent(context, ShowActivity.class);
                        intent.putExtra("dramaId", continueWatchingDramaDetailsArrayList.get(actualPosition).getDramaId());
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });
            if (position == getItemCount() - 1 && onDataLoadedListener != null) {
                onDataLoadedListener.onDataLoaded();
            }


        } else if (holder instanceof ViewAllAdapterActivity.LoadingViewHolder) {
            ViewAllAdapterActivity.LoadingViewHolder loadingViewHolder = (ViewAllAdapterActivity.LoadingViewHolder) holder;
            //loadingViewHolder.top_airing_ProgressBar.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return continueWatchingDramaDetailsArrayList.size() + (isLoading ? 1 : 0);
    }

    public void showLoading() {
        isLoading = true;
        notifyItemInserted(continueWatchingDramaDetailsArrayList.size());
    }
    public void hideLoading() {
        isLoading = false;
        notifyItemRemoved(continueWatchingDramaDetailsArrayList.size());
    }

    public boolean containsOnlyNumbers(String input) {
        return input.matches("\\d+");
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relative_root_layout, rating_layout;
        ImageView dramaThumbnail;
        ProgressBar thumbnailProgress;
        TextView dramaName, dramaTotalEpisodes;
        public ItemViewHolder(View itemView) {
            super(itemView);
            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            rating_layout = itemView.findViewById(R.id.rating_layout);
            dramaName = itemView.findViewById(R.id.dramaName);
            dramaTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar top_airing_ProgressBar;
        public LoadingViewHolder(View loadingView) {
            super(loadingView);
            top_airing_ProgressBar = itemView.findViewById(R.id.top_airing_ProgressBar);
        }
    }
}
