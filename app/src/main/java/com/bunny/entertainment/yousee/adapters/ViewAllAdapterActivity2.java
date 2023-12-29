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
import com.bunny.entertainment.yousee.activities.GenresActivity;
import com.bunny.entertainment.yousee.activities.ShowActivity;
import com.bunny.entertainment.yousee.activities.ShowsActivity_2;
import com.bunny.entertainment.yousee.models.DramaListModel;
import com.bunny.entertainment.yousee.models.ShowsListModel;
import com.bunny.entertainment.yousee.utils.Constants;

import java.util.ArrayList;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ViewAllAdapterActivity2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<ShowsListModel> showsListModelArrayList;
    Context context;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_END = 2;
    boolean isLoading = false;
    boolean showEnd = false;
    SharedPreferences dramaPreferences;

    public ViewAllAdapterActivity2() {
    }

    public ViewAllAdapterActivity2(ArrayList<ShowsListModel> showsListModelArrayList, Context context) {
        this.showsListModelArrayList = showsListModelArrayList;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
       // return isLoading && position == dramaListModelArrayList.size() ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        if (isLoading && position == showsListModelArrayList.size()) {
            return VIEW_TYPE_LOADING;
        } else if (showEnd && position == showsListModelArrayList.size()) {
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

            int actualPosition = itemViewHolder.getAbsoluteAdapterPosition() % showsListModelArrayList.size();;

            itemViewHolder.card_root_layout.startAnimation(AnimationUtils.loadAnimation(itemViewHolder.card_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));

            Glide.with(context).load(showsListModelArrayList.get(actualPosition).getShowThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
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
                    .load(showsListModelArrayList.get(actualPosition).getShowThumbnail())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new BlurTransformation(20, 2))
                    .into(itemViewHolder.ImageBackground);


            itemViewHolder.dramaName.setText(showsListModelArrayList.get(actualPosition).getShowName());

            itemViewHolder.dramaCountry.setVisibility(View.GONE);

            String lastEpWatched = dramaPreferences.getString(showsListModelArrayList.get(actualPosition).getShowId()+"lastEpWatched", null );
            if(lastEpWatched != null) {
                lastEpWatched = lastEpWatched.substring(0, lastEpWatched.indexOf(","));
                String outOff = lastEpWatched+" watched";
                itemViewHolder.dramaTotalEpisodes.setText(outOff);
            }else {
                itemViewHolder.dramaTotalEpisodes.setVisibility(View.GONE);
            }

            /*else {
                String totalEpisodes = showsListModelArrayList.get(actualPosition).getTotalEpisodes();
                if(showsListModelArrayList.get(actualPosition).getCountrySpecifiedDrama().toLowerCase(Locale.ENGLISH).contains("movie") || showsListModelArrayList.get(actualPosition).getCountrySpecifiedDrama().toLowerCase(Locale.ENGLISH).contains("movies")) {
                    itemViewHolder.dramaTotalEpisodes.setVisibility(View.GONE);
                }else {
                    if(totalEpisodes == null || totalEpisodes.trim().equals("")){
                        totalEpisodes = "-  Episodes";
                    }else {
                        totalEpisodes = totalEpisodes+" Episodes";
                    }
                    itemViewHolder.dramaTotalEpisodes.setText(totalEpisodes);
                }
            }*/

            
            if (showsListModelArrayList.get(actualPosition).getShowRating().isEmpty()) {
                itemViewHolder.dramaRating.setText("0.0");
            } else {
                itemViewHolder.dramaRating.setText(showsListModelArrayList.get(actualPosition).getShowRating());
            }
            //itemViewHolder.dramaCountry.setText(showsListModelArrayList.get(actualPosition).getCountrySpecifiedDrama());
            itemViewHolder.dramaNumber.setText(showsListModelArrayList.get(actualPosition).getDataNumber());

            itemViewHolder.card_root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mediaType = showsListModelArrayList.get(actualPosition).getMediaType();
                    if(mediaType.contains("tv") || mediaType.contains("movie")){
                        Intent intent = new Intent(context, ShowsActivity_2.class);
                        intent.putExtra("dramaId", showsListModelArrayList.get(actualPosition).getShowId());
                        intent.putExtra("mediaType", mediaType);
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        Intent intent = new Intent(context, ShowActivity.class);
                        intent.putExtra("dramaId", showsListModelArrayList.get(actualPosition).getShowId());
                        context.startActivity(intent);
                        ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        return showsListModelArrayList.size() + (isLoading || showEnd ? 1 : 0);
    }

    public void showLoading() {
        isLoading = true;
        notifyItemInserted(showsListModelArrayList.size());
    }

    public void hideLoading() {
        isLoading = false;
        notifyItemRemoved(showsListModelArrayList.size());
    }
    public void showEnd() {
        showEnd = true;
        notifyItemInserted(showsListModelArrayList.size()+1); // added +1 here because of inconsistency and crash
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
