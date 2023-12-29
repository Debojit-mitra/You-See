package com.bunny.entertainment.yousee.models.anime;

import java.util.ArrayList;

public class AnimeEpisodeLinkFinalModel {

    public String animeName, episodeLink, serverName, siteName, episodeNo, subtitleLink;
    public ArrayList<AnimeEpisodeAndQualityModel> episodeLinks;
    public AnimeEpisodeLinkFinalModel(String animeName, String episodeLink, String serverName, String siteName, String episodeNo, String subtitleLink) {
        this.animeName = animeName;
        this.episodeLink = episodeLink;
        this.serverName = serverName;
        this.siteName = siteName;
        this.episodeNo = episodeNo;
        this.subtitleLink = subtitleLink;
    }

    public AnimeEpisodeLinkFinalModel(String animeName, ArrayList<AnimeEpisodeAndQualityModel> episodeLinks, String serverName, String siteName, String episodeNo, String subtitleLink) {
        this.animeName = animeName;
        this.episodeLinks = episodeLinks;
        this.serverName = serverName;
        this.siteName = siteName;
        this.episodeNo = episodeNo;
        this.subtitleLink = subtitleLink;
    }

    public String getAnimeName() {
        return animeName;
    }

    public void setAnimeName(String animeName) {
        this.animeName = animeName;
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

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getEpisodeNo() {
        return episodeNo;
    }

    public void setEpisodeNo(String episodeNo) {
        this.episodeNo = episodeNo;
    }

    public String getSubtitleLink() {
        return subtitleLink;
    }

    public void setSubtitleLink(String subtitleLink) {
        this.subtitleLink = subtitleLink;
    }

    public ArrayList<AnimeEpisodeAndQualityModel> getEpisodeLinks() {
        return episodeLinks;
    }

    public void setEpisodeLinks(ArrayList<AnimeEpisodeAndQualityModel> episodeLinks) {
        this.episodeLinks = episodeLinks;
    }
}
