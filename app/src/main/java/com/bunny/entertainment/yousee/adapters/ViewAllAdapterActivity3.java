package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.AnimeActivity;
import com.bunny.entertainment.yousee.models.anime.AnimeListModel;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ViewAllAdapterActivity3 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<AnimeListModel> animeListModelArrayList;
    Context context;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_END = 2;
    boolean isLoading = false;
    boolean showEnd = false;
    SharedPreferences dramaPreferences;

    public ViewAllAdapterActivity3() {
    }

    public ViewAllAdapterActivity3(ArrayList<AnimeListModel> animeListModelArrayList, Context context) {
        this.animeListModelArrayList = animeListModelArrayList;
        this.context = context;
        dramaPreferences = context.getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
    }

    @Override
    public int getItemViewType(int position) {
        // return isLoading && position == dramaListModelArrayList.size() ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        if (isLoading && position == animeListModelArrayList.size()) {
            return VIEW_TYPE_LOADING;
        } else if (showEnd && position == animeListModelArrayList.size()) {
            return VIEW_TYPE_END;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

            int actualPosition = itemViewHolder.getAbsoluteAdapterPosition() % animeListModelArrayList.size();


            itemViewHolder.card_root_layout.startAnimation(AnimationUtils.loadAnimation(itemViewHolder.card_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));


            Glide.with(context).load(animeListModelArrayList.get(actualPosition).getAnimeThumbnail()).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.image_not_available)
                    .into(itemViewHolder.animeThumbnail);

            Glide.with(context)
                    .load(animeListModelArrayList.get(actualPosition).getAnimeThumbnail())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(new BlurTransformation(20, 2))
                    .into(itemViewHolder.ImageBackground);


            itemViewHolder.animeName.setText(animeListModelArrayList.get(actualPosition).getAnimeTitle());

            String totalEP = animeListModelArrayList.get(actualPosition).getAnimeEpisodes();
            if (totalEP.contains("TV") || totalEP.contains("Movie")) {
                totalEP = totalEP.replace("eps", "Episodes");
            } else {
                totalEP = totalEP + " Episodes";
            }
            itemViewHolder.animeTotalEpisodes.setText(totalEP);


            String rating = animeListModelArrayList.get(actualPosition).getAnimeRating();
            if (rating.equals("0")) {
                rating = "- ";
                ((ItemViewHolder) holder).animeRating.setText(rating);
            } else {
                ((ItemViewHolder) holder).animeRating.setText(rating);
            }
            String genres = "";
            if (animeListModelArrayList.get(actualPosition).getGenresList() != null && !animeListModelArrayList.get(actualPosition).getGenresList().isEmpty()) {
                for (int i = 0; i < animeListModelArrayList.get(actualPosition).getGenresList().size(); i++) {
                    genres = animeListModelArrayList.get(actualPosition).getGenresList().get(i);
                }
                itemViewHolder.animeGenres.setText(genres);
                itemViewHolder.animeGenres.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            itemViewHolder.animeDataNumber.setText(String.valueOf(animeListModelArrayList.get(actualPosition).getTotalAnime()));

            itemViewHolder.animeGenres.setVisibility(View.GONE);

            itemViewHolder.card_root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, AnimeActivity.class);
                    intent.putExtra("malId", animeListModelArrayList.get(actualPosition).getMalID());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return animeListModelArrayList.size() + (isLoading || showEnd ? 1 : 0);
    }

    public void showLoading() {
        isLoading = true;
        notifyItemInserted(animeListModelArrayList.size());
    }

    public void hideLoading() {
        isLoading = false;
        notifyItemRemoved(animeListModelArrayList.size());
    }

    public void showEnd() {
        showEnd = true;
        notifyItemInserted(animeListModelArrayList.size()+1);  ///+1 fix`s invalid issue but need checking
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
        ImageView animeThumbnail;
        ProgressBar thumbnailProgress;
        TextView animeRating, animeName, animeTotalEpisodes, animeGenres, animeDataNumber;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageBackground = itemView.findViewById(R.id.ImageBackground);
            card_root_layout = itemView.findViewById(R.id.card_root_layout);
            top_airing_button = itemView.findViewById(R.id.top_airing_button);
            animeThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            animeRating = itemView.findViewById(R.id.dramaRating);
            animeName = itemView.findViewById(R.id.dramaName);
            animeTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);
            animeGenres = itemView.findViewById(R.id.dramaCountry);
            animeDataNumber = itemView.findViewById(R.id.dramaNumber);
        }
    }
}
