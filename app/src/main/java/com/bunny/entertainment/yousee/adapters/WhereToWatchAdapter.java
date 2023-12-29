package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.WhereToWatchModel;

import java.util.ArrayList;

public class WhereToWatchAdapter extends RecyclerView.Adapter<WhereToWatchAdapter.Holder>{

    ArrayList<WhereToWatchModel> whereToWatchModelArrayList;
    Context context;

    public WhereToWatchAdapter() {
    }

    public WhereToWatchAdapter(ArrayList<WhereToWatchModel> whereToWatchModelArrayList, Context context) {
        this.whereToWatchModelArrayList = whereToWatchModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WhereToWatchAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.watch_here_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WhereToWatchAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        Glide.with(context).load(whereToWatchModelArrayList.get(actualPosition).getOttImageUrl()).error(R.drawable.image_not_available)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ottThumbnail);

        holder.textView_ottName.setText(whereToWatchModelArrayList.get(actualPosition).getOttName());
        if(whereToWatchModelArrayList.get(actualPosition).getOttSubscription().isEmpty()){
            holder.textView_ottSubscription.setVisibility(View.GONE);
        }else {
            holder.textView_ottSubscription.setText(whereToWatchModelArrayList.get(actualPosition).getOttSubscription());
        }

    }

    @Override
    public int getItemCount() {
        return whereToWatchModelArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        ImageView ottThumbnail;
        TextView textView_ottName, textView_ottSubscription;
        public Holder(@NonNull View itemView) {
            super(itemView);

            ottThumbnail = itemView.findViewById(R.id.ottThumbnail);
            textView_ottName = itemView.findViewById(R.id.textView_ottName);
            textView_ottSubscription = itemView.findViewById(R.id.textView_ottSubscription);
        }
    }
}
