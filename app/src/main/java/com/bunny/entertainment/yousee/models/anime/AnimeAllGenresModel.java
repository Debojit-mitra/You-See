package com.bunny.entertainment.yousee.models.anime;

public class AnimeAllGenresModel {

    String name, link;
    int totalAnime;
    public AnimeAllGenresModel() {
    }
    public AnimeAllGenresModel(String name, String link, int totalAnime) {
        this.name = name;
        this.link = link;
        this.totalAnime = totalAnime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getTotalAnime() {
        return totalAnime;
    }

    public void setTotalAnime(int totalAnime) {
        this.totalAnime = totalAnime;
    }
}
