package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
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
import com.bunny.entertainment.yousee.models.CastListModel;
import com.bunny.entertainment.yousee.models.anime.CharacterListModel;
import com.bunny.entertainment.yousee.utils.DataHolder;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AnimeCharacterListAdapter  extends RecyclerView.Adapter<AnimeCharacterListAdapter.Holder>{
    ArrayList<CharacterListModel> characterListModelArrayList;
    Context context;

    public AnimeCharacterListAdapter(ArrayList<CharacterListModel> characterListModelArrayList, Context context) {
        this.characterListModelArrayList = characterListModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnimeCharacterListAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.anime_character_list_layout, parent, false);
        return new AnimeCharacterListAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeCharacterListAdapter.Holder holder, int position) {
        int actualPosition = holder.getAbsoluteAdapterPosition();
        holder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_left_right));
        holder.rating_layout.setVisibility(View.GONE);
        holder.animeTotalEpisodes.setVisibility(View.GONE);
        Glide.with(context)
                .load(characterListModelArrayList.get(actualPosition).getCharacterImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
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

        holder.characterName.setText(characterListModelArrayList.get(actualPosition).getCharacterName());
        holder.CharacterRole.setText(characterListModelArrayList.get(actualPosition).getCharacterRole());

    }

    @Override
    public int getItemCount() {
        return characterListModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        RelativeLayout relative_root_layout, rating_layout;
        ImageView animeThumbnail;
        ProgressBar thumbnailProgress;
        TextView characterName, CharacterRole, animeTotalEpisodes;

        public Holder(@NonNull View itemView) {
            super(itemView);
            animeThumbnail = itemView.findViewById(R.id.animeThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            rating_layout = itemView.findViewById(R.id.rating_layout);
            characterName = itemView.findViewById(R.id.animeName);
            CharacterRole = itemView.findViewById(R.id.animeRelation);
            animeTotalEpisodes = itemView.findViewById(R.id.animeTotalEpisodes);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }
}
