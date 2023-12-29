package com.bunny.entertainment.yousee.models;

import com.bunny.entertainment.yousee.models.anime.AnimeEpisodeModel;

import java.util.ArrayList;

public class EpisodesGroups {

    public String startEP, endEP;
    public ArrayList<DramaMovieEpisodeSecondModel> episodeGroupList;


    public EpisodesGroups() {
    }

    public EpisodesGroups(String startEP, String endEP, ArrayList<DramaMovieEpisodeSecondModel> episodeGroupList) {
        this.startEP = startEP;
        this.endEP = endEP;
        this.episodeGroupList = episodeGroupList;
    }

    public String getStartEP() {
        return startEP;
    }

    public void setStartEP(String startEP) {
        this.startEP = startEP;
    }

    public String getEndEP() {
        return endEP;
    }

    public void setEndEP(String endEP) {
        this.endEP = endEP;
    }

    public ArrayList<DramaMovieEpisodeSecondModel> getEpisodeGroupList() {
        return episodeGroupList;
    }

    public void setEpisodeGroupList(ArrayList<DramaMovieEpisodeSecondModel> episodeGroupList) {
        this.episodeGroupList = episodeGroupList;
    }
}
