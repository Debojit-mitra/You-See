package com.bunny.entertainment.yousee.models;

public class GenresModel {

    public String genreName, genreLink;

    public GenresModel() {
    }

    public GenresModel(String genreName, String genreLink) {
        this.genreName = genreName;
        this.genreLink = genreLink;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public String getGenreLink() {
        return genreLink;
    }

    public void setGenreLink(String genreLink) {
        this.genreLink = genreLink;
    }
}
