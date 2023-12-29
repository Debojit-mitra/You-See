package com.bunny.entertainment.yousee.models.anime;

import java.util.ArrayList;

public class AnimeListModel {

    String animeTitle, malID, animeRating, animeThumbnail, animeEpisodes;
    ArrayList<String> genresList;
    int totalAnime;
    String firstReleaseDate;
    public AnimeListModel() {
    }

    public AnimeListModel(String animeTitle, String malID, String animeRating, String animeThumbnail, String animeEpisodes, ArrayList<String> genresList,  int totalAnime) {
        this.animeTitle = animeTitle;
        this.malID = malID;
        this.animeRating = animeRating;
        this.animeThumbnail = animeThumbnail;
        this.animeEpisodes = animeEpisodes;
        this.genresList = genresList;
        this.totalAnime = totalAnime;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public String getMalID() {
        return malID;
    }

    public void setMalID(String malID) {
        this.malID = malID;
    }

    public String getAnimeRating() {
        return animeRating;
    }

    public void setAnimeRating(String animeRating) {
        this.animeRating = animeRating;
    }

    public String getAnimeThumbnail() {
        return animeThumbnail;
    }

    public void setAnimeThumbnail(String animeThumbnail) {
        this.animeThumbnail = animeThumbnail;
    }

    public String getAnimeEpisodes() {
        return animeEpisodes;
    }

    public void setAnimeEpisodes(String animeEpisodes) {
        this.animeEpisodes = animeEpisodes;
    }

    public ArrayList<String> getGenresList() {
        return genresList;
    }

    public void setGenresList(ArrayList<String> genresList) {
        this.genresList = genresList;
    }

    public int getTotalAnime() {
        return totalAnime;
    }

    public void setTotalAnime(int totalAnime) {
        this.totalAnime = totalAnime;
    }

    public String getFirstReleaseDate() {
        return firstReleaseDate;
    }

    public void setFirstReleaseDate(String firstReleaseDate) {
        this.firstReleaseDate = firstReleaseDate;
    }
}
