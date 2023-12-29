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
import com.bunny.entertainment.yousee.activities.PeopleDetailsActivity;
import com.bunny.entertainment.yousee.activities.ViewAllActivity;
import com.bunny.entertainment.yousee.models.CastListModel;

import java.util.ArrayList;

public class CastAdapter extends RecyclerView.Adapter<CastAdapter.Holder>{

    ArrayList<CastListModel> castListModelArrayList;
    Context context;
    String checkWhereFrom;

    public CastAdapter(ArrayList<CastListModel> castListModelArrayList, Context context, String checkWhereFrom) {
        this.castListModelArrayList = castListModelArrayList;
        this.context = context;
        this.checkWhereFrom = checkWhereFrom;
    }

    @NonNull
    @Override
    public CastAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.drama_layout_3, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        if(checkWhereFrom.equals("fromViewAllActivity")){
            holder.relative_root_layout.startAnimation(AnimationUtils.loadAnimation(holder.relative_root_layout.getContext(), R.anim.recyclerviewscroll_top_bottom));
        }

        Glide.with(context).load(castListModelArrayList.get(actualPosition).getCastImage()).diskCacheStrategy(DiskCacheStrategy.ALL).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.thumbnailProgress.setVisibility(View.GONE);
                return false;
            }
        }).error(R.drawable.image_not_available).into(holder.castThumbnail);


        holder.textView_castRealName.setText(castListModelArrayList.get(actualPosition).getCastRealName());
        if(!castListModelArrayList.get(actualPosition).getCastCharacterName().isEmpty()){
            holder.textView_castCharacterName.setText(castListModelArrayList.get(actualPosition).getCastCharacterName());
        }else {
            holder.textView_castCharacterName.setVisibility(View.GONE);
        }
        holder.textView_castRole.setText(castListModelArrayList.get(actualPosition).getCastRole());

        if(castListModelArrayList.get(actualPosition).getCastId() != null){
            holder.relative_root_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PeopleDetailsActivity.class);
                    intent.putExtra("castId", castListModelArrayList.get(actualPosition).getCastId());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return castListModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView castThumbnail;
        TextView textView_castRealName, textView_castCharacterName, textView_castRole;
        ProgressBar thumbnailProgress;
        RelativeLayout relative_root_layout;
        public Holder(@NonNull View itemView) {
            super(itemView);

            castThumbnail = itemView.findViewById(R.id.castThumbnail);
            textView_castRealName = itemView.findViewById(R.id.textView_castRealName);
            textView_castCharacterName = itemView.findViewById(R.id.textView_castCharacterName);
            textView_castRole = itemView.findViewById(R.id.textView_castRole);
            thumbnailProgress = itemView.findViewById(R.id.thumbnailProgress);
            relative_root_layout = itemView.findViewById(R.id.relative_root_layout);
        }
    }
}
