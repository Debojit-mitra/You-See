package com.bunny.entertainment.yousee.models;

public class DramaMovieEpisodeThridModel {

    public String episodeLink, serverName, dramaName, episodeNo;

    public DramaMovieEpisodeThridModel() {
    }

    public DramaMovieEpisodeThridModel(String episodeLink, String serverName, String dramaName, String episodeNo) {
        this.episodeLink = episodeLink;
        this.serverName = serverName;
        this.dramaName = dramaName;
        this.episodeNo = episodeNo;
    }

    public String getEpisodeLink() {
        return episodeLink;
    }

    public void setEpisodeLink(String episodeLink) {
        this.episodeLink = episodeLink;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getDramaName() {
        return dramaName;
    }

    public void setDramaName(String dramaName) {
        this.dramaName = dramaName;
    }

    public String getEpisodeNo() {
        return episodeNo;
    }

    public void setEpisodeNo(String episodeNo) {
        this.episodeNo = episodeNo;
    }
}
