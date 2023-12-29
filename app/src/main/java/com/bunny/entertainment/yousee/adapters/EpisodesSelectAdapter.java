package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.fragments.WatchFragment;
import com.bunny.entertainment.yousee.models.DramaMovieEpisodeThridModel;
import com.bunny.entertainment.yousee.player.PlayerActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class EpisodesSelectAdapter extends RecyclerView.Adapter<EpisodesSelectAdapter.Holder> {

    ArrayList<DramaMovieEpisodeThridModel> episodeUrlsArraylist;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    String dramaIdMyDramaList, dramaType;
    SharedPreferences dramaPreferences;
    boolean played = false;

    public EpisodesSelectAdapter(ArrayList<DramaMovieEpisodeThridModel> episodeUrlsArraylist, Context context, BottomSheetDialog bottomSheetDialog, String dramaIdMyDramaList, String dramaType) {
        this.episodeUrlsArraylist = episodeUrlsArraylist;
        this.context = context;
        this.bottomSheetDialog = bottomSheetDialog;
        this.dramaIdMyDramaList = dramaIdMyDramaList;
        this.dramaType = dramaType;
    }

    @NonNull
    @Override
    public EpisodesSelectAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.episode_link_layout, parent, false);
        dramaPreferences = context.getSharedPreferences("dramaPreferences", Context.MODE_PRIVATE);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesSelectAdapter.Holder holder, int position) {

        int actualPosition = holder.getAbsoluteAdapterPosition();

        holder.textView_serverName.setText(episodeUrlsArraylist.get(actualPosition).serverName);


       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
               if(!played && dramaPreferences.getString(dramaIdMyDramaList+"defaultSelectedServerIs", null) != null){
                   if (episodeUrlsArraylist != null){
                       if (context != null) {
                           bottomSheetDialog.dismiss();
                           Intent intent = new Intent(context, PlayerActivity.class);
                           intent.putExtra("whereFrom", "DramaFragment");
                           intent.putExtra("videoLink", episodeUrlsArraylist.get(actualPosition).getEpisodeLink());
                           intent.putExtra("dramaNameAndEpisodeNo", episodeUrlsArraylist.get(actualPosition).getDramaName()+" - Episode "+episodeUrlsArraylist.get(actualPosition).getEpisodeNo());
                           intent.putExtra("dramaIdMyDramaList", dramaIdMyDramaList);
                           intent.putExtra("dramaType", dramaType);
                           context.startActivity(intent);
                       }
                       played = true;
                   }
               }
           }
       },1500);

        holder.main_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context != null) {
                    String dramaServerSelectedDefault = dramaPreferences.getString("dramaServerSelectedDefault"+dramaIdMyDramaList, null);
                    if(dramaServerSelectedDefault != null){
                        if(dramaServerSelectedDefault.contains("true")){
                            dramaPreferences.edit().putString(dramaIdMyDramaList+"defaultSelectedServerIs", episodeUrlsArraylist.get(actualPosition).getServerName()).apply();
                        }
                    }
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("whereFrom", "DramaFragment");
                    intent.putExtra("videoLink", episodeUrlsArraylist.get(actualPosition).getEpisodeLink());
                    intent.putExtra("dramaNameAndEpisodeNo", episodeUrlsArraylist.get(actualPosition).getDramaName()+" - Episode "+episodeUrlsArraylist.get(actualPosition).getEpisodeNo());
                    intent.putExtra("dramaIdMyDramaList", dramaIdMyDramaList);
                    intent.putExtra("dramaType", dramaType);
                    context.startActivity(intent);
                    played = true;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return episodeUrlsArraylist.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView textView_sourceName, textView_serverName;
        RelativeLayout main_relative_layout;

        public Holder(@NonNull View itemView) {
            super(itemView);
            //   textView_sourceName = itemView.findViewById(R.id.textView_sourceName);
            textView_serverName = itemView.findViewById(R.id.textView_serverName);
            main_relative_layout = itemView.findViewById(R.id.main_relative_layout);
        }
    }

}
