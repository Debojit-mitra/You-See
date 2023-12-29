package com.bunny.entertainment.yousee.models.anime;

public class AnimeEpisodeModel {

    public String episodeId, episodeName, seasonNoEpisodeNo, episodeImage, TotalEps;
    boolean isFiller;
    public AnimeEpisodeModel() {
    }

    public AnimeEpisodeModel(String episodeId, String episodeName, String seasonNoEpisodeNo, String episodeImage, String totalEps) {
        this.episodeId = episodeId;
        this.episodeName = episodeName;
        this.seasonNoEpisodeNo = seasonNoEpisodeNo;
        this.episodeImage = episodeImage;
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
        this.seasonNoEpisodeNo = seasonNoEpisodeNo;
    }

    public String getEpisodeImage() {
        return episodeImage;
    }

    public void setEpisodeImage(String episodeImage) {
        this.episodeImage = episodeImage;
    }

    public String getTotalEps() {
        return TotalEps;
    }

    public void setTotalEps(String totalEps) {
        TotalEps = totalEps;
    }

    public boolean isFiller() {
        return isFiller;
    }

    public void setFiller(boolean filler) {
        isFiller = filler;
    }
}
