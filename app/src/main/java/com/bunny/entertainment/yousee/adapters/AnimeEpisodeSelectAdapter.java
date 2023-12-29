package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeLinkFinalModel;
import com.bunny.entertainment.yousee.player.PlayerActivity;
import com.bunny.entertainment.yousee.utils.DataHolder;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class AnimeEpisodeSelectAdapter extends RecyclerView.Adapter<AnimeEpisodeSelectAdapter.Holder> {
    ArrayList<AnimeEpisodeLinkFinalModel> episodeUrlsArraylist;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    String animeID, animeType, animeName;
    SharedPreferences animePreferences;
    boolean played = false;

    public AnimeEpisodeSelectAdapter(ArrayList<AnimeEpisodeLinkFinalModel> episodeUrlsArraylist, Context context, BottomSheetDialog bottomSheetDialog, String animeID, String animeType, String animeName) {
        this.episodeUrlsArraylist = episodeUrlsArraylist;
        this.context = context;
        this.bottomSheetDialog = bottomSheetDialog;
        this.animeID = animeID;
        this.animeType = animeType;
        this.animeName = animeName;
    }

    @NonNull
    @Override
    public AnimeEpisodeSelectAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.episode_link_layout, parent, false);
        animePreferences = context.getSharedPreferences("animePreferences", Context.MODE_PRIVATE);
        return new AnimeEpisodeSelectAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeEpisodeSelectAdapter.Holder holder, int position) {
        int actualPosition = holder.getAbsoluteAdapterPosition();

        String server = episodeUrlsArraylist.get(actualPosition).getSiteName()+" : "+episodeUrlsArraylist.get(actualPosition).getServerName();
        holder.textView_serverName.setText(server);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String previouslySelectedSource = animePreferences.getString(animeName + "Source", null);
                String savedServer = "";
                if(previouslySelectedSource != null){
                    savedServer = animePreferences.getString(animeID + previouslySelectedSource + "defaultSelectedServerIs", null);
                } else {
                    savedServer = animePreferences.getString(animeID + "Aniwatch(consumet)" + "defaultSelectedServerIs", null);
                }

                if(!played && savedServer != null){
                    if (episodeUrlsArraylist != null){
                        if (context != null) {
                            for (int i = 0; i < episodeUrlsArraylist.size(); i++){
                                String siteNameServerName = episodeUrlsArraylist.get(i).getSiteName() + episodeUrlsArraylist.get(i).getServerName();
                                if(savedServer.equals(siteNameServerName)){
                                    bottomSheetDialog.dismiss();
                                    Intent intent = new Intent(context, PlayerActivity.class);
                                    intent.putExtra("whereFrom", "AnimeFragment");
                                    if(episodeUrlsArraylist.get(i).getEpisodeLink() == null){
                                        intent.putExtra("videoLink","DataHolder");
                                        DataHolder.setAnimeEpisodeAndQualityModelArrayList(episodeUrlsArraylist.get(i).getEpisodeLinks());
                                    } else {
                                        intent.putExtra("videoLink", episodeUrlsArraylist.get(i).getEpisodeLink());
                                        intent.putExtra("subtitleLink", episodeUrlsArraylist.get(i).getSubtitleLink());
                                    }
                                    intent.putExtra("animeNameAndEpisodeNo", episodeUrlsArraylist.get(i).getAnimeName()+" - Episode "+episodeUrlsArraylist.get(i).getEpisodeNo());
                                    intent.putExtra("animeID", animeID);
                                    intent.putExtra("animeType", animeType);
                                    context.startActivity(intent);
                                    played = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        },1500);


        holder.main_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (context != null) {
                    String previouslySelectedSource = animePreferences.getString(animeName + "Source", null);
                    String dramaServerSelectedDefault = animePreferences.getString("animeServerSelectedDefault" + animeID, null);
                    if (dramaServerSelectedDefault != null) {
                        if (dramaServerSelectedDefault.contains("true")) {
                            if (previouslySelectedSource != null){
                                animePreferences.edit().putString(animeID + previouslySelectedSource.trim() + "defaultSelectedServerIs", episodeUrlsArraylist.get(actualPosition).getSiteName() + episodeUrlsArraylist.get(actualPosition).getServerName()).apply();
                            } else {
                                animePreferences.edit().putString(animeID + "Aniwatch(consumet)" + "defaultSelectedServerIs", episodeUrlsArraylist.get(actualPosition).getSiteName() + episodeUrlsArraylist.get(actualPosition).getServerName()).apply();
                            }
                        }
                    }
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("whereFrom", "AnimeFragment");
                    if(episodeUrlsArraylist.get(actualPosition).getEpisodeLink() == null){
                        intent.putExtra("videoLink","DataHolder");
                        DataHolder.setAnimeEpisodeAndQualityModelArrayList(episodeUrlsArraylist.get(actualPosition).getEpisodeLinks());
                    } else {
                        intent.putExtra("videoLink", episodeUrlsArraylist.get(actualPosition).getEpisodeLink());
                        intent.putExtra("subtitleLink", episodeUrlsArraylist.get(actualPosition).getSubtitleLink());
                    }
                    intent.putExtra("animeNameAndEpisodeNo", episodeUrlsArraylist.get(actualPosition).getAnimeName()+" - Episode "+episodeUrlsArraylist.get(actualPosition).getEpisodeNo());
                    intent.putExtra("animeID", animeID);
                    intent.putExtra("animeType", animeType);
                    context.startActivity(intent);
                    played = true;

                }
            }
        });



        /*holder.main_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context != null) {
                    String dramaServerSelectedDefault = animePreferences.getString("animeServerSelectedDefault"+animeID, null);
                    if(dramaServerSelectedDefault != null){
                        if(dramaServerSelectedDefault.contains("true")){
                            animePreferences.edit().putString(animeID+"defaultSelectedServerIs", episodeUrlsArraylist.get(actualPosition).getSiteName()+episodeUrlsArraylist.get(actualPosition).getServerName()).apply();
                        }
                    }
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(context, PlayerActivity.class);
                    intent.putExtra("whereFrom", "AnimeFragment");
                    intent.putExtra("videoLink", episodeUrlsArraylist.get(actualPosition).getEpisodeLink());
                    intent.putExtra("animeNameAndEpisodeNo", episodeUrlsArraylist.get(actualPosition).getAnimeName()+" - Episode "+episodeUrlsArraylist.get(actualPosition).getEpisodeNo());
                    intent.putExtra("animeID", animeID);
                    intent.putExtra("animeType", animeType);
                    context.startActivity(intent);
                    played = true;
                }
            }
        });*/


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
