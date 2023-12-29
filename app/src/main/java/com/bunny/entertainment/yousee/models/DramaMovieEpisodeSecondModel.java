package com.bunny.entertainment.yousee.models;

public class DramaMovieEpisodeSecondModel {

    public String episodeId, episodeName, seasonNoEpisodeNo, episodeImage, episodeReleaseDate, TotalEps;

    public DramaMovieEpisodeSecondModel() {
    }
    public DramaMovieEpisodeSecondModel(String episodeId, String episodeName, String seasonNoEpisodeNo, String episodeImage, String episodeReleaseDate, String totalEps) {
        this.episodeId = episodeId;
        this.episodeName = episodeName;
        this.seasonNoEpisodeNo = seasonNoEpisodeNo;
        this.episodeImage = episodeImage;
        this.episodeReleaseDate = episodeReleaseDate;
        TotalEps = totalEps;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public String getSeasonNoEpisodeNo() {
        return seasonNoEpisodeNo;
    }

    public void setSeasonNoEpisodeNo(String seasonNoEpisodeNo) {
        seasonNoEpisodeNo = seasonNoEpisodeNo;
    }

    public String getEpisodeImage() {
        return episodeImage;
    }

    public void setEpisodeImage(String episodeImage) {
        this.episodeImage = episodeImage;
    }

    public String getEpisodeReleaseDate() {
        return episodeReleaseDate;
    }

    public void setEpisodeReleaseDate(String episodeReleaseDate) {
        this.episodeReleaseDate = episodeReleaseDate;
    }

    public String getTotalEps() {
        return TotalEps;
    }

    public void setTotalEps(String totalEps) {
        TotalEps = totalEps;
    }
}
