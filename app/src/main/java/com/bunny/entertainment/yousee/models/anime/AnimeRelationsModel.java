package com.bunny.entertainment.yousee.models.anime;

public class AnimeRelationsModel {

    String myAnimeListID, animeRelationType, animeTitle, animeImage, animeEpisodes, animeRating;
    public AnimeRelationsModel() {
    }
    public AnimeRelationsModel(String myAnimeListID, String animeRelationType, String animeTitle) {
        this.myAnimeListID = myAnimeListID;
        this.animeRelationType = animeRelationType;
        this.animeTitle = animeTitle;
    }

    public String getMyAnimeListID() {
        return myAnimeListID;
    }

    public void setMyAnimeListID(String myAnimeListID) {
        this.myAnimeListID = myAnimeListID;
    }

    public String getAnimeRelationType() {
        return animeRelationType;
    }

    public void setAnimeRelationType(String animeRelationType) {
        this.animeRelationType = animeRelationType;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public String getAnimeImage() {
        return animeImage;
    }

    public void setAnimeImage(String animeImage) {
        this.animeImage = animeImage;
    }

    public String getAnimeEpisodes() {
        return animeEpisodes;
    }

    public void setAnimeEpisodes(String animeEpisodes) {
        this.animeEpisodes = animeEpisodes;
    }

    public String getAnimeRating() {
        return animeRating;
    }

    public void setAnimeRating(String animeRating) {
        this.animeRating = animeRating;
    }
}
