package com.bunny.entertainment.yousee.models.anime;

public class AnimeDetailsModel {

    String aniListID, animeImage, animeTitle, airingDetails, animeRating, animeType, animeSeasonTotalEP, animeGenres, animeStudio;

    public AnimeDetailsModel() {
    }

    public AnimeDetailsModel(String aniListID, String animeImage, String animeTitle, String airingDetails, String animeRating, String animeType, String animeSeasonTotalEP, String animeGenres, String animeStudio) {
        this.aniListID = aniListID;
        this.animeImage = animeImage;
        this.animeTitle = animeTitle;
        this.airingDetails = airingDetails;
        this.animeRating = animeRating;
        this.animeType = animeType;
        this.animeSeasonTotalEP = animeSeasonTotalEP;
        this.animeGenres = animeGenres;
        this.animeStudio = animeStudio;
    }

    public String getAniListID() {
        return aniListID;
    }

    public void setAniListID(String aniListID) {
        this.aniListID = aniListID;
    }

    public String getAnimeImage() {
        return animeImage;
    }

    public void setAnimeImage(String animeImage) {
        this.animeImage = animeImage;
    }

    public String getAnimeTitle() {
        return animeTitle;
    }

    public void setAnimeTitle(String animeTitle) {
        this.animeTitle = animeTitle;
    }

    public String getAiringDetails() {
        return airingDetails;
    }

    public void setAiringDetails(String airingDetails) {
        this.airingDetails = airingDetails;
    }

    public String getAnimeRating() {
        return animeRating;
    }

    public void setAnimeRating(String animeRating) {
        this.animeRating = animeRating;
    }

    public String getAnimeType() {
        return animeType;
    }

    public void setAnimeType(String animeType) {
        this.animeType = animeType;
    }

    public String getAnimeSeasonTotalEP() {
        return animeSeasonTotalEP;
    }

    public void setAnimeSeasonTotalEP(String animeSeasonTotalEP) {
        this.animeSeasonTotalEP = animeSeasonTotalEP;
    }

    public String getAnimeGenres() {
        return animeGenres;
    }

    public void setAnimeGenres(String animeGenres) {
        this.animeGenres = animeGenres;
    }

    public String getAnimeStudio() {
        return animeStudio;
    }

    public void setAnimeStudio(String animeStudio) {
        this.animeStudio = animeStudio;
    }
}
