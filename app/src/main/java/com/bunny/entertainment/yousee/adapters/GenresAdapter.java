package com.bunny.entertainment.yousee.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.models.GenresModel;
import com.bunny.entertainment.yousee.utils.DataHolder;

import java.util.ArrayList;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class GenresAdapter extends RecyclerView.Adapter<GenresAdapter.Holder>{

    ArrayList<GenresModel> genresModelArrayList;
    Context context;

    public GenresAdapter() {
    }

    public GenresAdapter(ArrayList<GenresModel> genresModelArrayList, Context context) {
        this.genresModelArrayList = genresModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public GenresAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_genres_layout2, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenresAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        holder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));

        Random random = new Random();
        int randomNumber = random.nextInt(20);
        Glide.with(context)
                .load(DataHolder.getTopAiringDramaListModelArrayList().get(randomNumber).getDramaThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new BlurTransformation( 8, 2))
                .into(holder.dramaThumbnail);

        holder.textView_genres.setText(genresModelArrayList.get(actualPosition).genreName);

        holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("whereFrom", "Genre Adapter");
                intent.putExtra("genreName", genresModelArrayList.get(actualPosition).getGenreName());
                intent.putExtra("genreLink", genresModelArrayList.get(actualPosition).getGenreLink());
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });



    }

    @Override
    public int getItemCount() {
        return genresModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView dramaThumbnail;
        TextView textView_genres;
        LinearLayout relative_root_layout;
        public Holder(@NonNull View itemView) {
            super(itemView);

            dramaThumbnail = itemView.findViewById(R.id.dramaThumbnail);
            textView_genres = itemView.findViewById(R.id.textView_genres);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);

        }
    }
}
