package com.bunny.entertainment.yousee.models.anime;

import java.util.ArrayList;

public class AnimeEpisodeGroups {

    public String startEP, endEP;
    public ArrayList<AnimeEpisodeModel> episodeGroupListAnime;

    public AnimeEpisodeGroups(String startEP, String endEP, ArrayList<AnimeEpisodeModel> episodeGroupListAnime) {
        this.startEP = startEP;
        this.endEP = endEP;
        this.episodeGroupListAnime = episodeGroupListAnime;
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

    public ArrayList<AnimeEpisodeModel> getEpisodeGroupListAnime() {
        return episodeGroupListAnime;
    }

    public void setEpisodeGroupListAnime(ArrayList<AnimeEpisodeModel> episodeGroupListAnime) {
        this.episodeGroupListAnime = episodeGroupListAnime;
    }
}
