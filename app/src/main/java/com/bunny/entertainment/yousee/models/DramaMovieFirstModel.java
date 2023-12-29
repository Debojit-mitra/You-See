package com.bunny.entertainment.yousee.models;

public class DramaMovieFirstModel {

    public String dramaId, dramaName, dramaImage;

    public DramaMovieFirstModel() {
    }

    public DramaMovieFirstModel(String dramaId, String dramaName, String dramaImage) {
        this.dramaId = dramaId;
        this.dramaName = dramaName;
        this.dramaImage = dramaImage;
    }

    public String getDramaId() {
        return dramaId;
    }

    public void setDramaId(String dramaId) {
        this.dramaId = dramaId;
    }

    public String getDramaName() {
        return dramaName;
    }

    public void setDramaName(String dramaName) {
        this.dramaName = dramaName;
    }

    public String getDramaImage() {
        return dramaImage;
    }

    public void setDramaImage(String dramaImage) {
        this.dramaImage = dramaImage;
    }
}
