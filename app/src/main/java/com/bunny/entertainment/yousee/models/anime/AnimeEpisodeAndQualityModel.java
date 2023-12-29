package com.bunny.entertainment.yousee.models.anime;

public class AnimeEpisodeAndQualityModel {
    public String epQuality, epLink;

    public AnimeEpisodeAndQualityModel(String epQuality, String epLink) {
        this.epQuality = epQuality;
        this.epLink = epLink;
    }

    public String getEpQuality() {
        return epQuality;
    }

    public void setEpQuality(String epQuality) {
        this.epQuality = epQuality;
    }

    public String getEpLink() {
        return epLink;
    }

    public void setEpLink(String epLink) {
        this.epLink = epLink;
    }
}
