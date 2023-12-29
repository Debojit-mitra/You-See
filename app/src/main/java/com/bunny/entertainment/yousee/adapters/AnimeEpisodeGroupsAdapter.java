package com.bunny.entertainment.yousee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunny.entertainment.yousee.R;
import com.bunny.entertainment.yousee.models.EpisodesGroups;
import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeGroups;

import java.util.ArrayList;

public class AnimeEpisodeGroupsAdapter extends RecyclerView.Adapter<AnimeEpisodeGroupsAdapter.Holder> {

    Context context;
    ArrayList<AnimeEpisodeGroups> episodesGroupsArrayList;
    private OnButtonClickListener buttonClickListener;


    public AnimeEpisodeGroupsAdapter(Context context, ArrayList<AnimeEpisodeGroups> episodesGroupsArrayList, OnButtonClickListener buttonClickListener) {
        this.context = context;
        this.episodesGroupsArrayList = episodesGroupsArrayList;
        this.buttonClickListener = buttonClickListener;
    }

    @NonNull
    @Override
    public AnimeEpisodeGroupsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.episode_groups_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeEpisodeGroupsAdapter.Holder holder, int position) {
        int actualPosition = holder.getAbsoluteAdapterPosition();


        int startEP = Integer.parseInt(episodesGroupsArrayList.get(actualPosition).getStartEP()) + 1;
        holder.startEP.setText(String.valueOf(startEP));
        holder.endEP.setText(episodesGroupsArrayList.get(actualPosition).getEndEP());

        holder.main_group_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonClickListener != null) {
                    buttonClickListener.onButtonClicked(episodesGroupsArrayList.get(actualPosition).getStartEP(), episodesGroupsArrayList.get(actualPosition).getEndEP());
                }
            }
        });

    }

    public interface OnButtonClickListener {
        void onButtonClicked(String startEP, String endEP);
    }

    @Override
    public int getItemCount() {
        return episodesGroupsArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        TextView startEP, endEP;
        LinearLayout main_group_button;

        public Holder(@NonNull View itemView) {
            super(itemView);
            startEP = itemView.findViewById(R.id.startEP);
            endEP = itemView.findViewById(R.id.endEP);
            main_group_button = itemView.findViewById(R.id.main_group_button);
        }
    }

    public void performClick(View view, int pos) {
        if (buttonClickListener != null) {
            buttonClickListener.onButtonClicked(episodesGroupsArrayList.get(pos).getStartEP(), episodesGroupsArrayList.get(pos).getEndEP());
        }
    }
}
