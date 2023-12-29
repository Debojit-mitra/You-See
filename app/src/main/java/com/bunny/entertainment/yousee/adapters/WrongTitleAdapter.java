package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.DramaMovieFirstModel;

import java.util.ArrayList;

public class WrongTitleAdapter extends RecyclerView.Adapter<WrongTitleAdapter.Holder> {

    public interface OnItemClickListener {
        void onItemClick(int position); // You can customize the data type
    }

    private OnItemClickListener listener;

    ArrayList<DramaMovieFirstModel> dramaMovieFirstModelArrayList;
    Context context;

    public WrongTitleAdapter(ArrayList<DramaMovieFirstModel> dramaMovieFirstModelArrayList, Context context, OnItemClickListener listener) {
        this.dramaMovieFirstModelArrayList = dramaMovieFirstModelArrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WrongTitleAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_search_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WrongTitleAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        holder.rating_layout.setVisibility(View.GONE);
        holder.dramaTotalEpisodes.setVisibility(View.GONE);
        Glide.with(context).load(dramaMovieFirstModelArrayList.get(actualPosition).getDramaImage()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.thumbnailProgress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.image_not_available)
                .into(holder.dramaThumbnail);

        holder.dramaName.setText(dramaMovieFirstModelArrayList.get(actualPosition).getDramaName());

        holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actualPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(actualPosition); // Pass your data here
                }
            }
        });






    }

    @Override
    public int getItemCount() {
        return dramaMovieFirstModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        RelativeLayout relative_root_layout, rating_layout;
        ImageView dramaThumbnail;
        ProgressBar thumbnailProgress;
        TextView dramaName,dramaTotalEpisodes;
        public Holder(@NonNull View itemView) {
            super(itemView);

            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
            rating_layout = itemView.findViewById(R.id.rating_layout);
            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            dramaName = itemView.findViewById(R.id.dramaName);
            dramaTotalEpisodes = itemView.findViewById(R.id.dramaTotalEpisodes);



        }
    }
}
